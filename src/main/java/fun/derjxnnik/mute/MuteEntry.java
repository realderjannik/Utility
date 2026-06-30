package fun.derjxnnik.mute;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MuteEntry {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final String playerName;
    private final String playerUuid;
    private final String reason;
    private final String muterName;
    private final long mutedAt;
    private final long expiresAt;

    public MuteEntry(String playerName, String playerUuid, String reason,
                     String muterName, long mutedAt, long expiresAt) {
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.reason     = reason;
        this.muterName  = muterName;
        this.mutedAt    = mutedAt;
        this.expiresAt  = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != -1 && System.currentTimeMillis() > expiresAt;
    }

    public boolean isPermanent() { return expiresAt == -1; }

    public String getExpiryString() {
        if (expiresAt == -1) return "Permanent";
        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiresAt), ZoneId.systemDefault());
        return FMT.format(dt);
    }

    public String getPlayerName() { return playerName; }
    public String getPlayerUuid() { return playerUuid; }
    public String getReason()     { return reason; }
    public String getMuterName()  { return muterName; }
    public long   getMutedAt()    { return mutedAt; }
    public long   getExpiresAt()  { return expiresAt; }
}
