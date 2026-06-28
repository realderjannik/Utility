package fun.derjxnnik.commands;

import fun.derjxnnik.currency.CurrencyManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Icons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoinsCommand implements CommandExecutor {

    private final CurrencyManager currency;

    public CoinsCommand(CurrencyManager currency) {
        this.currency = currency;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Colors.CONSOLE_SENDER);
            return true;
        }

        long balance = currency.getBalance(p.getUniqueId());

        p.sendMessage(Component.text(Colors.PREFIX)
                .append(Component.text("Deine Coins: ", NamedTextColor.GRAY))
                .append(Component.text(Icons.COINS + " ", NamedTextColor.GOLD))
                .append(Component.text(String.format("%,d", balance), NamedTextColor.YELLOW)));
        return true;
    }
}
