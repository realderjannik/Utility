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

public class TwitchCommand implements CommandExecutor {

   private static final String URL = "https://dasumc.de/twitch";

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      sender.sendMessage(Component.text("[SMP] ", NamedTextColor.DARK_AQUA)
              .append(Component.text("Du hast Interesse an dem ", NamedTextColor.GRAY))
              .append(Component.text("Twitch ", NamedTextColor.DARK_PURPLE))
              .append(Component.text("Rang?\n", NamedTextColor.GRAY))
              .append(Component.text("Sende uns deine Bewerbung unter: ", NamedTextColor.GRAY))
              .append(Component.text(URL, NamedTextColor.AQUA)
                      .decorate(TextDecoration.UNDERLINED)
                      .clickEvent(ClickEvent.openUrl(URL))
                      .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Öffnen", NamedTextColor.YELLOW)))));
      return true;
   }
}
