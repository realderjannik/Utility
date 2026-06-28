package fun.derjxnnik.commands;

import fun.derjxnnik.currency.CurrencyManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Icons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class PayCommand implements CommandExecutor, TabCompleter {

    private final CurrencyManager currency;

    public PayCommand(CurrencyManager currency) {
        this.currency = currency;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Colors.CONSOLE_SENDER);
            return true;
        }

        if (args.length != 2) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Verwendung: " + Colors.YELLOW + "/pay <Spieler> <Menge>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Spieler \"" + Colors.YELLOW + args[0] + Colors.RED + "\" ist nicht online.");
            return true;
        }

        if (target.equals(p)) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Du kannst dir nicht selbst Coins überweisen.");
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Ungültige Menge: " + Colors.YELLOW + args[1]);
            return true;
        }

        if (amount <= 0) {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Die Menge muss größer als 0 sein.");
            return true;
        }

        boolean success = currency.transfer(
                p.getUniqueId(), p.getName(),
                target.getUniqueId(), target.getName(),
                amount);

        if (!success) {
            long balance = currency.getBalance(p.getUniqueId());
            p.sendMessage(Component.text(Colors.PREFIX)
                    .append(Component.text("Nicht genug Coins. (Verfügbar: ", NamedTextColor.RED))
                    .append(Component.text(Icons.COINS + " " + String.format("%,d", balance), NamedTextColor.YELLOW))
                    .append(Component.text(")", NamedTextColor.RED)));
            return true;
        }

        // Notify sender
        p.sendMessage(Component.text(Colors.PREFIX)
                .append(Component.text("Du hast ", NamedTextColor.GRAY))
                .append(Component.text(Icons.COINS + " " + String.format("%,d", amount), NamedTextColor.YELLOW))
                .append(Component.text(" Coins an ", NamedTextColor.GRAY))
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" überwiesen.", NamedTextColor.GRAY)));

        // Notify recipient
        target.sendMessage(Component.text(Colors.PREFIX)
                .append(Component.text(p.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" hat dir ", NamedTextColor.GRAY))
                .append(Component.text(Icons.COINS + " " + String.format("%,d", amount), NamedTextColor.YELLOW))
                .append(Component.text(" Coins überwiesen.", NamedTextColor.GRAY)));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
