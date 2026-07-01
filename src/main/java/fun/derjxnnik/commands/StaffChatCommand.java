package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.staffchat.StaffChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffChatCommand implements CommandExecutor {

    private final StaffChatManager staffChatManager;

    public StaffChatCommand(StaffChatManager staffChatManager) {
        this.staffChatManager = staffChatManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        // Console: inline message only
        if (!(sender instanceof Player p)) {
            if (args.length == 0) {
                sender.sendMessage(Colors.PREFIX + Colors.RED + "Nutzung: /sc <Nachricht>");
                return true;
            }
            staffChatManager.broadcast("Konsole", String.join(" ", args));
            return true;
        }

        if (!p.hasPermission("utility.staffchat")) {
            p.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }

        if (args.length == 0) {
            // Toggle persistent mode
            boolean active = staffChatManager.toggle(p.getUniqueId());
            if (active) {
                p.sendMessage(Colors.PREFIX + Colors.DARK_RED + "Staff-Chat "
                        + Colors.GREEN + "aktiviert" + Colors.GRAY
                        + " — alle deine Nachrichten gehen ans Team.");
            } else {
                p.sendMessage(Colors.PREFIX + Colors.DARK_RED + "Staff-Chat "
                        + Colors.RED + "deaktiviert" + Colors.GRAY + ".");
            }
            return true;
        }

        // Inline single message (does not toggle persistent mode)
        staffChatManager.broadcast(p.getName(), String.join(" ", args));
        return true;
    }
}
