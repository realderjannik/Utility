package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.warn.WarnEntry;
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

public class WarnListCommand implements CommandExecutor, TabCompleter {

    private final WarnManager warnManager;

    public WarnListCommand(WarnManager warnManager) {
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
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Nutzung: /warnlist <Spieler>");
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(Messages.SPIELER_OFFLINE);
            return true;
        }

        List<WarnEntry> warns = warnManager.getWarns(target.getUniqueId());
        sender.sendMessage(Colors.DARK_AQUA + "────────────────────────");
        sender.sendMessage(Colors.DARK_AQUA + " Verwarnungen: " + Colors.WHITE + target.getName()
                + Colors.GRAY + " (" + warns.size() + ")");
        sender.sendMessage(Colors.DARK_AQUA + "────────────────────────");
        if (warns.isEmpty()) {
            sender.sendMessage(Colors.GRAY + "Keine Verwarnungen vorhanden.");
        } else {
            for (int i = 0; i < warns.size(); i++) {
                WarnEntry w = warns.get(i);
                sender.sendMessage(Colors.YELLOW + "#" + (i + 1) + " " + Colors.WHITE + w.getReason()
                        + Colors.GRAY + " — Von: " + Colors.WHITE + w.getWarnedByName()
                        + Colors.DARK_GRAY + " | " + w.getDateString());
            }
        }
        sender.sendMessage(Colors.DARK_AQUA + "────────────────────────");
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
