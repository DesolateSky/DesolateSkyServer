package net.desolatesky.cooldown;

import net.desolatesky.database.MongoCodec;
import net.kyori.adventure.key.Key;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerCooldowns {

    public static final MongoCodec<PlayerCooldowns, PlayerCooldowns, CooldownConfig> MONGO_CODEC = new MongoCodec<>() {
        @Override
        public void write(PlayerCooldowns input, Document document) {
            final Map<Key, CooldownData> cooldowns = input.cooldowns;
            final List<Document> cooldownDocuments = cooldowns.values().stream()
                    .filter(CooldownData::isNotExpired)
                    .map(data -> {
                        final Document cooldownDocument = new Document();
                        CooldownData.MONGO_CODEC.write(data, cooldownDocument);
                        return cooldownDocument;
                    })
                    .toList();
            document.append("cooldowns", cooldownDocuments);
        }

        @Override
        public @UnknownNullability PlayerCooldowns read(Document document, CooldownConfig cooldownConfig) {
            final List<Document> cooldownDocuments = document.getList("cooldowns", Document.class);
            final Map<Key, CooldownData> cooldowns = cooldownDocuments.stream()
                    .map(cooldownDocument -> CooldownData.MONGO_CODEC.read(cooldownDocument, null))
                    .collect(Collectors.toMap(data -> data.cooldown().key(), Function.identity()));
            return new PlayerCooldowns(cooldownConfig, cooldowns);
        }
    };

    private final CooldownConfig cooldownConfig;
    private final Map<Key, CooldownData> cooldowns;

    public PlayerCooldowns(CooldownConfig cooldownConfig, Map<Key, CooldownData> cooldowns) {
        this.cooldownConfig = cooldownConfig;
        this.cooldowns = cooldowns;
    }

    public Duration getCooldownTime(Key key) {
        final CooldownData cooldown = this.cooldowns.get(key);
        if (cooldown == null) {
            return Duration.ZERO;
        }
        if (cooldown.isExpired()) {
            this.cooldowns.remove(key);
            return Duration.ZERO;
        }
        return cooldown.getTimeLeft();
    }

    public boolean isOnCooldown(Key key) {
        return this.getCooldownTime(key).compareTo(Duration.ZERO) > 0;
    }

    public boolean addCooldown(Key key) {
        final CooldownData cooldownData = this.cooldownConfig.createCooldown(key);
        if (cooldownData == null) {
            return false;
        }
        this.cooldowns.put(key, cooldownData);
        return true;
    }

    public @Unmodifiable Collection<Key> getCooldownKeys() {
        return Collections.unmodifiableSet(this.cooldowns.keySet());
    }

    public @Nullable CooldownData getCooldownData(Key key) {
        return this.cooldowns.get(key);
    }

    public void addCooldown(Key key, Cooldown cooldown) {
        this.cooldowns.put(key, new CooldownData(cooldown));
    }

    public void removeCooldown(Key key) {
        this.cooldowns.remove(key);
    }

}
