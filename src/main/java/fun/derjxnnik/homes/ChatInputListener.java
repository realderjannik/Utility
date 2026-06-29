package fun.derjxnnik.homes;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputListener implements Listener {

   private final HomeManager manager;
   private final Set<UUID> awaitingInput = new HashSet<>();

   public ChatInputListener(HomeManager manager) {
      this.manager = manager;
   }

   public void waitForInput(Player player) {
      awaitingInput.add(player.getUniqueId());
      player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1.2F);
      player.sendMessage(Messages.HOME_EINGABE_WARTEN);
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (!awaitingInput.contains(player.getUniqueId())) return;

      event.setCancelled(true);
      String message = org.bukkit.ChatColor.stripColor(event.getMessage().trim());

      if (message.equalsIgnoreCase("cancel")) {
         awaitingInput.remove(player.getUniqueId());
         player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
         player.sendMessage(Messages.HOME_ABGEBROCHEN);
         return;
      }

      if (message.length() > 20) {
         player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8F, 0.8F);
         player.sendMessage(Messages.HOME_NAME_ZU_LANG);
         return;
      }

      int limit = manager.getMaxHomes(player);
      int current = manager.getHomeCount(player);
      if (limit > 0 && current >= limit) {
         awaitingInput.remove(player.getUniqueId());
         player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8F, 0.8F);
         player.sendMessage(Messages.homeLimitErreicht(limit));
         return;
      }

      manager.setHome(player, message);
      awaitingInput.remove(player.getUniqueId());
      player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7F, 1.3F);
      player.sendMessage(Messages.homeGesetzt(message));
   }
}
