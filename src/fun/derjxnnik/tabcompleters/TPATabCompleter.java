package fun.derjxnnik.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TPATabCompleter implements TabCompleter {
   public List onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> completions = new ArrayList();
      if (args.length == 1) {
         for(Player p : Bukkit.getOnlinePlayers()) {
            if (sender instanceof Player) {
               Player self = (Player)sender;
               if (p.getUniqueId().equals(self.getUniqueId())) {
                  continue;
               }
            }

            String name = p.getName();
            if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
               completions.add(name);
            }
         }
      }

      return completions;
   }
}
