package fun.derjxnnik.commands;

import fun.derjxnnik.freeze.FreezeManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class FreezeCommand implements CommandExecutor, TabCompleter {

    private final FreezeManager freezeManager;

    public FreezeCommand(FreezeManager freezeManager) {
        this.freezeManager = freezeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("utility.freeze")) {
            sender.sendMessage(Messages.KEINE_BERECHTIGUNG);
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Nutzung: /freeze <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Messages.SPIELER_OFFLINE);
            return true;
        }
        if (target.hasPermission("utility.freeze")) {
            sender.sendMessage(Colors.PREFIX + Colors.RED + "Dieser Spieler kann nicht eingefroren werden.");
            return true;
        }

        boolean nowFrozen = freezeManager.toggle(target.getUniqueId());
        if (nowFrozen) {
            sender.sendMessage(Colors.PREFIX + Colors.AQUA + target.getName()
                    + Colors.GRAY + " wurde eingefroren.");
            target.sendMessage(Colors.PREFIX + Colors.RED + "Du wurdest eingefroren!"
                    + Colors.GRAY + " Wende dich an ein Teammitglied.");
        } else {
            sender.sendMessage(Colors.PREFIX + Colors.GREEN + target.getName()
                    + Colors.GRAY + " wurde freigegeben.");
            target.sendMessage(Colors.PREFIX + Colors.GREEN + "Du wurdest freigegeben.");
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
        return List.of();
    }
}
