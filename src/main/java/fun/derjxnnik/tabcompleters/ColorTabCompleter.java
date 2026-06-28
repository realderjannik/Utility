package fun.derjxnnik.tabcompleters;

import fun.derjxnnik.commands.ColorCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ColorTabCompleter implements TabCompleter {

    private static final List<String> SUGGESTIONS = new ArrayList<>();

    static {
        for (Object[] c : ColorCommand.COLORS) SUGGESTIONS.add((String) c[0]);
        for (Object[] f : ColorCommand.FORMATS) SUGGESTIONS.add((String) f[0]);
        SUGGESTIONS.add("default");
        SUGGESTIONS.add("reset");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                                @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length != 1) return List.of();
        String prefix = args[0].toLowerCase();
        List<String> result = new ArrayList<>();
        for (String s : SUGGESTIONS) {
            if (s.startsWith(prefix)) result.add(s);
        }
        return result;
    }
}
