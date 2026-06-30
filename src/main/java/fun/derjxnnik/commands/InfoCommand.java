package fun.derjxnnik.commands;

import fun.derjxnnik.ban.BanEntry;
import fun.derjxnnik.ban.BanManager;
import fun.derjxnnik.currency.CurrencyManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.mute.MuteEntry;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.settings.SettingsManager;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class InfoCommand implements CommandExecutor, TabCompleter {

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                            @NotNull String s, @NotNull String @NotNull [] args) {
      if (!(sender instanceof Player p)) {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }

      FileConfiguration config = Utility.getInstance().getConfig();
      if (!config.getBoolean("use-command.info", true)) {
         p.sendMessage(Colors.DISABLED);
         return true;
      }

      if (args.length != 1) {
         p.sendMessage(Colors.PREFIX + Colors.RED + "Nutzung: /info <Spieler>");
         return true;
      }

      @SuppressWarnings("deprecation")
      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      if (!target.hasPlayedBefore() && !target.isOnline()) {
         p.sendMessage(Colors.PREFIX + Colors.RED + "Dieser Spieler hat noch nie gespielt.");
         return true;
      }

      // Respect info-visibility setting (bypass for staff)
      SettingsManager sm = Utility.getInstance().getSettingsManager();
      if (target.isOnline() && target.getPlayer() != null && !p.hasPermission("utility.info.bypass")) {
         if (!sm.isInfoVisible(target.getPlayer())) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Dieser Spieler hat sein Profil auf privat gestellt.");
            return true;
         }
      }

      String status = target.isOnline() ? Colors.GREEN + "Online" : Colors.RED + "Offline";

      String rankDisplay = Colors.GRAY + "-";
      RankManager rm = Utility.getInstance().getRankManager();
      if (rm != null && rm.isAvailable() && target.isOnline() && target.getPlayer() != null) {
         String prefix = rm.getPrefix(target.getPlayer());
         if (!prefix.isEmpty()) rankDisplay = prefix;
      }

      String coinsDisplay = Colors.GRAY + "-";
      CurrencyManager cm = Utility.getInstance().getCurrencyManager();
      if (cm != null) {
         long coins = cm.getBalance(target.getUniqueId());
         coinsDisplay = Colors.YELLOW + String.format("%,d", coins) + Colors.GRAY + " Coins";
      }

      int kills = 0, deaths = 0;
      String playtime = Colors.GRAY + "-";
      if (target.isOnline() && target.getPlayer() != null) {
         Player t = target.getPlayer();
         kills    = t.getStatistic(Statistic.PLAYER_KILLS);
         deaths   = t.getStatistic(Statistic.DEATHS);
         playtime = Colors.WHITE + formatPlaytime(t.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200);
      }

      p.sendMessage(Colors.DARK_AQUA + "────────────────────────");
      p.sendMessage(Colors.DARK_AQUA + " Info: " + Colors.WHITE + target.getName());
      p.sendMessage(Colors.DARK_AQUA + "────────────────────────");
      p.sendMessage(Colors.GRAY + "Status:    " + status);
      p.sendMessage(Colors.GRAY + "Rang:      " + rankDisplay);
      p.sendMessage(Colors.GRAY + "Guthaben:  " + coinsDisplay);
      p.sendMessage(Colors.GRAY + "Spielzeit: " + playtime);
      p.sendMessage(Colors.GRAY + "Kills: " + Colors.GREEN + kills
              + Colors.GRAY + "  |  Tode: " + Colors.RED + deaths);

      if (p.hasPermission("utility.help.staff")) {
         BanEntry ban = Utility.getInstance().getBanManager().getBan(target.getUniqueId());
         String banDisplay = ban == null
                 ? Colors.GREEN + "Nicht gebannt"
                 : Colors.RED + "Gebannt" + Colors.GRAY + " | Grund: " + Colors.WHITE + ban.getReason()
                   + Colors.GRAY + " | Bis: " + Colors.WHITE + ban.getExpiryString()
                   + Colors.GRAY + " | Von: " + Colors.WHITE + ban.getBannerName();

         MuteEntry mute = Utility.getInstance().getMuteManager().getMute(target.getUniqueId());
         String muteDisplay = mute == null
                 ? Colors.GREEN + "Nicht stumm"
                 : Colors.YELLOW + "Stummgeschaltet" + Colors.GRAY + " | Grund: " + Colors.WHITE + mute.getReason()
                   + Colors.GRAY + " | Bis: " + Colors.WHITE + mute.getExpiryString()
                   + Colors.GRAY + " | Von: " + Colors.WHITE + mute.getMuterName();

         p.sendMessage(Colors.DARK_AQUA + "────────────────────────");
         p.sendMessage(Colors.GRAY + "Ban:  " + banDisplay);
         p.sendMessage(Colors.GRAY + "Mute: " + muteDisplay);
      }

      p.sendMessage(Colors.DARK_AQUA + "────────────────────────");
      return true;
   }

   @Override
   public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                     @NotNull String alias, @NotNull String[] args) {
      if (args.length == 1) {
         return Bukkit.getOnlinePlayers().stream()
                 .map(Player::getName)
                 .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                 .collect(Collectors.toList());
      }
      return List.of();
   }

   private String formatPlaytime(int totalMinutes) {
      int days    = totalMinutes / 1440;
      int hours   = totalMinutes % 1440 / 60;
      int minutes = totalMinutes % 60;
      return days + "d " + hours + "h " + minutes + "m";
   }
}
