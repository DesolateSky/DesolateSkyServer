package net.desolatesky.entity.type;

import net.desolatesky.entity.DSEntity;
import net.desolatesky.entity.EntityTags;
import net.desolatesky.island.Island;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import org.joml.Quaternionf;

import java.awt.Color;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class IslandCoreSpawnerEntity extends DSEntity<IslandCoreSpawnerEntity> {

    private static final Vec MIN_SCALE = new Vec(0.1);
    private static final Vec MAX_SCALE = new Vec(0.3);
    private static final Vec MIN_TRANSLATION = MIN_SCALE.apply((x, y, z) -> new Vec((1 - x) / 2, (1 - y) / 2, (1 - z) / 2));
    private static final Vec MAX_TRANSLATION = MAX_SCALE.apply((x, y, z) -> new Vec((1 - x) / 2, (1 - y) / 2, (1 - z) / 2));
    private static final Quaternionf ROTATION = new Quaternionf().rotateY((float) Math.toRadians(45))
            .rotateX((float) Math.toRadians(45))
            .rotateZ((float) Math.toRadians(45));
    private static final int GLOW_COLOR = new Color(0, 0, 255).getRGB();
    private static final int TRANSLATION_TICKS = 30;
    private static final double ROTATION_INCREMENT = 90;
    private double rotationDeg = ROTATION_INCREMENT;

    private final Quaternionf rotation;
    private int currentTranslationTicks = TRANSLATION_TICKS;
    private boolean growing = false;

    public IslandCoreSpawnerEntity(UUID uuid, Island island, Consumer<Entity> tagApplier) {
        this.rotation = ROTATION;
        super(EntityType.ITEM_DISPLAY, uuid, island, tagApplier);
    }

    public IslandCoreSpawnerEntity(Island island, Consumer<Entity> tagApplier) {
        this.rotation = ROTATION;
        super(EntityType.ITEM_DISPLAY, island, tagApplier);
    }

    @Override
    protected void initialize() {
        final Key itemKey = Objects.requireNonNull(this.getTag(EntityTags.ITEM_DISPLAY_KEY));
        final DSWorld world = this.world();
        this.setNoGravity(true);
        final ItemDefinition itemDefinition = world.itemFactory().getItemDefinition(itemKey);
        if (itemDefinition == null) {
            return;
        }
        final ItemStack itemStack = itemDefinition.defaultItemStack();
        if (itemStack == null) {
            return;
        }
        this.setupDisplay(itemStack);
    }

    private void setupDisplay(ItemStack itemStack) {
        this.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setItemStack(itemStack);
            meta.setScale(MAX_SCALE);
            meta.setGlowColorOverride(GLOW_COLOR);
            meta.setLeftRotation(this.getRotation());
            meta.setHasGlowingEffect(true);
        });
    }

    private float[] getRotation() {
        return new float[]{this.rotation.x, this.rotation.y, this.rotation.z, this.rotation.w};
    }

    @Override
    protected void onTick(long time) {
        this.currentTranslationTicks++;
        if (this.currentTranslationTicks >= TRANSLATION_TICKS) {
            this.translate();
            this.currentTranslationTicks = 0;
        }
    }

    private void translate() {
        this.rotationDeg += ROTATION_INCREMENT;
        if (this.rotationDeg > 360) {
            this.rotationDeg = ROTATION_INCREMENT;
        }
        this.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setTransformationInterpolationDuration(TRANSLATION_TICKS);
            this.rotation.identity()
                    .rotateY((float) Math.toRadians(this.rotationDeg))
                    .rotateX((float) Math.toRadians(45))
                    .rotateZ((float) Math.toRadians(45));
            meta.setPosRotInterpolationDuration(TRANSLATION_TICKS);
            meta.setLeftRotation(this.getRotation());
            this.growing = !this.growing;
            if (this.growing) {
                meta.setScale(MAX_SCALE);
            } else {
                meta.setScale(MIN_SCALE);
            }
            meta.setTransformationInterpolationStartDelta(0);
        });
    }

    @Override
    protected void dropItems() {

    }
}
