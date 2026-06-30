package fun.derjxnnik.commands;

import fun.derjxnnik.ban.BanManager;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.mute.MuteEntry;
import fun.derjxnnik.mute.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MuteCommand implements CommandExecutor, TabCompleter {

    private static final List<String> GRUND_VORSCHLAEGE = List.of(
            "Spam", "Beleidigung", "Werbung", "Toxic", "Rassismus", "Provokation"
    );
    private static final List<String> ZEIT_VORSCHLAEGE = List.of(
            "1h", "6h", "12h", "1d", "3d", "7d", "permanent"
    );

    private final MuteManager muteManager;

    public MuteCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("utility.mute")) {
            sender.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(Messages.MUTE_NUTZUNG);
            return true;
        }

        String targetName = args[0];
        String grund = args[1];
        String zeitArg = args[2];

        Player online = Bukkit.getPlayerExact(targetName);
        UUID targetUuid;
        String actualName;

        if (online != null) {
            if (muteManager.isMuted(online.getUniqueId())) {
                sender.sendMessage(Messages.MUTE_BEREITS_GEMUTET);
                return true;
            }
            targetUuid = online.getUniqueId();
            actualName = online.getName();
        } else {
            @SuppressWarnings("deprecation")
            OfflinePlayer op = Bukkit.getOfflinePlayer(targetName);
            if (!op.hasPlayedBefore()) {
                sender.sendMessage(Messages.SPIELER_OFFLINE);
                return true;
            }
            if (muteManager.isMuted(op.getUniqueId())) {
                sender.sendMessage(Messages.MUTE_BEREITS_GEMUTET);
                return true;
            }
            targetUuid = op.getUniqueId();
            actualName = op.getName() != null ? op.getName() : targetName;
        }

        long expiresAt;
        String dauer;
        try {
            expiresAt = BanManager.parseTime(zeitArg);
            dauer = BanManager.formatDuration(expiresAt);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Messages.BAN_ZEIT_UNGUELTIG);
            return true;
        }

        String muterName = sender instanceof Player p ? p.getName() : "Konsole";
        MuteEntry entry = new MuteEntry(actualName, targetUuid.toString(), grund, muterName,
                System.currentTimeMillis(), expiresAt);
        muteManager.mute(entry);

        if (online != null) {
            online.sendMessage(Messages.muteNachricht(grund, dauer, muterName));
        }
        sender.sendMessage(Messages.muteErfolgreich(actualName, grund, dauer));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                     @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) return GRUND_VORSCHLAEGE.stream()
                .filter(r -> r.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        if (args.length == 3) return ZEIT_VORSCHLAEGE.stream()
                .filter(t -> t.startsWith(args[2].toLowerCase()))
                .collect(Collectors.toList());
        return List.of();
    }
}
