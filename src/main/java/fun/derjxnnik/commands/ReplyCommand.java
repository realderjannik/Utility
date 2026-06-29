package fun.derjxnnik.commands;

import fun.derjxnnik.messages.MessageManager;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ReplyCommand implements CommandExecutor {

    private final MessageManager messageManager;
    private final SettingsManager settingsManager;

    public ReplyCommand(MessageManager messageManager, SettingsManager settingsManager) {
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
        if (args.length < 1) {
            p.sendMessage(Messages.REPLY_NUTZUNG);
            return true;
        }

        UUID partnerUuid = messageManager.getLastPartner(p.getUniqueId());
        if (partnerUuid == null) {
            p.sendMessage(Messages.REPLY_KEIN_PARTNER);
            return true;
        }

        Player target = Bukkit.getPlayer(partnerUuid);
        if (target == null || !target.isOnline()) {
            p.sendMessage(Messages.REPLY_KEIN_PARTNER);
            return true;
        }

        if (!settingsManager.isMsgEnabled(target)) {
            p.sendMessage(Messages.MSG_DEAKTIVIERT);
            return true;
        }

        String nachricht = String.join(" ", args);
        p.sendMessage(Messages.msgGesendet(target.getName(), nachricht));
        target.sendMessage(Messages.msgErhalten(p.getName(), nachricht));
        messageManager.setLastPartner(p, target);
        return true;
    }
}
