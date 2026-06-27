package fun.derjxnnik.tabcompleters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class BackpackTabCompleter implements TabCompleter {
   private static final List SUBCOMMANDS = List.of("open", "backup", "restore", "help");

   public List onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
      if (args.length == 1) {
         List<String> completions = new ArrayList();

         for(String sub : SUBCOMMANDS) {
            if (sub.startsWith(args[0].toLowerCase())) {
               completions.add(sub);
            }
         }

         return completions;
      } else if (args.length == 2 && args[0].equalsIgnoreCase("open")) {
         return this.getOnlinePlayerNames(args[1]);
      } else if (args.length == 2 && args[0].equalsIgnoreCase("backup")) {
         return this.getOnlinePlayerNames(args[1]);
      } else if (args.length == 2 && args[0].equalsIgnoreCase("restore")) {
         return Collections.emptyList();
      } else {
         return args.length == 3 && args[0].equalsIgnoreCase("restore") ? this.getOnlinePlayerNames(args[2]) : Collections.emptyList();
      }
   }

   private List getOnlinePlayerNames(String prefix) {
      List<String> list = new ArrayList();

      for(Player p : Bukkit.getOnlinePlayers()) {
         if (p.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
            list.add(p.getName());
         }
      }

      return list;
   }
}
