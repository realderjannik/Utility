package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.utility.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UUIDCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         FileConfiguration config = Utility.getInstance().getConfig();
         if (!config.getBoolean("use-command.uuid", true)) {
            p.sendMessage(Colors.DISABLED);
            return true;
         } else if (args.length == 0) {
            Component uuidMsgPlayer = ((TextComponent)Component.text(Colors.PREFIX + Colors.GREEN + p.getName() + "'s UUID: ", NamedTextColor.GRAY).append(Component.text(p.getUniqueId().toString(), NamedTextColor.WHITE).hoverEvent(HoverEvent.showText(Component.text("Click to copy"))))).clickEvent(ClickEvent.copyToClipboard(p.getUniqueId().toString()));
            p.sendMessage(uuidMsgPlayer);
            return true;
         } else if (args.length == 1) {
            OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
            if (t.getName() != null) {
               Component uuidMsgTarget = ((TextComponent)Component.text(Colors.PREFIX + Colors.GREEN + t.getName() + "'s UUID: ", NamedTextColor.GRAY).append(Component.text(t.getUniqueId().toString(), NamedTextColor.WHITE).hoverEvent(HoverEvent.showText(Component.text("Click to copy"))))).clickEvent(ClickEvent.copyToClipboard(t.getUniqueId().toString()));
               p.sendMessage(uuidMsgTarget);
            } else {
               sender.sendMessage(Colors.PREFIX + Colors.RED + "Player not found");
            }

            return true;
         } else {
            if (args.length > 2) {
               sender.sendMessage(Colors.PREFIX + Colors.RED + "Usage: /uuid <player>");
            }

            return true;
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}
