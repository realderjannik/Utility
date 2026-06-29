package fun.derjxnnik.ban;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BanEntry {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final String playerName;
    private final String playerUuid;
    private final String reason;
    private final String bannerName;
    private final long bannedAt;
    private final long expiresAt;

    public BanEntry(String playerName, String playerUuid, String reason,
                    String bannerName, long bannedAt, long expiresAt) {
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.reason = reason;
        this.bannerName = bannerName;
        this.bannedAt = bannedAt;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != -1 && System.currentTimeMillis() > expiresAt;
    }

    public boolean isPermanent() {
        return expiresAt == -1;
    }

    public String getExpiryString() {
        if (expiresAt == -1) return "Permanent";
        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiresAt), ZoneId.systemDefault());
        return FMT.format(dt);
    }

    public String getPlayerName() { return playerName; }
    public String getPlayerUuid() { return playerUuid; }
    public String getReason()     { return reason; }
    public String getBannerName() { return bannerName; }
    public long   getBannedAt()   { return bannedAt; }
    public long   getExpiresAt()  { return expiresAt; }
}
