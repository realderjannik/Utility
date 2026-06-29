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
      e.setQuitMessage(Colors.GRAY + "[" + Colors.BOLD_RED + "-" + Colors.GRAY + "] " + Colors.RED + p.getName());
      ScoreboardManager.remove(p);
      Bukkit.getScheduler().runTaskLater(this.plugin, this::updateTablistForAll, 1L);

      // Save backpack with the default slot size (cached inventory is used if available)
      int slots = this.plugin.getConfig().getInt("backpack.slots", 9);
      Inventory inv = this.bpManager.getBackpack(p, slots);
      this.bpManager.saveBackpack(p, inv);
      this.bpManager.unloadBackpack(p);

      // Clear PM partner tracking
      if (this.plugin.getMessageManager() != null) {
         this.plugin.getMessageManager().removePlayer(p.getUniqueId());
      }
   }

   private void updateTablistForAll() {
      String serverName = plugin.getConfig().getString("server.name", "SMP");
      String header = Colors.BOLD_DARK_AQUA + serverName + "\n";
      String footer = "\n " + Colors.DARK_AQUA + "Online Players: " + Colors.YELLOW
              + Bukkit.getOnlinePlayers().size() + Colors.GRAY + "/" + Colors.YELLOW + Bukkit.getMaxPlayers();
      for (Player online : Bukkit.getOnlinePlayers()) {
         online.setPlayerListHeaderFooter(header, footer);
      }
   }
}
