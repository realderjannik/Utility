package fun.derjxnnik.tabcompleters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaytimeTabCompleter implements TabCompleter {
   public @Nullable List onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
      return args.length == 1 ? this.getOnlinePlayerNames(args[0]) : Collections.emptyList();
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
