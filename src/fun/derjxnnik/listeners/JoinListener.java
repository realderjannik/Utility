package fun.derjxnnik.listeners;

import fun.derjxnnik.backpack.BackpackItemManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import fun.derjxnnik.utility.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
   private final Utility plugin;
   private final BackpackItemManager backpackItemManager;

   public JoinListener(Utility plugin, BackpackItemManager backpackItemManager) {
      this.plugin = plugin;
      this.backpackItemManager = backpackItemManager;
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent e) {
      Player player = e.getPlayer();
      String pName = player.getName();
      e.setJoinMessage(Colors.GRAY + "[" + Colors.BOLD_GREEN + "+" + Colors.GRAY + "] " + Colors.GREEN + pName);
      ScoreboardManager.set(player);
      Bukkit.getScheduler().runTaskLater(this.plugin, this::updateTablistForAll, 1L);
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.backpackItemManager.refreshBackpackItem(player), 10L);
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
