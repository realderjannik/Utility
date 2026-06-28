package fun.derjxnnik.misc;

import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryUtil {
   private static final List PLUGIN_GUI_KEYWORDS = Arrays.asList("Home", "Head", "Spawn", "Utility", "Your Settings");

   public static boolean isPluginInventory(InventoryClickEvent event) {
      String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
      if (!title.contains("Select Backpack Color") && !title.contains("Backpack Color")) {
         for(String keyword : PLUGIN_GUI_KEYWORDS) {
            if (title.equalsIgnoreCase(keyword) || title.contains(keyword + " ")) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
