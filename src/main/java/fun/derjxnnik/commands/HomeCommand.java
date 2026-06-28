package fun.derjxnnik.commands;

import fun.derjxnnik.homes.HomeMainGUI;
import fun.derjxnnik.homes.HomeManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeCommand implements CommandExecutor {
   private final HomeManager manager;
   private final int maxHomes;

   public HomeCommand(HomeManager manager, int maxHomes) {
      this.manager = manager;
      this.maxHomes = maxHomes;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.home", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (args.length == 0) {
            HomeMainGUI mainGUI = new HomeMainGUI(this.manager);
            mainGUI.open(p);
            return true;
         } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            return this.showHelp(p);
         } else {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Unknown subcommand! " + Colors.GRAY + "Nutze " + Colors.DARK_AQUA + "/home help");
            return true;
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }

   private boolean showHelp(Player p) {
      p.sendMessage(Colors.DARK_AQUA + "/home" + Colors.GRAY + ": " + Colors.WHITE + "Open the Home-GUI\n" + Colors.DARK_AQUA + "/home help" + Colors.GRAY + ": " + Colors.WHITE + "Zeigt diese Hilfe\n" + Colors.GRAY + "Max. Homes: " + Colors.YELLOW + this.maxHomes);
      return true;
   }
}
