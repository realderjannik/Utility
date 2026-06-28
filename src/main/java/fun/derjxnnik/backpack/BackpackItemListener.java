package fun.derjxnnik.backpack;

import fun.derjxnnik.utility.Utility;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.scheduler.BukkitScheduler;

public class BackpackItemListener implements Listener {
   private final BackpackManager bpManager;
   private final Utility plugin;
   private final int backpackSlots;
   private static final int BUNDLE_PLAYER_SLOT = 9;

   public BackpackItemListener(BackpackManager bpManager, Utility plugin) {
      this.bpManager = bpManager;
      this.plugin = plugin;
      this.backpackSlots = plugin.getConfig().getInt("backpack.slots", 54);
   }

   private boolean isBackpackOpen(Player p) {
      if (p.getOpenInventory() != null && p.getOpenInventory().getTitle() != null) {
         String title = ChatColor.stripColor(p.getOpenInventory().getTitle());
         return title.endsWith("'s Backpack");
      } else {
         return false;
      }
   }

   private boolean isBundleItem(ItemStack it) {
      return BackpackItemManager.isBackpackItem(it);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player p) {
         String rawTitle = e.getView().getTitle();
         String viewTitle = rawTitle == null ? "" : ChatColor.stripColor(rawTitle);
         if (!viewTitle.equals("Your Settings") && !viewTitle.equals("Select Backpack Color")) {
            ItemStack clicked = e.getCurrentItem();
            ItemStack cursor = e.getCursor();
            if (clicked != null && !clicked.getType().isAir()) {
               int bundleRaw = e.getView().convertSlot(9);
               int raw = e.getRawSlot();
               if (!this.isBackpackOpen(p) || !this.isBundleItem(clicked) && !this.isBundleItem(cursor)) {
                  boolean clickedIsPlayerInv = e.getClickedInventory() == p.getInventory();
                  boolean clickedIsBundleSlot = raw == bundleRaw || clickedIsPlayerInv && e.getSlot() == 9;
                  if (clickedIsBundleSlot && this.isBundleItem(clicked)) {
                     if (e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT && e.getClick() != ClickType.DOUBLE_CLICK && e.getClick() != ClickType.NUMBER_KEY && e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getAction() != InventoryAction.HOTBAR_SWAP && e.getAction() != InventoryAction.COLLECT_TO_CURSOR) {
                        e.setCancelled(true);
                        e.setResult(Result.DENY);
                        if (cursor != null && !cursor.getType().isAir()) {
                           Inventory backpack = this.bpManager.getBackpack(p, this.backpackSlots);
                           int firstEmpty = backpack.firstEmpty();
                           ItemStack toStore = cursor.clone();
                           if (toStore.getType() == Material.BUNDLE) {
                              BundleMeta meta = (BundleMeta)toStore.getItemMeta();
                              List<ItemStack> inner = meta.getItems();
                              if (!inner.isEmpty()) {
                                 toStore = ((ItemStack)inner.get(0)).clone();
                              }
                           }

                           if (firstEmpty == -1) {
                              ItemStack keep = cursor.clone();
                              e.setCancelled(true);
                              e.setResult(Result.DENY);
                              Bukkit.getScheduler().runTask(this.plugin, () -> {
                                 p.setItemOnCursor(keep);
                                 p.updateInventory();
                              });
                              p.sendMessage("§cYour backpack is full!");
                           } else {
                              p.setItemOnCursor((ItemStack)null);
                              e.setCursor((ItemStack)null);
                              backpack.setItem(firstEmpty, toStore);
                              this.bpManager.saveBackpack(p, backpack);
                              p.sendMessage("§aItem added to your backpack!");
                              p.updateInventory();
                           }
                        } else {
                           Bukkit.getScheduler().runTask(this.plugin, () -> {
                              if (!this.isBackpackOpen(p)) {
                                 Inventory bp = this.bpManager.getBackpack(p, this.backpackSlots);
                                 if (bp != null) {
                                    p.openInventory(bp);
                                    p.updateInventory();
                                 }
                              }
                           });
                        }
                     } else {
                        e.setCancelled(true);
                        e.setResult(Result.DENY);
                        BukkitScheduler var18 = Bukkit.getScheduler();
                        Utility var19 = this.plugin;
                        Objects.requireNonNull(p);
                        var18.runTask(var19, p::updateInventory);
                     }
                  } else {
                     if (this.isBundleItem(e.getCursor())) {
                        e.setCancelled(true);
                        e.setResult(Result.DENY);
                        BukkitScheduler var10000 = Bukkit.getScheduler();
                        Utility var10001 = this.plugin;
                        Objects.requireNonNull(p);
                        var10000.runTask(var10001, p::updateInventory);
                     }

                  }
               } else {
                  e.setCancelled(true);
                  e.setResult(Result.DENY);
                  p.updateInventory();
               }
            }
         }
      }
   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player) {
         Player p = (Player)var3;
         switch (e.getView().getType()) {
            case CRAFTING:
            case WORKBENCH:
            case PLAYER:
               return;
            default:
               String rawTitle = e.getView().getTitle();
               String viewTitle = rawTitle == null ? "" : ChatColor.stripColor(rawTitle);
               if (!viewTitle.equals("Your Settings") && !viewTitle.equals("Select Backpack Color")) {
                  int bundleRaw = e.getView().convertSlot(9);
                  if (this.isBackpackOpen(p) && BackpackItemManager.isBackpackItem(p.getItemOnCursor())) {
                     e.setCancelled(true);
                     e.setResult(Result.DENY);
                     p.updateInventory();
                  } else {
                     if (e.getRawSlots().contains(bundleRaw)) {
                        e.setCancelled(true);
                        e.setResult(Result.DENY);
                        p.updateInventory();
                     }

                  }
               }
         }
      }
   }
}
