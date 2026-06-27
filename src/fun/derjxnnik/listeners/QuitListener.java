package fun.derjxnnik.listeners;

import fun.derjxnnik.backpack.BackpackManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import fun.derjxnnik.utility.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class QuitListener implements Listener {
   private final Utility plugin;
   private final BackpackManager bpManager;

   public QuitListener(Utility plugin, BackpackManager bpManager) {
      this.plugin = plugin;
      this.bpManager = bpManager;
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent e) {
      Player p = e.getPlayer();
      String var10001 = Colors.GRAY;
      e.setQuitMessage(var10001 + "[" + Colors.BOLD_RED + "-" + Colors.GRAY + "] " + Colors.RED + p.getName());
      ScoreboardManager.remove(p);
      Bukkit.getScheduler().runTaskLater(this.plugin, this::updateTablistForAll, 1L);
      Inventory inv = this.bpManager.getBackpack(p, this.plugin.getConfig().getInt("backpack.slots", 54));
      this.bpManager.saveBackpack(p, inv);
      this.bpManager.unloadBackpack(p);
   }

   private void updateTablistForAll() {
      String header = Colors.BOLD_DARK_AQUA + "SMP.DERJXNNIK.FUN\n";
      String var10000 = Colors.DARK_AQUA;
      String footer = "\n " + var10000 + "Online Players: " + Colors.YELLOW + Bukkit.getOnlinePlayers().size() + Colors.GRAY + "/" + Colors.YELLOW + Bukkit.getMaxPlayers();

      for(Player online : Bukkit.getOnlinePlayers()) {
         online.setPlayerListHeaderFooter(header, footer);
      }

   }
}
