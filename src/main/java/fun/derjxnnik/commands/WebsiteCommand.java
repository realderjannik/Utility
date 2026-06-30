package fun.derjxnnik.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WebsiteCommand implements CommandExecutor {

   private static final String URL = "https://dasumc.de";

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      sender.sendMessage(Component.text("[SMP] ", NamedTextColor.DARK_AQUA)
              .append(Component.text("Website: ", NamedTextColor.GRAY))
              .append(Component.text(URL, NamedTextColor.AQUA)
                      .decorate(TextDecoration.UNDERLINED)
                      .clickEvent(ClickEvent.openUrl(URL))
                      .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Öffnen", NamedTextColor.YELLOW)))));
      return true;
   }
}
