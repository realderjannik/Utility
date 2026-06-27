package fun.derjxnnik.misc;

public enum TimeLabel {
   DAY("Day", "\ud83c\udf1e", 0, 5999),
   NOON("Noon", "\ud83c\udf1e", 6000, 11999),
   SUNSET("Sunset", "\ud83c\udf07", 12000, 12999),
   NIGHT("Night", "\ud83c\udf19", 13000, 17999),
   MIDNIGHT("Midnight", "\ud83c\udf0c", 18000, 22999),
   SUNRISE("Sunrise", "\ud83c\udf05", 23000, 23999);

   private final String label;
   private final String emoji;
   private final int startTime;
   private final int endTime;

   private TimeLabel(String label, String emoji, int startTime, int endTime) {
      this.label = label;
      this.emoji = emoji;
      this.startTime = startTime;
      this.endTime = endTime;
   }

   public static TimeLabel fromTime(long time) {
      for(TimeLabel t : values()) {
         if (time >= (long)t.startTime && time <= (long)t.endTime) {
            return t;
         }
      }

      return null;
   }

   public String getLabel() {
      return this.label;
   }

   public String getEmoji() {
      return this.emoji;
   }

   // $FF: synthetic method
   private static TimeLabel[] $values() {
      return new TimeLabel[]{DAY, NOON, SUNSET, NIGHT, MIDNIGHT, SUNRISE};
   }
}
