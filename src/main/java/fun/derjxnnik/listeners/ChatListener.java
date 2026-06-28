package fun.derjxnnik.listeners;

import fun.derjxnnik.chat.ColorManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.rank.RankManager;
import fun.derjxnnik.utility.Utility;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static final Key SC = Key.key("minecraft", "small_caps");
    private static final Key DEFAULT_FONT = Key.key("minecraft", "default");

    private final RankManager rankManager;
    private final ColorManager colorManager;
    private final Utility plugin;

    public ChatListener(RankManager rankManager, ColorManager colorManager, Utility plugin) {
        this.rankManager = rankManager;
        this.colorManager = colorManager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncChatEvent e) {
        Player player = e.getPlayer();

        e.renderer((source, sourceDisplayName, message, viewer) -> {
            Component processedMessage = processMessage(
                    PlainTextComponentSerializer.plainText().serialize(message), player);

            Component sender;
            boolean showInChat = rankManager.isAvailable()
                    && plugin.getConfig().getBoolean("ranks.show-in-chat", true);

            if (showInChat) {
                Component prefix = rankManager.buildGradientPrefix(player);
                if (!prefix.equals(Component.empty())) {
                    sender = prefix
                            .append(Component.text(" | ", NamedTextColor.GRAY).font(SC))
                            .append(Component.text(player.getName(), NamedTextColor.WHITE).font(SC))
                            .append(Component.text(": ", NamedTextColor.GRAY).font(DEFAULT_FONT));
                } else {
                    sender = Component.text(player.getName(), NamedTextColor.WHITE).font(SC)
                            .append(Component.text(": ", NamedTextColor.GRAY).font(DEFAULT_FONT));
                }
            } else {
                sender = Component.text(player.getName(), NamedTextColor.WHITE).font(DEFAULT_FONT)
                        .append(Component.text(": ", NamedTextColor.GRAY).font(DEFAULT_FONT));
            }

            Component msg = Component.empty().font(DEFAULT_FONT).append(processedMessage);
            return sender.append(msg);
        });
    }

    private Component processMessage(String raw, Player player) {
        boolean allColors = player.hasPermission("utility.chat.color.*");
        boolean allFormats = player.hasPermission("utility.chat.format.*");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (raw.charAt(i) == '&' && i + 1 < raw.length()) {
                char code = Character.toLowerCase(raw.charAt(i + 1));
                String[] permInfo = getPermInfo(code);
                if (permInfo != null) {
                    boolean isFormat = permInfo[0].equals("format");
                    boolean allowed = (isFormat ? allFormats : allColors)
                            || player.hasPermission("utility.chat." + permInfo[0] + "." + permInfo[1]);
                    if (allowed) result.append('§').append(code);
                    i++;
                } else if (code == 'r') {
                    result.append('§').append(code);
                    i++;
                } else {
                    result.append(raw.charAt(i));
                }
            } else {
                result.append(raw.charAt(i));
            }
        }

        String defaultPrefix = colorManager.buildPrefix(player.getUniqueId());
        Component msg = LEGACY.deserialize(result.toString());
        if (!defaultPrefix.isEmpty()) {
            return LEGACY.deserialize(defaultPrefix).append(msg);
        }
        return msg;
    }

    private String[] getPermInfo(char code) {
        return switch (code) {
            case '0' -> new String[]{"color", "black"};
            case '1' -> new String[]{"color", "dark_blue"};
            case '2' -> new String[]{"color", "dark_green"};
            case '3' -> new String[]{"color", "dark_aqua"};
            case '4' -> new String[]{"color", "dark_red"};
            case '5' -> new String[]{"color", "dark_purple"};
            case '6' -> new String[]{"color", "gold"};
            case '7' -> new String[]{"color", "gray"};
            case '8' -> new String[]{"color", "dark_gray"};
            case '9' -> new String[]{"color", "blue"};
            case 'a' -> new String[]{"color", "green"};
            case 'b' -> new String[]{"color", "aqua"};
            case 'c' -> new String[]{"color", "red"};
            case 'd' -> new String[]{"color", "light_purple"};
            case 'e' -> new String[]{"color", "yellow"};
            case 'f' -> new String[]{"color", "white"};
            case 'l' -> new String[]{"format", "bold"};
            case 'o' -> new String[]{"format", "italic"};
            case 'n' -> new String[]{"format", "underline"};
            case 'k' -> new String[]{"format", "obfuscated"};
            case 'm' -> new String[]{"format", "strikethrough"};
            default -> null;
        };
    }
}
