package net.desolatesky.cooldown;

import net.desolatesky.database.MongoCodec;
import net.kyori.adventure.key.Key;
import org.bson.Document;
import org.jetbrains.annotations.UnknownNullability;

import java.time.Duration;
import java.time.Instant;

public record CooldownData(Cooldown cooldown, Instant start) {

    public static final MongoCodec<CooldownData, CooldownData, Void> MONGO_CODEC = new MongoCodec<>() {
        @Override
        public void write(CooldownData input, Document document) {
            document.append("key", input.cooldown.key().asString());
            document.append("duration", input.cooldown.duration().toMillis());
            document.append("start", input.start);
        }

        @Override
        public @UnknownNullability CooldownData read(Document document, Void context) {
            final String keyString = document.getString("key");
            final Key key = Key.key(keyString);
            final Duration duration = Duration.ofMillis(document.getLong("duration"));
            final Instant start = Instant.ofEpochMilli(document.getLong("start"));
            return new CooldownData(Cooldown.create(key, duration), start);
        }
    };

    public static CooldownData complete(Key key)  {
        return new CooldownData(Cooldown.empty(key), Instant.now());
    }

    public CooldownData(Cooldown cooldown) {
        this(cooldown, Instant.now());
    }

    public CooldownData(Cooldown cooldown, Instant start) {
        this.cooldown = cooldown;
        this.start = start;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.start.plus(this.cooldown.duration()));
    }

    public Instant getEnd() {
        return this.start.plus(this.cooldown.duration());
    }

    public Duration getTimeLeft() {
        return Duration.between(Instant.now(), this.getEnd());
    }

}
