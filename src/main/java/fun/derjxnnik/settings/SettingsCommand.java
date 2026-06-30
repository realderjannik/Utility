package fun.derjxnnik.settings;

import fun.derjxnnik.misc.Colors;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsCommand implements CommandExecutor {

   private final SettingsManager settings;

   public SettingsCommand(SettingsManager settings) {
      this.settings = settings;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!(sender instanceof Player p)) {
         sender.sendMessage(Colors.CONSOLE_SENDER);
         return true;
      }

      Inventory gui = Bukkit.createInventory((InventoryHolder) null, 9, "§8Einstellungen");

      // Slot 0 – Private Nachrichten
      boolean msgEnabled = settings.isMsgEnabled(p);
      ItemStack msgItem = makeItem(Material.PAPER,
              "§ePrivate Nachrichten: " + state(msgEnabled),
              "§7Klicke, um private Nachrichten zu " + toggle(msgEnabled));
      gui.setItem(0, msgItem);

      // Slot 2 – Rucksack-Item
      boolean bpEnabled = settings.hasBackpackEnabled(p);
      ItemStack bpItem = makeItem(Material.BUNDLE,
              "§eRucksack-Item: " + state(bpEnabled),
              "§7Klicke, um das Rucksack-Item zu " + toggle(bpEnabled));
      gui.setItem(2, bpItem);

      // Slot 4 – Rucksack-Farbe
      ItemStack colorItem = makeItem(Material.BLUE_DYE,
              "§eRucksack-Farbe",
              "§7Aktuell: §f" + settings.getBackpackColor(p),
              "§7Klicke zum Ändern");
      gui.setItem(4, colorItem);

      // Slot 6 – Guthaben anzeigen
      boolean showBalance = settings.isShowBalance(p);
      ItemStack balItem = makeItem(Material.GOLD_INGOT,
              "§eGuthaben anzeigen: " + state(showBalance),
              "§7Legt fest, ob andere Spieler",
              "§7dein Guthaben sehen dürfen.");
      gui.setItem(6, balItem);

      // Slot 8 – Profil sichtbar (/info)
      boolean infoVisible = settings.isInfoVisible(p);
      ItemStack infoItem = makeItem(Material.PLAYER_HEAD,
              "§eProfil sichtbar: " + state(infoVisible),
              "§7Legt fest, ob andere Spieler",
              "§7dein Profil per /info sehen dürfen.");
      gui.setItem(8, infoItem);

      p.openInventory(gui);
      p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
      return true;
   }

   private ItemStack makeItem(Material mat, String name, String... lore) {
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(name);
      meta.setLore(Arrays.asList(lore));
      item.setItemMeta(meta);
      return item;
   }

   private String state(boolean on) { return on ? "§aAN" : "§cAUS"; }
   private String toggle(boolean on) { return on ? "§cdeaktivieren" : "§aaktivieren"; }
}
