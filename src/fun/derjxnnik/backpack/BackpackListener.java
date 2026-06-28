package fun.derjxnnik.backpack;

import fun.derjxnnik.misc.Colors;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BackpackListener implements Listener {
   private final BackpackManager manager;

   public BackpackListener(BackpackManager manager) {
      this.manager = manager;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player p) {
         Inventory var5 = e.getInventory();
         if (e.getView().getTitle().toLowerCase().contains("backpack")) {
            ItemStack clicked = e.getCurrentItem();
            if (!this.manager.isAllowed(clicked)) {
               e.setCancelled(true);
               p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               p.sendActionBar(Colors.RED + "Shulkerboxes are not allowed to be placed in the backpack.");
            }

         }
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent e) {
      HumanEntity var3 = e.getPlayer();
      if (var3 instanceof Player p) {
         Inventory var4 = e.getInventory();
         if (e.getView().getTitle().toLowerCase().contains("backpack")) {
            this.manager.saveBackpack(p, var4);
            p.sendActionBar(Colors.GREEN + "Backpack closed!");
            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0F, 1.0F);
         }
      }
   }
}
