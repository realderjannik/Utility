package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BugreportCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         p.sendMessage(((TextComponent)Component.text(Colors.PREFIX).append(Component.text("Report bugs at: ", NamedTextColor.GRAY))).append(((TextComponent)Component.text("derjxnnik.fun/bugreport", NamedTextColor.YELLOW).clickEvent(ClickEvent.openUrl("https://derjxnnik.fun/bugreport.html"))).hoverEvent(Component.text("Click to open bug report page", NamedTextColor.GRAY))));
         return true;
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}
