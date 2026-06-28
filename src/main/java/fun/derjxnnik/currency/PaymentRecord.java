package fun.derjxnnik.currency;

import java.util.UUID;

public class PaymentRecord {
    public final String timestamp;
    public final UUID fromUUID;
    public final String fromName;
    public final UUID toUUID;
    public final String toName;
    public final long amount;

    public PaymentRecord(String timestamp, UUID fromUUID, String fromName,
                         UUID toUUID, String toName, long amount) {
        this.timestamp = timestamp;
        this.fromUUID  = fromUUID;
        this.fromName  = fromName;
        this.toUUID    = toUUID;
        this.toName    = toName;
        this.amount    = amount;
    }
}
