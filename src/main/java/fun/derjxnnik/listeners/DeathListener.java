package fun.derjxnnik.listeners;

import fun.derjxnnik.backpack.BackpackItemManager;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {
   private final BackpackItemManager backpackManager;
   private final Map savedBackpack = new HashMap();

   public DeathListener(BackpackItemManager backpackManager) {
      this.backpackManager = backpackManager;
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent e) {
      Player p = e.getEntity();
      ItemStack toKeep = null;

      for(ItemStack item : e.getDrops()) {
         if (BackpackItemManager.isBackpackItem(item)) {
            toKeep = item.clone();
            e.getDrops().remove(item);
            break;
         }
      }

      if (toKeep != null) {
         this.savedBackpack.put(p, toKeep);
      }

   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent e) {
      Player p = e.getPlayer();
      if (this.savedBackpack.containsKey(p)) {
         ItemStack bp = (ItemStack)this.savedBackpack.remove(p);
         p.getInventory().setItem(9, bp);
         p.updateInventory();
      } else {
         this.backpackManager.refreshBackpackItem(p);
      }

   }
}
