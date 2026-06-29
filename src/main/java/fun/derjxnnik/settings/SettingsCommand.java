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
      ItemStack msgItem = new ItemStack(Material.PAPER);
      ItemMeta msgMeta = msgItem.getItemMeta();
      msgMeta.setDisplayName("§ePrivate Nachrichten: " + (msgEnabled ? "§aAN" : "§cAUS"));
      msgMeta.setLore(Arrays.asList("§7Klicke, um private Nachrichten zu " + (msgEnabled ? "§cdeaktivieren" : "§aaktivieren")));
      msgItem.setItemMeta(msgMeta);
      gui.setItem(0, msgItem);

      // Slot 2 – Sitzen
      boolean sitEnabled = settings.isSittingEnabled(p);
      ItemStack sitItem = new ItemStack(Material.OAK_STAIRS);
      ItemMeta sitMeta = sitItem.getItemMeta();
      sitMeta.setDisplayName("§eSitzen: " + (sitEnabled ? "§aAN" : "§cAUS"));
      sitMeta.setLore(Arrays.asList("§7Klicke, um das Sitzen zu " + (sitEnabled ? "§cdeaktivieren" : "§aaktivieren")));
      sitItem.setItemMeta(sitMeta);
      gui.setItem(2, sitItem);

      // Slot 4 – Rucksack-Item
      boolean bpEnabled = settings.hasBackpackEnabled(p);
      ItemStack bpItem = new ItemStack(Material.BUNDLE);
      ItemMeta bpMeta = bpItem.getItemMeta();
      bpMeta.setDisplayName("§eRucksack-Item: " + (bpEnabled ? "§aAN" : "§cAUS"));
      bpMeta.setLore(Arrays.asList("§7Klicke, um das Rucksack-Item zu " + (bpEnabled ? "§cdeaktivieren" : "§aaktivieren")));
      bpItem.setItemMeta(bpMeta);
      gui.setItem(4, bpItem);

      // Slot 6 – Rucksack-Farbe
      ItemStack colorItem = new ItemStack(Material.BLUE_DYE);
      ItemMeta colorMeta = colorItem.getItemMeta();
      colorMeta.setDisplayName("§eRucksack-Farbe");
      colorMeta.setLore(Arrays.asList("§7Aktuell: §f" + settings.getBackpackColor(p), "§7Klicke zum Ändern"));
      colorItem.setItemMeta(colorMeta);
      gui.setItem(6, colorItem);

      // Slot 8 – Guthaben anzeigen
      boolean showBalance = settings.isShowBalance(p);
      ItemStack balItem = new ItemStack(Material.GOLD_INGOT);
      ItemMeta balMeta = balItem.getItemMeta();
      balMeta.setDisplayName("§eGuthaben anzeigen: " + (showBalance ? "§aAN" : "§cAUS"));
      balMeta.setLore(Arrays.asList("§7Legt fest, ob andere Spieler",
              "§7dein Guthaben sehen dürfen."));
      balItem.setItemMeta(balMeta);
      gui.setItem(8, balItem);

      p.openInventory(gui);
      p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
      return true;
   }
}
