package fun.derjxnnik.homes;

import fun.derjxnnik.misc.Colors;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputListener implements Listener {
   private final HomeManager manager;
   private final Set awaitingInput = new HashSet();
   private final int maxHomes;

   public ChatInputListener(HomeManager manager, int maxHomes) {
      this.manager = manager;
      this.maxHomes = maxHomes;
   }

   public void waitForInput(Player player) {
      this.awaitingInput.add(player.getUniqueId());
      player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6F, 1.2F);
      player.sendMessage(Colors.PREFIX + Colors.GRAY + "Type a name for your " + Colors.YELLOW + "new home " + Colors.GRAY + "in chat " + Colors.GRAY + "(type " + Colors.RED + "cancel" + Colors.GRAY + " to abort)");
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (this.awaitingInput.contains(player.getUniqueId())) {
         event.setCancelled(true);
         String message = ChatColor.stripColor(event.getMessage().trim());
         if (message.equalsIgnoreCase("cancel")) {
            this.awaitingInput.remove(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
            player.sendMessage(Colors.PREFIX + Colors.GRAY + "Home creation " + Colors.RED + "cancelled" + Colors.GRAY + ".");
         } else if (message.length() > 20) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8F, 0.8F);
            String var5 = Colors.PREFIX;
            player.sendMessage(var5 + Colors.RED + "That name is too long " + String.valueOf(ChatColor.BLUE) + "(max " + Colors.YELLOW + "20" + Colors.GRAY + " characters" + String.valueOf(ChatColor.BLUE) + ")");
         } else {
            int currentHomes = this.manager.getHomeCount(player);
            if (this.maxHomes > 0 && currentHomes >= this.maxHomes) {
               this.awaitingInput.remove(player.getUniqueId());
               player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8F, 0.8F);
               String var10001 = Colors.PREFIX;
               player.sendMessage(var10001 + Colors.RED + "You have reached your " + Colors.YELLOW + "home limit " + String.valueOf(ChatColor.BLUE) + "(max " + Colors.YELLOW + this.maxHomes + String.valueOf(ChatColor.BLUE) + ")");
            } else {
               this.manager.setHome(player, message);
               this.awaitingInput.remove(player.getUniqueId());
               player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7F, 1.3F);
               player.sendMessage(Colors.PREFIX + Colors.GREEN + "Home " + Colors.YELLOW + "'" + message + "' " + Colors.GREEN + "has been set successfully!");
            }
         }
      }
   }
}
