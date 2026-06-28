package fun.derjxnnik.commands;

import fun.derjxnnik.chat.ColorManager;
import fun.derjxnnik.misc.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class ColorCommand implements CommandExecutor {

    // {id, displayName, §-code, NamedTextColor}
    public static final Object[][] COLORS = {
        {"black",        "Black",        "§0", NamedTextColor.BLACK},
        {"dark_blue",    "Dark Blue",    "§1", NamedTextColor.DARK_BLUE},
        {"dark_green",   "Dark Green",   "§2", NamedTextColor.DARK_GREEN},
        {"dark_aqua",    "Dark Aqua",    "§3", NamedTextColor.DARK_AQUA},
        {"dark_red",     "Dark Red",     "§4", NamedTextColor.DARK_RED},
        {"dark_purple",  "Dark Purple",  "§5", NamedTextColor.DARK_PURPLE},
        {"gold",         "Gold",         "§6", NamedTextColor.GOLD},
        {"gray",         "Gray",         "§7", NamedTextColor.GRAY},
        {"dark_gray",    "Dark Gray",    "§8", NamedTextColor.DARK_GRAY},
        {"blue",         "Blue",         "§9", NamedTextColor.BLUE},
        {"green",        "Green",        "§a", NamedTextColor.GREEN},
        {"aqua",         "Aqua",         "§b", NamedTextColor.AQUA},
        {"red",          "Red",          "§c", NamedTextColor.RED},
        {"light_purple", "Light Purple", "§d", NamedTextColor.LIGHT_PURPLE},
        {"yellow",       "Yellow",       "§e", NamedTextColor.YELLOW},
        {"white",        "White",        "§f", NamedTextColor.WHITE},
    };

    // {id, displayName, §-code, TextDecoration}
    public static final Object[][] FORMATS = {
        {"bold",          "Bold",          "§l", TextDecoration.BOLD},
        {"italic",        "Italic",        "§o", TextDecoration.ITALIC},
        {"underline",     "Underline",     "§n", TextDecoration.UNDERLINED},
        {"obfuscated",    "Obfuscated",    "§k", TextDecoration.OBFUSCATED},
        {"strikethrough", "Strikethrough", "§m", TextDecoration.STRIKETHROUGH},
    };

    private final ColorManager colorManager;

    public ColorCommand(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Colors.CONSOLE_SENDER);
            return true;
        }

        if (args.length == 0) {
            sendPage(p, 1);
            return true;
        }

        String arg = args[0].toLowerCase();

        if (arg.equals("2") || arg.equals("format") || arg.equals("formats") || arg.equals("formatting")) {
            sendPage(p, 2);
            return true;
        }
        if (arg.equals("1") || arg.equals("colors")) {
            sendPage(p, 1);
            return true;
        }
        if (arg.equals("default") || arg.equals("reset")) {
            colorManager.reset(p);
            p.sendMessage(Component.text("[Utility] ", NamedTextColor.DARK_AQUA)
                .append(Component.text("Chat-Format zurückgesetzt.", NamedTextColor.GREEN)));
            return true;
        }

        for (Object[] c : COLORS) {
            if (arg.equals(c[0])) {
                String perm = "utility.chat.color." + c[0];
                if (!p.hasPermission(perm) && !p.hasPermission("utility.chat.color.*")) {
                    p.sendMessage(Colors.NO_PERMS);
                    return true;
                }
                colorManager.setColor(p, (String) c[2]);
                p.sendMessage(Component.text("[Utility] ", NamedTextColor.DARK_AQUA)
                    .append(Component.text("Standard-Farbe: ", NamedTextColor.GRAY))
                    .append(Component.text((String) c[1], (NamedTextColor) c[3])));
                return true;
            }
        }

        for (Object[] f : FORMATS) {
            if (arg.equals(f[0])) {
                String perm = "utility.chat.format." + f[0];
                if (!p.hasPermission(perm) && !p.hasPermission("utility.chat.format.*")) {
                    p.sendMessage(Colors.NO_PERMS);
                    return true;
                }
                boolean nowActive = colorManager.toggleFormat(p, (String) f[2]);
                p.sendMessage(Component.text("[Utility] ", NamedTextColor.DARK_AQUA)
                    .append(Component.text("Format ", NamedTextColor.GRAY))
                    .append(Component.text((String) f[1], NamedTextColor.WHITE).decorate((TextDecoration) f[3]))
                    .append(Component.text(nowActive ? " aktiviert." : " deaktiviert.", NamedTextColor.GRAY)));
                return true;
            }
        }

        p.sendMessage(Component.text("[Utility] ", NamedTextColor.DARK_AQUA)
            .append(Component.text("Unbekannte Farbe/Format. Nutze ", NamedTextColor.RED))
            .append(Component.text("/color", NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.runCommand("/color")))
            .append(Component.text(" für eine Übersicht.", NamedTextColor.RED)));
        return true;
    }

    private void sendPage(Player p, int page) {
        p.sendMessage(Component.empty());
        p.sendMessage(Component.text(" Chat " + (page == 1 ? "Colors" : "Formatting"), NamedTextColor.DARK_AQUA, TextDecoration.BOLD)
            .append(Component.text(" (Page " + page + "/2)", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)));
        p.sendMessage(Component.empty());

        if (page == 1) {
            sendColorGrid(p);
        } else {
            sendFormatList(p);
        }

        p.sendMessage(Component.empty());
        sendCurrentStatus(p);
        p.sendMessage(Component.empty());
        sendNavigation(p, page);
        p.sendMessage(Component.empty());
    }

    private void sendColorGrid(Player p) {
        String currentColor = colorManager.getDefaultColor(p.getUniqueId());
        boolean allColors = p.hasPermission("utility.chat.color.*");
        int cols = 4;

        for (int row = 0; row < COLORS.length; row += cols) {
            Component line = Component.text("  ");
            for (int col = 0; col < cols && row + col < COLORS.length; col++) {
                Object[] c = COLORS[row + col];
                String id = (String) c[0];
                String name = (String) c[1];
                String code = (String) c[2];
                NamedTextColor nc = (NamedTextColor) c[3];
                boolean hasPerm = allColors || p.hasPermission("utility.chat.color." + id);
                boolean isCurrent = code.equals(currentColor);

                Component chip;
                if (hasPerm) {
                    Component nameText = isCurrent
                        ? Component.text(name, nc, TextDecoration.BOLD, TextDecoration.UNDERLINED)
                        : Component.text(name, nc);
                    chip = Component.text("[", NamedTextColor.GRAY)
                        .append(nameText)
                        .append(Component.text("] ", NamedTextColor.GRAY))
                        .clickEvent(ClickEvent.runCommand("/color " + id));
                } else {
                    chip = Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(Component.text(name, NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH))
                        .append(Component.text("] ", NamedTextColor.DARK_GRAY));
                }
                line = line.append(chip);
            }
            p.sendMessage(line);
        }
    }

    private void sendFormatList(Player p) {
        Set<String> activeFmts = colorManager.getActiveFormats(p.getUniqueId());
        boolean allFormats = p.hasPermission("utility.chat.format.*");

        Component line = Component.text("  ");
        for (Object[] f : FORMATS) {
            String id = (String) f[0];
            String name = (String) f[1];
            String code = (String) f[2];
            TextDecoration dec = (TextDecoration) f[3];
            boolean hasPerm = allFormats || p.hasPermission("utility.chat.format." + id);
            boolean isActive = activeFmts.contains(code);

            Component chip;
            if (hasPerm) {
                NamedTextColor nameColor = isActive ? NamedTextColor.GREEN : NamedTextColor.WHITE;
                chip = Component.text("[", NamedTextColor.GRAY)
                    .append(Component.text(name, nameColor).decorate(dec))
                    .append(Component.text("] ", NamedTextColor.GRAY))
                    .clickEvent(ClickEvent.runCommand("/color " + id));
            } else {
                chip = Component.text("[", NamedTextColor.DARK_GRAY)
                    .append(Component.text(name, NamedTextColor.DARK_GRAY, TextDecoration.STRIKETHROUGH))
                    .append(Component.text("] ", NamedTextColor.DARK_GRAY));
            }
            line = line.append(chip);
        }
        p.sendMessage(line);
    }

    private void sendCurrentStatus(Player p) {
        UUID uuid = p.getUniqueId();
        String colorCode = colorManager.getDefaultColor(uuid);
        Set<String> formatCodes = colorManager.getActiveFormats(uuid);

        if (colorCode.isEmpty() && formatCodes.isEmpty()) {
            p.sendMessage(Component.text(" Standard: ", NamedTextColor.GRAY)
                .append(Component.text("Keine", NamedTextColor.DARK_GRAY)));
            return;
        }

        Component preview = Component.text("Beispieltext");
        NamedTextColor previewColor = NamedTextColor.WHITE;
        for (Object[] c : COLORS) {
            if (colorCode.equals((String) c[2])) {
                previewColor = (NamedTextColor) c[3];
                break;
            }
        }
        preview = preview.color(previewColor);
        for (Object[] f : FORMATS) {
            if (formatCodes.contains((String) f[2])) {
                preview = preview.decorate((TextDecoration) f[3]);
            }
        }

        Component resetBtn = Component.text(" [× Reset]", NamedTextColor.RED)
            .clickEvent(ClickEvent.runCommand("/color reset"));

        p.sendMessage(Component.text(" Standard: ", NamedTextColor.GRAY)
            .append(preview)
            .append(resetBtn));
    }

    private void sendNavigation(Player p, int page) {
        Component nav = Component.empty();
        if (page > 1) {
            nav = nav.append(Component.text("[← Colors]", NamedTextColor.GRAY)
                .clickEvent(ClickEvent.runCommand("/color 1")));
        }
        if (page < 2) {
            nav = nav.append(Component.text("[Formatting →]", NamedTextColor.GRAY)
                .clickEvent(ClickEvent.runCommand("/color 2")));
        }
        p.sendMessage(nav);
    }
}
