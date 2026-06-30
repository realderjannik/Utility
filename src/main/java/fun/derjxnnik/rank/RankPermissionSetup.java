package fun.derjxnnik.rank;

import fun.derjxnnik.misc.LogUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;

public class RankPermissionSetup {

    private final LuckPerms lp;

    public RankPermissionSetup(LuckPerms lp) {
        this.lp = lp;
    }

    /**
     * Applies the permission hierarchy to all rank groups.
     * Safe to run multiple times – adding an existing node is a no-op in LP.
     *
     * Hierarchy (lowest → highest):
     *   default → a-subscriber / d-subscriber
     *           → a-vip / d-vip
     *           → streamer
     *           → a-moderator / d-moderator
     *           → admin
     */
    public void apply() {
        // ── default ───────────────────────────────────────────────────────────
        addPerms("default",
                "utility.spawn",
                "utility.home",
                "utility.ping",
                "utility.uuid",
                "utility.day",
                "utility.playtime",
                "utility.lastdeath",
                "utility.backpack",
                "utility.enderchest",
                "utility.craftingtable",
                "utility.chat.use");

        // ── subscriber (paired: a + d, both inherit default) ──────────────────
        inherit("a-subscriber", "default");
        inherit("d-subscriber", "default");
        // no extra perms over default — distinguish from default by rank only

        // ── vip (paired: a + d, both inherit a-subscriber) ───────────────────
        inherit("a-vip", "a-subscriber");
        inherit("d-vip", "a-subscriber");
        addPerms("a-vip",
                "utility.rename",
                "utility.hat",
                "utility.chat.color.*",
                "utility.chat.format.*");
        addPerms("d-vip",
                "utility.rename",
                "utility.hat",
                "utility.chat.color.*",
                "utility.chat.format.*");

        // ── streamer (inherits a-vip) ─────────────────────────────────────────
        inherit("streamer", "a-vip");
        addPerms("streamer",
                "utility.coins.others",
                "utility.info.bypass");

        // ── helper (staff entry-level, inherits default) ──────────────────────
        inherit("helper", "default");
        addPerms("helper",
                "utility.mute",
                "utility.ban.limited",   // max 7 days, no permanent
                "utility.invsee",
                "utility.info.bypass",
                "utility.help.staff",
                "utility.coins.others");

        // ── moderator (merged role, inherits helper) ──────────────────────────
        inherit("moderator", "helper");
        addPerms("moderator",
                "utility.ban",
                "utility.setspawn");

        // ── moderator (paired legacy roles, inherit moderator) ────────────────
        inherit("a-moderator", "moderator");
        inherit("d-moderator", "moderator");

        // ── admin (inherits moderator) ────────────────────────────────────────
        inherit("admin", "moderator");
        // Admin is op in most servers; explicit perm list kept minimal

        LogUtil.info("[RankPerms] Permissions erfolgreich auf alle Gruppen angewendet.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void inherit(String groupName, String parent) {
        Group group = getOrCreate(groupName);
        if (group == null) return;
        group.data().add(InheritanceNode.builder(parent).build());
        save(group);
    }

    private void addPerms(String groupName, String... permissions) {
        Group group = getOrCreate(groupName);
        if (group == null) return;
        for (String perm : permissions) {
            group.data().add(Node.builder(perm).value(true).build());
        }
        save(group);
    }

    private void save(Group group) {
        lp.getGroupManager().saveGroup(group).join();
    }

    private Group getOrCreate(String name) {
        Group group = lp.getGroupManager().getGroup(name);
        if (group == null) {
            try {
                group = lp.getGroupManager().createAndLoadGroup(name).join();
                LogUtil.info("[RankPerms] LP-Gruppe erstellt: " + name);
            } catch (Exception e) {
                LogUtil.info("[RankPerms] Fehler beim Erstellen der Gruppe '" + name + "': " + e.getMessage());
                return null;
            }
        }
        return group;
    }
}
