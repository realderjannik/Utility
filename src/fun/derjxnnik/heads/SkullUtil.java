package fun.derjxnnik.heads;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullUtil {
   public static ItemStack fromBase64(String base64, String displayName) {
      ItemStack head = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta)head.getItemMeta();
      if (meta != null) {
         PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), (String)null);
         profile.setProperty(new ProfileProperty("textures", base64));
         meta.setPlayerProfile(profile);
         if (displayName != null && !displayName.isEmpty()) {
            meta.setDisplayName(displayName);
         }

         head.setItemMeta(meta);
      }

      return head;
   }
}
