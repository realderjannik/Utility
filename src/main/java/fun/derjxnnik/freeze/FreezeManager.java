package fun.derjxnnik.freeze;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeManager {

    private final Set<UUID> frozen = new HashSet<>();

    /** Toggles freeze for the UUID. Returns true if now frozen, false if released. */
    public boolean toggle(UUID uuid) {
        if (frozen.remove(uuid)) return false;
        frozen.add(uuid);
        return true;
    }

    public boolean isFrozen(UUID uuid) {
        return frozen.contains(uuid);
    }

    public void remove(UUID uuid) {
        frozen.remove(uuid);
    }

    public Set<UUID> getFrozen() {
        return Collections.unmodifiableSet(frozen);
    }
}
