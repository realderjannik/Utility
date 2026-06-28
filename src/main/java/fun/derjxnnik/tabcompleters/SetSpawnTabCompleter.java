package fun.derjxnnik.tabcompleters;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetSpawnTabCompleter implements TabCompleter {
   public @Nullable List onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
      return args.length == 1 ? Collections.singletonList("confirm") : Collections.emptyList();
   }
}
