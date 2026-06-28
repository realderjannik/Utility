package fun.derjxnnik.listeners;

import fun.derjxnnik.misc.InventoryUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryProtectionListener implements Listener {
   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      if (InventoryUtil.isPluginInventory(event)) {
         event.setCancelled(true);
      }

   }
}
