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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarnCommand implements CommandExecutor, TabCompleter {

    private final WarnManager warnManager;

    public WarnCommand(WarnManager warnManager) {
        this.warnManager = warnManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("utility.warn")) {
            sender.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Messages.WARN_NUTZUNG);
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(Messages.SPIELER_OFFLINE);
            return true;
        }

        String senderName = sender instanceof Player p ? p.getName() : "Konsole";
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String tName  = target.getName() != null ? target.getName() : args[0];

        WarnEntry entry = new WarnEntry(tName, target.getUniqueId().toString(),
                reason, senderName, System.currentTimeMillis());
        warnManager.addWarn(entry);
        int count = warnManager.getWarnCount(target.getUniqueId());

        sender.sendMessage(Messages.warnErfolgreich(tName, reason, count));

        Player online = Bukkit.getPlayer(target.getUniqueId());
        if (online != null) online.sendMessage(Messages.warnNachricht(reason, senderName, count));

        // Broadcast to other staff
        String staffLine = Colors.PREFIX + Colors.GOLD + "[WARN] " + Colors.WHITE + senderName
                + Colors.GRAY + " → " + Colors.WHITE + tName
                + Colors.GRAY + " (" + count + ". Verwarnung)" + Colors.GRAY + ": " + Colors.WHITE + reason;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("utility.warn") && !p.equals(sender) && !p.equals(online)) {
                p.sendMessage(staffLine);
            }
        }

        // Auto-action on threshold
        String action = warnManager.checkThresholds(target.getUniqueId(), tName);
        if ("mute".equals(action)) {
            sender.sendMessage(Colors.PREFIX + Colors.YELLOW + tName
                    + " wurde automatisch für 24h stummgeschaltet (" + count + " Verwarnungen).");
        } else if ("ban".equals(action)) {
            sender.sendMessage(Colors.PREFIX + Colors.RED + tName
                    + " wurde automatisch für 7 Tage gebannt (" + count + " Verwarnungen).");
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
            return List.of("Beleidigung", "Spam", "Griefing", "Cheating", "Regelverstoß", "Werbung");
        }
        return List.of();
    }
}
