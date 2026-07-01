package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.warn.WarnManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class DelWarnCommand implements CommandExecutor, TabCompleter {

    private final WarnManager warnManager;

    public DelWarnCommand(WarnManager warnManager) {
        this.warnManager = warnManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("utility.warn")) {
            sender.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Nutzung: /delwarn <Spieler> <Nummer>");
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(Messages.SPIELER_OFFLINE);
            return true;
        }

        int index;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Bitte gib eine gültige Zahl an.");
            return true;
        }

        boolean removed = warnManager.removeWarn(target.getUniqueId(), index);
        if (removed) {
            sender.sendMessage(Colors.PREFIX + Colors.GREEN + "Verwarnung #" + index + " von "
                    + Colors.WHITE + target.getName() + Colors.GREEN + " wurde entfernt.");
        } else {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Verwarnung #" + index + " wurde nicht gefunden.");
        }
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
        if (args.length == 2) {
            return List.of("1", "2", "3");
        }
        return List.of();
    }
}
