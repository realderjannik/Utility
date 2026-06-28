package fun.derjxnnik.listeners;

import fun.derjxnnik.misc.StairsUtil;
import fun.derjxnnik.settings.SettingsManager;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class SitListener implements Listener {
   private final SettingsManager settings;

   public SitListener(SettingsManager settings) {
      this.settings = settings;
   }

   @EventHandler
   public void onClick(PlayerInteractEvent e) {
      if (e.getClickedBlock() != null) {
         if (!e.getAction().isLeftClick()) {
            final Player p = e.getPlayer();
            Block block = e.getClickedBlock();
            if (this.settings.isSittingEnabled(p)) {
               if (StairsUtil.isStair(block.getType())) {
                  Stairs data = (Stairs)block.getBlockData();
                  if (data.getHalf() == Half.BOTTOM) {
                     Location loc = block.getLocation().add((double)0.5F, 0.4, (double)0.5F);
                     World w = p.getWorld();
                     float var10000;
                     switch (data.getFacing()) {
                        case NORTH -> var10000 = 180.0F;
                        case SOUTH -> var10000 = 0.0F;
                        case WEST -> var10000 = 90.0F;
                        case EAST -> var10000 = -90.0F;
                        default -> var10000 = 0.0F;
                     }

                     float yaw = var10000;
                     loc.setYaw(yaw);
                     final ArmorStand seat = (ArmorStand)w.spawnEntity(loc, EntityType.ARMOR_STAND);
                     seat.setInvisible(true);
                     seat.setMarker(true);
                     seat.setGravity(false);
                     seat.setInvulnerable(true);
                     seat.setSilent(true);
                     seat.setSmall(true);
                     seat.setRotation(yaw, 0.0F);
                     seat.addPassenger(p);
                     seat.setMetadata("sitBlock", new FixedMetadataValue(Utility.getInstance(), block.getLocation()));
                     p.playSound(p.getLocation(), Sound.BLOCK_WOOD_STEP, 0.7F, 1.2F);
                     (new BukkitRunnable() {
                        public void run() {
                           if (seat.isValid() && seat.getPassengers().contains(p)) {
                              seat.setRotation(p.getLocation().getYaw(), 0.0F);
                           } else {
                              this.cancel();
                           }
                        }
                     }).runTaskTimer(Utility.getInstance(), 0L, 2L);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onSneak(PlayerToggleSneakEvent e) {
      Player p = e.getPlayer();
      if (e.isSneaking()) {
         Entity var4 = p.getVehicle();
         if (var4 instanceof ArmorStand) {
            ArmorStand seat = (ArmorStand)var4;
            seat.remove();
            p.playSound(p.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1.0F, 1.3F);
         }

      }
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent e) {
      Block broken = e.getBlock();
      World world = broken.getWorld();

      for(ArmorStand stand : world.getEntitiesByClass(ArmorStand.class)) {
         if (stand.hasMetadata("sitBlock")) {
            Location sitLoc = (Location)((MetadataValue)stand.getMetadata("sitBlock").get(0)).value();
            if (sitLoc.distance(broken.getLocation()) < 0.3) {
               stand.eject();
               stand.remove();
               world.playSound(broken.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7F, 1.2F);
            }
         }
      }

   }
}
