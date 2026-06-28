package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
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
            Component setClick = ((TextComponent)((TextComponent)Component.text(Colors.PREFIX + "Are you sure you wish to set the spawn here? To confirm, please type ", NamedTextColor.GRAY).append(Component.text("/setspawn confirm", NamedTextColor.YELLOW))).hoverEvent(HoverEvent.showText(Component.text(Colors.YELLOW + "Click to confirm")))).clickEvent(ClickEvent.runCommand("/setspawn confirm"));
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
            p.sendMessage(Colors.PREFIX + Colors.GREEN + "Successfully set the spawn at your current location.");
            return true;
         } else {
            p.sendMessage(Colors.PREFIX + Colors.RED + "Usage: " + Colors.YELLOW + "/setspawn confirm");
            return true;
         }
      } else {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
   }
}
