package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaytimeCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.playtime", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (args.length > 1) {
            p.sendMessage(Messages.SPIELZEIT_NUTZUNG);
            return true;
         } else if (args.length == 0) {
            p.sendMessage(Messages.spielzeitEigen(this.formatPlaytime(p)));
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(args[0]);
            if (t != null && t.isOnline()) {
               p.sendMessage(Messages.spielzeitSpieler(t.getName(), this.formatPlaytime(t)));
               return true;
            } else {
               p.sendMessage(Messages.SPIELZEIT_SPIELER_OFFLINE);
               return true;
            }
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }

   public String formatPlaytime(Player p) {
      int totalMinutes = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
      return Messages.formatSpielzeit(totalMinutes);
   }
}
