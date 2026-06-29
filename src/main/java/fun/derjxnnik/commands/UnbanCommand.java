package fun.derjxnnik.commands;

import fun.derjxnnik.ban.BanEntry;
import fun.derjxnnik.ban.BanManager;
import fun.derjxnnik.misc.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class UnbanCommand implements CommandExecutor, TabCompleter {

    private final BanManager banManager;

    public UnbanCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("utility.ban")) {
            sender.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Messages.UNBAN_NUTZUNG);
            return true;
        }

        BanEntry entry = banManager.getBanByName(args[0]);
        if (entry == null) {
            sender.sendMessage(Messages.BAN_NICHT_GEFUNDEN);
            return true;
        }

        banManager.unban(entry.getPlayerUuid());
        sender.sendMessage(Messages.unbanErfolgreich(entry.getPlayerName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                     @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return banManager.getBannedNames().stream()
                    .filter(n -> n.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
