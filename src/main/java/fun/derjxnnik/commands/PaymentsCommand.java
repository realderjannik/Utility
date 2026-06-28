package fun.derjxnnik.commands;

import fun.derjxnnik.currency.CurrencyManager;
import fun.derjxnnik.currency.PaymentRecord;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Icons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PaymentsCommand implements CommandExecutor {

    private final CurrencyManager currency;

    public PaymentsCommand(CurrencyManager currency) {
        this.currency = currency;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Colors.CONSOLE_SENDER);
            return true;
        }

        List<PaymentRecord> history = currency.getHistory(p.getUniqueId());

        if (history.isEmpty()) {
            p.sendMessage(Colors.PREFIX + Colors.GRAY + "Du hast noch keine Transaktionen.");
            return true;
        }

        p.sendMessage(Component.text(Colors.PREFIX)
                .append(Component.text("Deine letzten Transaktionen:", NamedTextColor.GRAY)));

        for (PaymentRecord record : history) {
            boolean sent = record.fromUUID.equals(p.getUniqueId());

            Component line = Component.text("  ")
                    .append(Component.text(sent ? "▲ " : "▼ ",
                            sent ? NamedTextColor.RED : NamedTextColor.GREEN))
                    .append(Component.text(record.fromName, NamedTextColor.YELLOW))
                    .append(Component.text(" → ", NamedTextColor.GRAY))
                    .append(Component.text(record.toName, NamedTextColor.YELLOW))
                    .append(Component.text(": ", NamedTextColor.GRAY))
                    .append(Component.text(
                            Icons.COINS + " " + String.format("%,d", record.amount),
                            sent ? NamedTextColor.RED : NamedTextColor.GREEN))
                    .append(Component.text("  " + record.timestamp, NamedTextColor.DARK_GRAY));

            p.sendMessage(line);
        }
        return true;
    }
}
