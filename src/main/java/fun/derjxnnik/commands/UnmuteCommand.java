package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.mute.MuteEntry;
import fun.derjxnnik.mute.MuteManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class UnmuteCommand implements CommandExecutor, TabCompleter {

    private final MuteManager muteManager;

    public UnmuteCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("utility.mute")) {
            sender.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(Messages.UNMUTE_NUTZUNG);
            return true;
        }

        MuteEntry entry = muteManager.getMuteByName(args[0]);
        if (entry == null) {
            sender.sendMessage(Messages.MUTE_NICHT_GEFUNDEN);
            return true;
        }

        muteManager.unmute(entry.getPlayerUuid());
        sender.sendMessage(Messages.unmuteErfolgreich(entry.getPlayerName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                     @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return muteManager.getMutedNames().stream()
                    .filter(n -> n.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
