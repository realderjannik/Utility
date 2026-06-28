package fun.derjxnnik.utility.scoreboard;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Icons;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.utility.Utility;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardBuilder {
   public static Scoreboard build(Player p) {
      org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
      Scoreboard board = manager.getNewScoreboard();
      Objective obj = board.registerNewObjective("stats", "dummy", Component.text(Icons.LOGO, NamedTextColor.WHITE));
      obj.setDisplaySlot(DisplaySlot.SIDEBAR);

      FileConfiguration config = Utility.getInstance().getConfig();
      RankManager rankManager = Utility.getInstance().getRankManager();

      // Register nametag teams for all online players on this board
      if (rankManager != null) {
         rankManager.applyAllNametagTeams(board);
      }

      // Rank line (score 7, above the spacer at 6)
      if (rankManager != null && rankManager.isAvailable() && config.getBoolean("ranks.show-in-scoreboard", true)) {
         String prefix = rankManager.getPrefix(p);
         if (!prefix.isEmpty()) {
            String label = config.getString("ranks.scoreboard-label", "Rang");
            obj.getScore(Colors.GRAY + label + ": " + prefix).setScore(7);
         }
      }

      LocalTime now = LocalTime.now();
      String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
      int kills = p.getStatistic(Statistic.PLAYER_KILLS);
      int deaths = p.getStatistic(Statistic.DEATHS);
      obj.getScore("").setScore(6);
      obj.getScore(Colors.WHITE + Icons.TIME     + Colors.DARK_AQUA + " Zeit "     + Colors.WHITE + time).setScore(5);
      obj.getScore(Colors.WHITE + Icons.KILLS    + Colors.GREEN     + " Kills "    + Colors.WHITE + kills).setScore(4);
      obj.getScore(Colors.WHITE + Icons.DEATHS   + Colors.RED       + " Tode "     + Colors.WHITE + deaths).setScore(3);
      obj.getScore(Colors.WHITE + Icons.PLAYTIME + Colors.YELLOW    + " Spielzeit "+ Colors.WHITE + getPlaytime(p)).setScore(2);
      obj.getScore("§1").setScore(1);
      obj.getScore(Colors.WHITE + Icons.PING     + Colors.GRAY      + " Ping "     + Colors.GREEN + p.getPing() + Colors.GRAY + "ms").setScore(0);
      return board;
   }

   private static String getPlaytime(Player p) {
      int total = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
      int days = total / 1440;
      int hours = total % 1440 / 60;
      int minutes = total % 60;
      return days + "d " + hours + "h " + minutes + "m";
   }
}
