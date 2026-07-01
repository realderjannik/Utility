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

public class ClearWarnsCommand implements CommandExecutor, TabCompleter {

    private final WarnManager warnManager;

    public ClearWarnsCommand(WarnManager warnManager) {
        this.warnManager = warnManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("utility.warn")) {
            sender.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Nutzung: /clearwarns <Spieler>");
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(Messages.SPIELER_OFFLINE);
            return true;
        }

        warnManager.clearWarns(target.getUniqueId());
        sender.sendMessage(Colors.PREFIX + Colors.GREEN + "Verwarnungen von " + Colors.WHITE
                + target.getName() + Colors.GREEN + " wurden gelöscht.");
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
        return List.of();
    }
}
