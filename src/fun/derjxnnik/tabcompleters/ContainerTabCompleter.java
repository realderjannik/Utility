package fun.derjxnnik.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ContainerTabCompleter implements TabCompleter {
   private static final List SUBCOMMANDS = List.of("lock", "unlock", "trust", "untrust", "transfer", "info");

   public List onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> suggestions = new ArrayList();
      if (args.length == 1) {
         suggestions.addAll(SUBCOMMANDS);
         return (List)suggestions.stream().filter((s) -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
      } else {
         if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("trust") || sub.equals("untrust") || sub.equals("transfer")) {
               return (List)Bukkit.getOnlinePlayers().stream().map((p) -> p.getName()).filter((name) -> name.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
            }
         }

         return suggestions;
      }
   }
}
