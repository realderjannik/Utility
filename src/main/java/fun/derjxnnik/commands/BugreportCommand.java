package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
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
         p.sendMessage(((TextComponent)Component.text(Colors.PREFIX).append(Component.text(Messages.BUGREPORT_LINK_TEXT, NamedTextColor.GRAY))).append(((TextComponent)Component.text(Messages.BUGREPORT_LINK_URL, NamedTextColor.YELLOW).clickEvent(ClickEvent.openUrl("https://derjxnnik.fun/bugreport.html"))).hoverEvent(Component.text(Messages.BUGREPORT_LINK_HOVER, NamedTextColor.GRAY))));
         return true;
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}
