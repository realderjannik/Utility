package fun.derjxnnik.misc;

import org.bukkit.plugin.java.JavaPlugin;

public class LogUtil {
   private static JavaPlugin plugin;

   public static void init(JavaPlugin pl) {
      plugin = pl;
   }

   private static String stripColors(String message) {
      return message.replaceAll("§.", "");
   }

   public static void info(String message) {
      plugin.getLogger().info(stripColors(Colors.PREFIX + message));
   }

   public static void warn(String message) {
      plugin.getLogger().warning(stripColors(Colors.PREFIX + message));
   }

   public static void error(String message, Throwable ex) {
      plugin.getLogger().severe(stripColors(Colors.PREFIX + message));
      if (ex != null) {
         ex.printStackTrace();
      }

   }
}
