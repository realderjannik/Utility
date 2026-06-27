package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.TimeLabel;
import fun.derjxnnik.utility.Utility;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DayCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.day", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else {
            World w = p.getWorld();
            long day = w.getFullTime() / 24000L;
            String var10001 = Colors.PREFIX;
            p.sendMessage(var10001 + Colors.GRAY + "Current day: " + Colors.YELLOW + day + Colors.GRAY + "\n" + Colors.PREFIX + Colors.GRAY + "Current time: " + Colors.YELLOW + w.getTime() + " (" + this.currentTime(w) + ")");
            return true;
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }

   public String currentTime(World w) {
      TimeLabel label = TimeLabel.fromTime(w.getTime());
      if (label != null) {
         String var10000 = label.getEmoji();
         return var10000 + " " + label.getLabel();
      } else {
         return "Unknown";
      }
   }
}
