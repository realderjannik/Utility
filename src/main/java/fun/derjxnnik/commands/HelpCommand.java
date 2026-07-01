package fun.derjxnnik.commands;

import fun.derjxnnik.misc.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements CommandExecutor {

   private static final String LINE = Colors.DARK_AQUA + "═══════════════════════════";

   @Override
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                            @NotNull String label, @NotNull String[] args) {
      if (!(sender instanceof Player p)) {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }
      sendHelp(p);
      return true;
   }

   private void sendHelp(Player p) {
      p.sendMessage(LINE);
      p.sendMessage(Colors.BOLD_DARK_AQUA + "  DASUSMP " + Colors.GRAY + "— Befehle");
      p.sendMessage(LINE);

      // Navigation
      entry(p, "/home",                       "Homes verwalten");
      entry(p, "/spawn",                      "Zum Spawn teleportieren");
      entry(p, "/bp",                         "Rucksack öffnen");
      entry(p, "/ec",                         "Enderchest öffnen");
      entry(p, "/craft",                      "Handwerksstation öffnen");
      entry(p, "/tpa <Spieler>",              "Teleportanfrage senden");
      entry(p, "/tpaccept / /tpdeny",         "Anfrage annehmen / ablehnen");

      // Info
      p.sendMessage("");
      entry(p, "/info <Spieler>",             "Spielerinfo anzeigen");
      entry(p, "/ping",                       "Deinen Ping anzeigen");
      entry(p, "/playtime [Spieler]",         "Spielzeit anzeigen");
      entry(p, "/lastdeath",                  "Letzten Tod anzeigen");
      entry(p, "/day",                        "Aktuellen Spieltag anzeigen");
      entry(p, "/uuid [Spieler]",             "UUID anzeigen");

      // Economy
      p.sendMessage("");
      entry(p, "/coins [Spieler]",            "Kontostand anzeigen");
      entry(p, "/pay <Spieler> <Betrag>",     "Coins überweisen");
      entry(p, "/payments",                   "Transaktionshistorie");

      // Communication
      p.sendMessage("");
      entry(p, "/msg <Spieler> <Nachricht>",  "Private Nachricht senden");
      entry(p, "/r <Nachricht>",              "Auf letzte Nachricht antworten");

      // Tools
      p.sendMessage("");
      entry(p, "/rename <Name>",              "Item in der Hand umbenennen");
      entry(p, "/hat",                        "Item als Hut tragen");
      entry(p, "/color",                      "Chat-Farben anzeigen");
      entry(p, "/bugreport",                  "Bug melden");
      entry(p, "/settings",                   "Einstellungen öffnen");

      // Staff section
      if (p.hasPermission("utility.help.staff")) {
         p.sendMessage(LINE);
         p.sendMessage(Colors.RED + Colors.BOLD_RED + "  Team-Befehle");
         p.sendMessage(LINE);
         entry(p, "/ban <Spieler> <Grund> [Zeit]",          "Spieler bannen");
         entry(p, "/unban <Spieler>",                      "Spieler entbannen");
         entry(p, "/mute <Spieler> <Grund> <Zeit>",        "Spieler stummschalten");
         entry(p, "/unmute <Spieler>",                     "Stummschaltung aufheben");
         entry(p, "/warn <Spieler> <Grund>",               "Spieler verwarnen");
         entry(p, "/warnlist <Spieler>",                   "Verwarnungen anzeigen");
         entry(p, "/clearwarns <Spieler>",                 "Verwarnungen löschen");
         entry(p, "/sc [Nachricht]",                       "Staff-Chat senden / umschalten");
         entry(p, "/vanish",                               "Unsichtbar werden");
         entry(p, "/freeze <Spieler>",                     "Spieler einfrieren");
         entry(p, "/invsee <Spieler>",                     "Inventar einsehen");
         entry(p, "/setspawn [confirm]",                   "Spawn setzen");
         entry(p, "/utility reload",                       "Plugin neu laden");
      }

      p.sendMessage(LINE);
   }

   private void entry(Player p, String command, String desc) {
      p.sendMessage(Colors.DARK_AQUA + command + Colors.DARK_GRAY + " — " + Colors.GRAY + desc);
   }
}
