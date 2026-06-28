package fun.derjxnnik.listeners;

import fun.derjxnnik.utility.scoreboard.ScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class SwitchWorldListener implements Listener {
   @EventHandler
   public void onWorldChange(PlayerChangedWorldEvent e) {
      Player p = e.getPlayer();
      ScoreboardManager.update(p);
   }
}
