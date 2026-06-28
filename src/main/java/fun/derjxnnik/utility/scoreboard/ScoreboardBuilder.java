package fun.derjxnnik.utility.scoreboard;

import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Icons;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.utility.Utility;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

   public static Scoreboard build(Player p) {
      org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
      Scoreboard board = manager.getNewScoreboard();
      Objective obj = board.registerNewObjective("stats", "dummy", Component.text(Icons.LOGO, NamedTextColor.WHITE));
      obj.setDisplaySlot(DisplaySlot.SIDEBAR);

      FileConfiguration config = Utility.getInstance().getConfig();
      RankManager rankManager = Utility.getInstance().getRankManager();

      if (rankManager != null) {
         rankManager.applyAllNametagTeams(board);
      }

      if (rankManager != null && rankManager.isAvailable() && config.getBoolean("ranks.show-in-scoreboard", true)) {
         String prefix = rankManager.getPrefix(p);
         if (!prefix.isEmpty()) {
            String label = config.getString("ranks.scoreboard-label", "Rang");
            setEntry(obj, "§r", 7,
                    Component.text(label + ": ", NamedTextColor.GRAY).font(SC)
                            .append(LEGACY.deserialize(prefix)));
         }
      }

      LocalTime now = LocalTime.now();
      String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
      int kills = p.getStatistic(Statistic.PLAYER_KILLS);
      int deaths = p.getStatistic(Statistic.DEATHS);

      setEntry(obj, "§a", 6, Component.empty());
      setEntry(obj, "§b", 5, sc(Icons.TIME,     " Zeit ",      time,                   NamedTextColor.WHITE));
      setEntry(obj, "§c", 4, sc(Icons.KILLS,    " Kills ",     String.valueOf(kills),  NamedTextColor.WHITE));
      setEntry(obj, "§d", 3, sc(Icons.DEATHS,   " Tode ",      String.valueOf(deaths), NamedTextColor.WHITE));
      setEntry(obj, "§e", 2, sc(Icons.PLAYTIME, " Spielzeit ", getPlaytime(p),         NamedTextColor.WHITE));
      setEntry(obj, "§1", 1, Component.empty());
      setEntry(obj, "§f", 0, sc(Icons.PING,     " Ping ",      p.getPing() + "ms",     pingColor(p.getPing())));

      return board;
   }

   private static Component sc(String icon, String label, String value, NamedTextColor valueColor) {
      return Component.text(icon, NamedTextColor.WHITE)
              .append(Component.text(label, NamedTextColor.GRAY).font(SC))
              .append(Component.text(value, valueColor).font(SC));
   }

   private static NamedTextColor pingColor(int ping) {
      if (ping <= 60) return NamedTextColor.GREEN;
      if (ping <= 150) return NamedTextColor.YELLOW;
      return NamedTextColor.RED;
   }

   /** Sets a score entry with a custom Component display name (Paper API). Falls back to legacy string if unavailable. */
   private static void setEntry(Objective obj, String key, int score, Component display) {
      Score s = obj.getScore(key);
      s.setScore(score);
      try {
         s.customName(display);
      } catch (NoSuchMethodError ignored) { }
   }

   private static String getPlaytime(Player p) {
      int total = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
      int days = total / 1440;
      int hours = total % 1440 / 60;
      int minutes = total % 60;
      return days + "d " + hours + "h " + minutes + "m";
   }
}
