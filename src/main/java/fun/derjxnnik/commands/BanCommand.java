package fun.derjxnnik.commands;

import fun.derjxnnik.ban.BanEntry;
import fun.derjxnnik.ban.BanManager;
import fun.derjxnnik.misc.Messages;
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

public class BanCommand implements CommandExecutor, TabCompleter {

    private static final List<String> GRUND_VORSCHLAEGE = List.of(
            "Cheating", "Griefing", "Spam", "Beleidigung", "Werbung", "Exploiting", "Trolling"
    );
    private static final List<String> ZEIT_VORSCHLAEGE = List.of(
            "1h", "6h", "12h", "1d", "3d", "7d", "14d", "30d", "permanent"
    );

    private final BanManager banManager;

    public BanCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("utility.ban")) {
            sender.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Messages.BAN_NUTZUNG);
            return true;
        }

        String targetName = args[0];
        String grund = args[1];
        String zeitArg = args.length >= 3 ? args[2] : null;

        // Resolve player (online or offline)
        Player online = Bukkit.getPlayerExact(targetName);
        UUID targetUuid;
        String actualName;

        if (online != null) {
            if (banManager.isBanned(online.getUniqueId())) {
                sender.sendMessage(Messages.BAN_BEREITS_GEBANNT);
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
            if (banManager.isBanned(op.getUniqueId())) {
                sender.sendMessage(Messages.BAN_BEREITS_GEBANNT);
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

        String bannerName = sender instanceof Player p ? p.getName() : "Konsole";
        BanEntry entry = new BanEntry(actualName, targetUuid.toString(), grund, bannerName,
                System.currentTimeMillis(), expiresAt);
        banManager.ban(entry);

        if (online != null) {
            String kickMsg = expiresAt == -1
                    ? Messages.banKickNachrichtPermanent(grund, bannerName)
                    : Messages.banKickNachricht(grund, dauer, bannerName);
            online.kickPlayer(kickMsg);
        }

        sender.sendMessage(Messages.banErfolgreich(actualName, grund, dauer));
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
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            return GRUND_VORSCHLAEGE.stream()
                    .filter(r -> r.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        if (args.length == 3) {
            String input = args[2].toLowerCase();
            return ZEIT_VORSCHLAEGE.stream()
                    .filter(t -> t.startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
