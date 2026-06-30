package fun.derjxnnik.utility.scoreboard;

import fun.derjxnnik.currency.CurrencyManager;
import fun.derjxnnik.misc.Icons;
import fun.derjxnnik.rank.RankManager;
import java.util.Map;
import fun.derjxnnik.utility.Utility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class ScoreboardBuilder {

   private static final MiniMessage MM = MiniMessage.miniMessage();
   private static final String[] KEYS = buildKeys();

   // Maps config label text → header texture PUA char (used when use-textures=true)
   private static final Map<String, String> LABEL_TEX = Map.of(
           "rang:",      Icons.HEADER_RANK,
           "coins:",     Icons.HEADER_MONEY,
           "kills:",     Icons.HEADER_KILLS,
           "tode:",      Icons.HEADER_DEATHS,
           "clan:",      Icons.HEADER_CLAN
   );

   private static String[] buildKeys() {
      String hex = "0123456789abcdef";
      String[] k = new String[256];
      for (int i = 0; i < 16; i++)  k[i]      = "§" + hex.charAt(i);
      for (int i = 16; i < 256; i++) k[i]     = "§" + hex.charAt(i % 16) + "§" + hex.charAt(i / 16);
      return k;
   }

   public static Scoreboard build(Player p) {
      Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
      FileConfiguration cfg = Utility.getInstance().getConfig();
      RankManager rm  = Utility.getInstance().getRankManager();
      CurrencyManager cm = Utility.getInstance().getCurrencyManager();

      if (rm != null) rm.applyAllNametagTeams(board);

      Objective obj = board.registerNewObjective("stats", "dummy", buildHeader(cfg));
      obj.setDisplaySlot(DisplaySlot.SIDEBAR);

      // Resolve variable values once per build
      boolean showRank = rm != null && rm.isAvailable()
              && cfg.getBoolean("ranks.show-in-scoreboard", true);
      Component rankComp = showRank ? rm.buildGradientPrefix(p) : null;
      if (rankComp == null || rankComp.equals(Component.empty()))
         rankComp = scLabel(cfg, "-");

      long coins = cm != null ? cm.getBalance(p.getUniqueId()) : 0;
      int kills  = p.getStatistic(Statistic.PLAYER_KILLS);
      int deaths = p.getStatistic(Statistic.DEATHS);
      String pt  = getPlaytime(p);
      int ping   = p.getPing();
      int online = Bukkit.getOnlinePlayers().size();
      int max    = Bukkit.getMaxPlayers();

      List<String> lines = cfg.getStringList("scoreboard.lines");
      int total = lines.size();

      for (int i = 0; i < total && i < KEYS.length; i++) {
         Component display = resolve(lines.get(i), cfg, rankComp,
                 coins, kills, deaths, pt, ping, online, max);
         Score s = obj.getScore(KEYS[i]);
         s.setScore(total - 1 - i);
         try { s.customName(display); } catch (NoSuchMethodError ignored) {}
      }

      return board;
   }

   // ── Header ─────────────────────────────────────────────────────────────

   private static Component buildHeader(FileConfiguration cfg) {
      if (cfg.getBoolean("scoreboard.use-textures", false))
         return Component.text(Icons.HEADER_GENERAL);

      String text = cfg.getString("scoreboard.header.text", "dasumc.de");
      List<String> grad = cfg.getStringList("scoreboard.header.gradient");
      return grad.size() >= 2
              ? gradComp(text, grad, null)
              : Component.text(text, TextColor.fromHexString("#FF44FF"));
   }

   // ── Line resolver ───────────────────────────────────────────────────────

   private static Component resolve(String line, FileConfiguration cfg, Component rankComp,
                                    long coins, int kills, int deaths, String pt,
                                    int ping, int online, int max) {
      if (line.isEmpty()) return Component.empty();

      // Raw MiniMessage — user has full control, no auto-formatting applied
      if (line.startsWith("<")) return MM.deserialize(line);

      boolean tex = cfg.getBoolean("scoreboard.use-textures", false);

      // Label texture replacement (use-textures=true only)
      if (tex && LABEL_TEX.containsKey(line))
         return Component.text(LABEL_TEX.get(line));


      // Exact variable → (optional icon) + value (small_caps + config gradient)
      switch (line) {
         case "%rank%": return rankComp;

         case "%coins%":
            return tex
                    ? iconComp(Icons.COINS, NamedTextColor.YELLOW).append(valComp(String.format("%,d", coins), "coins", cfg))
                    : valComp(String.format("%,d", coins), "coins", cfg);

         case "%kills%":
            return tex
                    ? iconComp(Icons.KILLS, NamedTextColor.WHITE).append(valComp(String.valueOf(kills), "kills", cfg))
                    : valComp(String.valueOf(kills), "kills", cfg);

         case "%deaths%":
            return tex
                    ? iconComp(Icons.DEATHS, NamedTextColor.WHITE).append(valComp(String.valueOf(deaths), "deaths", cfg))
                    : valComp(String.valueOf(deaths), "deaths", cfg);

         case "%spielzeit%":
            return tex
                    ? iconComp(Icons.PLAYTIME, NamedTextColor.WHITE).append(valComp(pt, "spielzeit", cfg))
                    : valComp(pt, "spielzeit", cfg);

         case "%ping%": {
            NamedTextColor pc = pingColor(ping);
            return tex
                    ? iconComp(Icons.PING, pc).append(Component.text(ping + "ms", pc))
                    : Component.text(ping + "ms", pc);
         }
      }

      // Inline variable replacement (e.g. "%online%/%maxplayers%")
      String resolved = line
              .replace("%online%",     String.valueOf(online))
              .replace("%maxplayers%", String.valueOf(max))
              .replace("%kills%",      String.valueOf(kills))
              .replace("%deaths%",     String.valueOf(deaths))
              .replace("%spielzeit%",  pt)
              .replace("%ping%",       ping + "ms")
              .replace("%coins%",      String.format("%,d", coins));

      if (line.contains("%online%"))
         return tex
                 ? iconComp(Icons.ONLINE, NamedTextColor.WHITE).append(valComp(resolved, "online", cfg))
                 : valComp(resolved, "online", cfg);

      // Plain label or static text → small_caps + label-color
      return scLabel(cfg, resolved);
   }

   // ── Component helpers ───────────────────────────────────────────────────

   private static Component iconComp(String icon, NamedTextColor color) {
      return Component.text(icon + " ", color);
   }

   private static Component scLabel(FileConfiguration cfg, String text) {
      String hex = cfg.getString("scoreboard.label-color", "#777777");
      TextColor c = TextColor.fromHexString(hex);
      if (c == null) c = TextColor.fromHexString("#777777");
      return Component.text(text, c);
   }

   private static Component valComp(String text, String key, FileConfiguration cfg) {
      List<String> colors = cfg.getStringList("scoreboard.value-colors." + key);
      if (colors.size() >= 2) return gradComp(text, colors, null);
      String single = cfg.getString("scoreboard.value-colors." + key);
      if (single != null && single.startsWith("#")) {
         TextColor c = TextColor.fromHexString(single);
         if (c != null) return Component.text(text, c);
      }
      return Component.text(text, NamedTextColor.WHITE);
   }

   private static Component gradComp(String text, List<String> colors, Key font) {
      StringBuilder mm = new StringBuilder();
      if (font != null) mm.append("<font:").append(font.asString()).append('>');
      mm.append("<gradient:");
      for (int i = 0; i < colors.size(); i++) {
         if (i > 0) mm.append(':');
         mm.append(colors.get(i));
      }
      mm.append('>').append(text).append("</gradient>");
      if (font != null) mm.append("</font>");
      return MM.deserialize(mm.toString());
   }

   private static NamedTextColor pingColor(int ping) {
      if (ping <= 60)  return NamedTextColor.GREEN;
      if (ping <= 150) return NamedTextColor.YELLOW;
      return NamedTextColor.RED;
   }

   private static String getPlaytime(Player p) {
      int total   = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
      int days    = total / 1440;
      int hours   = total % 1440 / 60;
      int minutes = total % 60;
      return days + "d " + hours + "h " + minutes + "m";
   }
}
