package fun.derjxnnik.container;

import fun.derjxnnik.misc.Colors;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Chest.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ContainerListener implements Listener {
   private final LockManager lockManager;
   private final ContainerCommand containerCommand;
   private static final Set CONTAINER_BLOCKS;

   public ContainerListener(JavaPlugin plugin, LockManager lockManager, ContainerCommand containerCommand) {
      this.lockManager = lockManager;
      this.containerCommand = containerCommand;
      plugin.getServer().getPluginManager().registerEvents(this, plugin);
   }

   private List resolveLinkedLocations(Block block) {
      List<Location> list = new ArrayList(2);
      list.add(block.getLocation());
      if (!this.isLockableChest(block.getType())) {
         return list;
      } else {
         BlockData var4 = block.getBlockData();
         if (var4 instanceof Chest) {
            Chest data = (Chest)var4;
            Chest.Type type = data.getType();
            if (type == Type.LEFT || type == Type.RIGHT) {
               if (type == Type.LEFT) {
                  data.getFacing().getOppositeFace().getOppositeFace();
               } else {
                  data.getFacing().getOppositeFace();
               }

               BlockFace var10000;
               switch (data.getFacing()) {
                  case NORTH -> var10000 = type == Type.LEFT ? BlockFace.EAST : BlockFace.WEST;
                  case SOUTH -> var10000 = type == Type.LEFT ? BlockFace.WEST : BlockFace.EAST;
                  case WEST -> var10000 = type == Type.LEFT ? BlockFace.NORTH : BlockFace.SOUTH;
                  case EAST -> var10000 = type == Type.LEFT ? BlockFace.SOUTH : BlockFace.NORTH;
                  default -> var10000 = null;
               }

               BlockFace otherFace = var10000;
               if (otherFace != null) {
                  Block neighbor = block.getRelative(otherFace);
                  if (this.isSameChestFamily(block.getType(), neighbor.getType())) {
                     BlockData neighborData = neighbor.getBlockData();
                     if (neighborData instanceof Chest) {
                        Chest neighborChest = (Chest)neighborData;
                        if (neighborChest.getType() != Type.SINGLE) {
                           list.add(neighbor.getLocation());
                        }
                     }
                  }
               }
            }
         }

         return list;
      }
   }

   private boolean isLockableChest(Material type) {
      boolean var10000;
      switch (type) {
         case CHEST:
         case TRAPPED_CHEST:
         case COPPER_CHEST:
         case EXPOSED_COPPER_CHEST:
         case WEATHERED_COPPER_CHEST:
         case OXIDIZED_COPPER_CHEST:
         case WAXED_COPPER_CHEST:
         case WAXED_EXPOSED_COPPER_CHEST:
         case WAXED_WEATHERED_COPPER_CHEST:
         case WAXED_OXIDIZED_COPPER_CHEST:
            var10000 = true;
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   private boolean isSameChestFamily(Material a, Material b) {
      if (a == b) {
         return true;
      } else if ((a != Material.CHEST || b != Material.TRAPPED_CHEST) && (a != Material.TRAPPED_CHEST || b != Material.CHEST)) {
         Set<Material> copperFamily = Set.of(Material.COPPER_CHEST, Material.EXPOSED_COPPER_CHEST, Material.WEATHERED_COPPER_CHEST, Material.OXIDIZED_COPPER_CHEST, Material.WAXED_COPPER_CHEST, Material.WAXED_EXPOSED_COPPER_CHEST, Material.WAXED_WEATHERED_COPPER_CHEST, Material.WAXED_OXIDIZED_COPPER_CHEST);
         return copperFamily.contains(a) && copperFamily.contains(b);
      } else {
         return false;
      }
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent e) {
      if (e.getClickedBlock() != null) {
         Block block = e.getClickedBlock();
         if (CONTAINER_BLOCKS.contains(block.getType())) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            List<Location> targets = this.resolveLinkedLocations(block);
            if (this.containerCommand.hasPendingAction(uuid)) {
               e.setCancelled(true);
               ContainerCommand.LockAction action = this.containerCommand.getPendingAction(uuid);
               switch (action) {
                  case LOCK:
                     this.lockManager.lockMany(targets, player);
                     break;
                  case UNLOCK:
                     this.lockManager.unlockMany(targets, player);
                     break;
                  case TRUST:
                     this.lockManager.trustMany(targets, player, this.containerCommand.getTarget(uuid));
                     break;
                  case UNTRUST:
                     this.lockManager.untrustMany(targets, player, this.containerCommand.getTarget(uuid));
                     break;
                  case TRANSFER:
                     this.lockManager.transferMany(targets, player, this.containerCommand.getTarget(uuid));
                     break;
                  case INFO:
                     Stream var13 = targets.stream();
                     LockManager var15 = this.lockManager;
                     Objects.requireNonNull(var15);
                     boolean anyLocked = var13.anyMatch(var15::isLocked);
                     if (!anyLocked) {
                        player.sendMessage(Colors.PREFIX + Colors.GRAY + "This container is not locked.");
                     } else {
                        var13 = targets.stream();
                        var15 = this.lockManager;
                        Objects.requireNonNull(var15);
                        Location ref = (Location)var13.filter(var15::isLocked).findFirst().orElse((Location)targets.get(0));
                        String owner = this.lockManager.getOwner(ref);
                        List<String> trusted = this.lockManager.getTrustedDisplay(ref);
                        player.sendMessage(Colors.PREFIX + Colors.GRAY + "Owner: " + Colors.YELLOW + owner);
                        String var17 = Colors.PREFIX;
                        player.sendMessage(var17 + Colors.GRAY + "Trusted: " + (trusted.isEmpty() ? Colors.YELLOW + "none" : Colors.YELLOW + String.join(", ", trusted)));
                     }
               }

               this.containerCommand.clearPendingAction(uuid);
            } else {
               Stream var10000 = targets.stream();
               LockManager var10001 = this.lockManager;
               Objects.requireNonNull(var10001);
               boolean locked = var10000.anyMatch(var10001::isLocked);
               if (locked) {
                  boolean trusted = targets.stream().anyMatch((loc) -> this.lockManager.isTrusted(loc, player.getName()));
                  if (!trusted) {
                     e.setCancelled(true);
                     player.sendActionBar(Colors.RED + "This container is locked.");
                  }
               }

            }
         }
      }
   }

   @EventHandler
   public void onBreak(BlockBreakEvent e) {
      Block block = e.getBlock();
      if (CONTAINER_BLOCKS.contains(block.getType())) {
         List<Location> targets = this.resolveLinkedLocations(block);
         Stream var10000 = targets.stream();
         LockManager var10001 = this.lockManager;
         Objects.requireNonNull(var10001);
         boolean locked = var10000.anyMatch(var10001::isLocked);
         if (locked) {
            boolean allOwned = targets.stream().allMatch((loc) -> this.lockManager.isOwner(loc, e.getPlayer().getName()));
            if (allOwned) {
               var10001 = this.lockManager;
               Objects.requireNonNull(var10001);
               targets.forEach(var10001::removeLockCompletely);
            } else {
               e.setCancelled(true);
               e.getPlayer().sendActionBar(Colors.RED + "You cannot break a locked container.");
            }
         }
      }
   }

   @EventHandler
   public void onExplosion(EntityExplodeEvent e) {
      Iterator<Block> it = e.blockList().iterator();

      while(it.hasNext()) {
         Block block = (Block)it.next();
         if (CONTAINER_BLOCKS.contains(block.getType())) {
            List<Location> targets = this.resolveLinkedLocations(block);
            Stream var10000 = targets.stream();
            LockManager var10001 = this.lockManager;
            Objects.requireNonNull(var10001);
            if (var10000.anyMatch(var10001::isLocked)) {
               it.remove();
            }
         }
      }

   }

   @EventHandler
   public void onHopperMove(InventoryMoveItemEvent e) {
      if (e.getSource().getLocation() != null) {
         if (this.lockManager.isLocked(e.getSource().getLocation())) {
            e.setCancelled(true);
         }

      }
   }

   static {
      CONTAINER_BLOCKS = EnumSet.of(Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL, Material.SHULKER_BOX, Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.DROPPER, Material.DISPENSER, Material.HOPPER, Material.CHISELED_BOOKSHELF, Material.DECORATED_POT, Material.OAK_SHELF, Material.SPRUCE_SHELF, Material.BIRCH_SHELF, Material.JUNGLE_SHELF, Material.ACACIA_SHELF, Material.DARK_OAK_SHELF, Material.MANGROVE_SHELF, Material.BAMBOO_SHELF, Material.CHERRY_SHELF, Material.CRIMSON_SHELF, Material.WARPED_SHELF, Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, Material.COPPER_CHEST, Material.EXPOSED_COPPER_CHEST, Material.WEATHERED_COPPER_CHEST, Material.OXIDIZED_COPPER_CHEST, Material.WAXED_COPPER_CHEST, Material.WAXED_EXPOSED_COPPER_CHEST, Material.WAXED_WEATHERED_COPPER_CHEST, Material.WAXED_OXIDIZED_COPPER_CHEST);
   }
}
