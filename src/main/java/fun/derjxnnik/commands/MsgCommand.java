package fun.derjxnnik.commands;

import fun.derjxnnik.messages.MessageManager;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MsgCommand implements CommandExecutor, TabCompleter {

    private final MessageManager messageManager;
    private final SettingsManager settingsManager;

    public MsgCommand(MessageManager messageManager, SettingsManager settingsManager) {
        this.messageManager = messageManager;
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Messages.NUR_SPIELER);
            return true;
        }
        if (args.length < 2) {
            p.sendMessage(Messages.MSG_NUTZUNG);
            return true;
        }
        if (!settingsManager.isMsgEnabled(p)) {
            p.sendMessage(Messages.MSG_SELBST_DEAKTIVIERT);
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            p.sendMessage(Messages.MSG_EMPFAENGER_OFFLINE);
            return true;
        }
        if (target.equals(p)) {
            p.sendMessage(Messages.MSG_SELBST);
            return true;
        }
        if (!settingsManager.isMsgEnabled(target)) {
            p.sendMessage(Messages.MSG_DEAKTIVIERT);
            return true;
        }

        String nachricht = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        p.sendMessage(Messages.msgGesendet(target.getName(), nachricht));
        target.sendMessage(Messages.msgErhalten(p.getName(), nachricht));
        messageManager.setLastPartner(p, target);
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
        return List.of();
    }
}
