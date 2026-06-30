package fun.derjxnnik.rank;

import fun.derjxnnik.currency.CurrencyManager;
import fun.derjxnnik.misc.Colors;
import fun.derjxnnik.misc.Icons;
import fun.derjxnnik.utility.Utility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.UUID;

public class RankManager {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final LuckPerms luckPerms;
    private final boolean luckPermsPresent;

    public RankManager() {
        RegisteredServiceProvider<LuckPerms> provider =
                Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.luckPerms = provider.getProvider();
            this.luckPermsPresent = true;
            registerLuckPermsListener();
        } else {
            this.luckPerms = null;
            this.luckPermsPresent = false;
        }
    }

    private void registerLuckPermsListener() {
        Utility plugin = Utility.getInstance();
        luckPerms.getEventBus().subscribe(plugin, UserDataRecalculateEvent.class, event -> {
            UUID uuid = event.getUser().getUniqueId();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                Bukkit.getScheduler().runTask(plugin, () -> refreshPlayer(player));
            }
        });
    }

    public boolean isLuckPermsPresent() {
        return luckPermsPresent;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public boolean isAvailable() {
        return luckPermsPresent && Utility.getInstance().getConfig().getBoolean("ranks.enabled", true);
    }

    public String getPrefix(Player player) {
        if (!luckPermsPresent) return "";
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        String prefix = user.getCachedData().getMetaData().getPrefix();
        if (prefix == null) return "";
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String buildTabName(Player player) {
        FileConfiguration config = Utility.getInstance().getConfig();
        if (!isAvailable() || !config.getBoolean("ranks.show-in-tab", true)) {
            return Colors.WHITE + player.getName();
        }
        String prefix = getPrefix(player);
        if (prefix.isEmpty()) return Colors.WHITE + player.getName();

        String format = config.getString("ranks.tab-format", "{prefix} | {name}");
        return format
                .replace("{prefix}", prefix)
                .replace(" | ", Colors.GRAY + " | " + Colors.WHITE)
                .replace("{name}", Colors.WHITE + player.getName());
    }

    public Component buildGradientPrefix(Player player) {
        if (!luckPermsPresent) return Component.empty();
        String raw = getPrefix(player);
        if (raw.isEmpty()) return Component.empty();

        String plain = Colors.strip(raw);
        if (plain == null || plain.isEmpty()) return Component.empty();

        String group = getPrimaryGroup(player);
        FileConfiguration config = Utility.getInstance().getConfig();

        List<String> colors = config.getStringList("ranks.gradients." + group + ".colors");
        if (colors.size() < 2)
            colors = config.getStringList("ranks.gradients.default.colors");

        if (colors.size() < 2) return LEGACY.deserialize(raw);

        String color1 = colors.get(0);
        String color2 = colors.get(1);

        String mm = "<font:minecraft:small_caps><gradient:" + color1 + ":" + color2 + ">" + plain.toLowerCase() + "</gradient></font>";
        return MiniMessage.miniMessage().deserialize(mm);
    }

    public Component buildTabComponent(Player player) {
        FileConfiguration config = Utility.getInstance().getConfig();
        int deaths = player.getStatistic(Statistic.DEATHS);
        Key sc  = Key.key("minecraft", "small_caps");
        Key def = Key.key("minecraft", "default");

        Component nameAndDeaths = Component.text(player.getName(), NamedTextColor.WHITE).font(sc)
                .append(Component.text(" " + deaths, NamedTextColor.YELLOW).font(sc));

        if (!isAvailable() || !config.getBoolean("ranks.show-in-tab", true))
            return nameAndDeaths;

        Component prefix = buildGradientPrefix(player);
        if (prefix.equals(Component.empty())) return nameAndDeaths;

        return prefix
                .append(Component.text(" | ", NamedTextColor.GRAY).font(def))
                .append(nameAndDeaths);
    }

    public String getPlayerGroup(Player player) {
        if (!luckPermsPresent) return "default";
        return getPrimaryGroup(player);
    }

    private String getPrimaryGroup(Player player) {
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        return user.getPrimaryGroup().toLowerCase();
    }

    /**
     * Team name encodes the group's LP weight as an inverted 3-digit sort key,
     * so Minecraft's alphabetical team sort mirrors LuckPerms weight order.
     * Higher LP weight → lower sort key → appears first in tab.
     * Format: r{3-digit-inverted-weight}{12 UUID chars} = 16 chars total.
     */
    private String getTeamName(Player player) {
        String uuid = player.getUniqueId().toString().replace("-", "");
        String sortKey = luckPermsPresent ? weightSortKey(player) : "999";
        return "r" + sortKey + uuid.substring(0, 12);
    }

    private String weightSortKey(Player player) {
        try {
            String groupName = getPrimaryGroup(player);
            net.luckperms.api.model.group.Group group = luckPerms.getGroupManager().getGroup(groupName);
            int weight = group != null ? group.getWeight().orElse(0) : 0;
            return String.format("%03d", Math.max(0, 1000 - weight));
        } catch (Exception e) {
            return "999";
        }
    }

    /**
     * Applies (or removes) the nametag prefix team for `target` on the given scoreboard.
     * Must be called for every online player's scoreboard so the prefix is visible to them.
     */
    public void applyNametagTeam(Player target, Scoreboard board) {
        FileConfiguration config = Utility.getInstance().getConfig();
        String teamName = getTeamName(target);

        // Clean up any stale rank team the player may have been in previously
        for (Team t : board.getTeams()) {
            if (t.getName().startsWith("r0") && !t.getName().equals(teamName)) {
                t.removeEntry(target.getName());
            }
        }

        if (!isAvailable() || !config.getBoolean("ranks.show-in-nametag", true)) {
            Team team = board.getTeam(teamName);
            if (team != null) team.removeEntry(target.getName());
            return;
        }

        Component prefix = buildGradientPrefix(target);
        if (prefix.equals(Component.empty())) {
            Team team = board.getTeam(teamName);
            if (team != null) team.removeEntry(target.getName());
            return;
        }

        Team team = board.getTeam(teamName);
        if (team == null) team = board.registerNewTeam(teamName);

        Key def = Key.key("minecraft", "default");
        Key sc  = Key.key("minecraft", "small_caps");
        team.prefix(prefix.append(Component.text(" | ", NamedTextColor.GRAY).font(def)));

        // Coins suffix: directly after the player name on the same line
        CurrencyManager cm = Utility.getInstance().getCurrencyManager();
        if (cm != null) {
            long coins = cm.getBalance(target.getUniqueId());
            Component suffix = Component.text(" " + Icons.COINS + " ", NamedTextColor.YELLOW)
                    .append(Component.text(String.format("%,d", coins), NamedTextColor.YELLOW).font(sc));
            team.suffix(suffix);
        }

        if (!team.hasEntry(target.getName())) {
            team.addEntry(target.getName());
        }
    }

    /** Applies nametag teams for ALL online players onto the given scoreboard. */
    public void applyAllNametagTeams(Scoreboard board) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            applyNametagTeam(p, board);
        }
    }

    /** Updates tab name + nametag team on every online player's scoreboard. */
    public void refreshPlayer(Player player) {
        player.playerListName(buildTabComponent(player));
        for (Player observer : Bukkit.getOnlinePlayers()) {
            applyNametagTeam(player, observer.getScoreboard());
        }
    }

    public void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshPlayer(player);
        }
    }
}
