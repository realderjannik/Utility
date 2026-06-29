package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand implements CommandExecutor {
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] args) {
      if (sender instanceof Player p) {
         if (!p.hasPermission("utility.setspawn")) {
            p.sendMessage(Colors.NO_PERMS);
            return true;
         } else if (args.length == 0) {
            Component setClick = ((TextComponent)((TextComponent)Component.text(Colors.PREFIX + Messages.SETSPAWN_BESTAETIGEN_TEXT, NamedTextColor.GRAY).append(Component.text(Messages.SETSPAWN_BESTAETIGEN_CMD, NamedTextColor.YELLOW))).hoverEvent(HoverEvent.showText(Component.text(Messages.SETSPAWN_BESTAETIGEN_HOVER)))).clickEvent(ClickEvent.runCommand("/setspawn confirm"));
            p.sendMessage(setClick);
            return true;
         } else if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            World w = p.getWorld();
            Location l = p.getLocation();
            double x = (double)l.getBlockX();
            double y = (double)l.getBlockY();
            double z = (double)l.getBlockZ();
            Location newSpawn = new Location(w, x, y, z);
            w.setSpawnLocation(newSpawn);
            p.sendMessage(Messages.SETSPAWN_GESETZT);
            return true;
         } else {
            p.sendMessage(Messages.SETSPAWN_NUTZUNG);
            return true;
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}
