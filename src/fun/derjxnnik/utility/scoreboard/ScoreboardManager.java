package fun.derjxnnik.utility.scoreboard;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
   private static final Map boards = new HashMap();

   public static void set(Player p) {
      Scoreboard board = ScoreboardBuilder.build(p);
      boards.put(p.getUniqueId(), board);
      p.setScoreboard(board);
   }

   public static void update(Player p) {
      if (boards.containsKey(p.getUniqueId())) {
         set(p);
      }
   }

   public static void updateAll() {
      for(Player p : Bukkit.getOnlinePlayers()) {
         update(p);
      }

   }

   public static void remove(Player p) {
      boards.remove(p.getUniqueId());
   }
}
