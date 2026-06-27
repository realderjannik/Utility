package fun.derjxnnik.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class EnderChestListener implements Listener {
   @EventHandler
   public void onEnderChestClose(InventoryCloseEvent e) {
      Player p = (Player)e.getPlayer();
      if (e.getInventory().getType() == InventoryType.ENDER_CHEST) {
         p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0F, 1.0F);
      }

   }
}
