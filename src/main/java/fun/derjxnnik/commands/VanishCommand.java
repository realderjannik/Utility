package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import fun.derjxnnik.vanish.VanishManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {

    private final VanishManager vanishManager;

    public VanishCommand(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Colors.CONSOLE_SENDER);
            return true;
        }
        if (!p.hasPermission("utility.vanish")) {
            p.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }

        boolean nowVanished = vanishManager.toggle(p);
        if (nowVanished) {
            p.sendMessage(Colors.PREFIX + Colors.GRAY + "Du bist jetzt "
                    + Colors.RED + "unsichtbar" + Colors.GRAY + ".");
        } else {
            p.sendMessage(Colors.PREFIX + Colors.GRAY + "Du bist jetzt wieder "
                    + Colors.GREEN + "sichtbar" + Colors.GRAY + ".");
        }
        return true;
    }
}
