package fun.derjxnnik.utility.scoreboard;

import fun.derjxnnik.currency.CurrencyManager;
import fun.derjxnnik.misc.Icons;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.utility.Utility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardBuilder {

   private static final Key SC = Key.key("minecraft", "small_caps");

   public static Scoreboard build(Player p) {
      Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
      Objective obj = board.registerNewObjective(
              "stats", "dummy",
              Component.text(Icons.LOGO, NamedTextColor.WHITE));
      obj.setDisplaySlot(DisplaySlot.SIDEBAR);

      FileConfiguration config = Utility.getInstance().getConfig();
      RankManager rankManager  = Utility.getInstance().getRankManager();
      CurrencyManager currency = Utility.getInstance().getCurrencyManager();

      if (rankManager != null) rankManager.applyAllNametagTeams(board);

      boolean showRank = rankManager != null && rankManager.isAvailable()
              && config.getBoolean("ranks.show-in-scoreboard", true);

      // Score 9: blank
      setEntry(obj, "§0", 9, Component.empty());

      // Score 8: RANG — only entry with label box
      Component rangLine;
      if (showRank) {
         Component prefix = rankManager.buildGradientPrefix(p);
         rangLine = prefix.equals(Component.empty())
                 ? Component.text("-", NamedTextColor.GRAY).font(SC)
                 : prefix;
      } else {
         rangLine = Component.text("-", NamedTextColor.GRAY).font(SC);
      }
      setEntry(obj, "§1", 8, rangLine);

      // Score 7: blank
      setEntry(obj, "§2", 7, Component.empty());

      // Score 6: COINS
      long coins = currency != null ? currency.getBalance(p.getUniqueId()) : 0;
      setEntry(obj, "§3", 6, row(Icons.COINS, NamedTextColor.YELLOW, "coins",
              String.format("%,d", coins), NamedTextColor.YELLOW));

      // Score 5: KILLS
      int kills = p.getStatistic(Statistic.PLAYER_KILLS);
      setEntry(obj, "§4", 5, row(Icons.KILLS, NamedTextColor.WHITE, "kills",
              String.valueOf(kills), NamedTextColor.WHITE));

      // Score 4: DEATHS
      int deaths = p.getStatistic(Statistic.DEATHS);
      setEntry(obj, "§5", 4, row(Icons.DEATHS, NamedTextColor.WHITE, "deaths",
              String.valueOf(deaths), NamedTextColor.WHITE));

      // Score 3: SPIELZEIT
      setEntry(obj, "§6", 3, row(Icons.PLAYTIME, NamedTextColor.WHITE, "spielzeit",
              getPlaytime(p), NamedTextColor.WHITE));

      // Score 2: PING
      int ping = p.getPing();
      NamedTextColor pc = pingColor(ping);
      setEntry(obj, "§7", 2, row(Icons.PING, pc, "ping", ping + "ms", pc));

      // Score 1: blank
      setEntry(obj, "§8", 1, Component.empty());

      // Score 0: ONLINE — uses clan icon (U+E006)
      int online = Bukkit.getOnlinePlayers().size();
      int max    = Bukkit.getMaxPlayers();
      setEntry(obj, "§9", 0, row(Icons.ONLINE, NamedTextColor.WHITE, "online",
              online + "/" + max, NamedTextColor.WHITE));

      return board;
   }

   /** Icon in default font, lowercase label + value in small_caps. */
   private static Component row(String icon, NamedTextColor iconColor,
                                String label, String value, NamedTextColor valueColor) {
      return Component.text(icon, iconColor)
              .append(Component.text(" " + label + " ", NamedTextColor.GRAY).font(SC))
              .append(Component.text(value, valueColor).font(SC));
   }

   private static NamedTextColor pingColor(int ping) {
      if (ping <= 60)  return NamedTextColor.GREEN;
      if (ping <= 150) return NamedTextColor.YELLOW;
      return NamedTextColor.RED;
   }

   private static void setEntry(Objective obj, String key, int score, Component display) {
      Score s = obj.getScore(key);
      s.setScore(score);
      try { s.customName(display); } catch (NoSuchMethodError ignored) {}
   }

   private static String getPlaytime(Player p) {
      int total   = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
      int days    = total / 1440;
      int hours   = total % 1440 / 60;
      int minutes = total % 60;
      return days + "d " + hours + "h " + minutes + "m";
   }
}
