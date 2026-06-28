package fun.derjxnnik.misc;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Material;

public class StairsUtil {
   private static final List STAIRS = (List)List.of(Material.values()).stream().filter((mat) -> mat.name().endsWith("_STAIRS")).collect(Collectors.toList());

   public static boolean isStair(Material mat) {
      return STAIRS.contains(mat);
   }

   public static List getAllStairs() {
      return STAIRS;
   }
}
