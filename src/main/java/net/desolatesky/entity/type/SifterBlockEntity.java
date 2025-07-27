package net.desolatesky.entity.type;

import net.desolatesky.block.handler.custom.SifterBlockHandler;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.entity.EntityKey;
import net.desolatesky.entity.EntityKeys;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.WorldEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SifterBlockEntity extends Entity implements DSEntity {

    public static final int MAX_STAGE = 8;

    private static final Vec TRANSLATION = new Vec(0, 0, 0);

    private final Block block;
    private final Block particleBlock;
    private final Point sifterPosition;
    private final SifterBlockHandler blockHandler;
    private int stage = 0;
    private Interaction interactionEntity;

    public SifterBlockEntity(Block block, Block particleBlock, Point sifterPosition, SifterBlockHandler blockHandler) {
        super(EntityType.BLOCK_DISPLAY);
        this.block = block;
        this.particleBlock = particleBlock;
        this.sifterPosition = sifterPosition;
        this.blockHandler = blockHandler;
        this.setup();
    }

    public SifterBlockEntity(Block block, Block particleBlock, Point sifterPosition, SifterBlockHandler blockHandler, UUID uuid) {
        super(EntityType.BLOCK_DISPLAY, uuid);
        this.block = block;
        this.particleBlock = particleBlock;
        this.sifterPosition = sifterPosition;
        this.blockHandler = blockHandler;
        this.setup();
    }

    private void setup() {
        final BlockDisplayMeta meta = (BlockDisplayMeta) this.getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setBlockState(this.block);
        meta.setHasNoGravity(true);
        meta.setTranslation(TRANSLATION);
        meta.setNotifyAboutChanges(true);

        this.interactionEntity = new Interaction();
    }

    public boolean isComplete() {
        return this.stage >= MAX_STAGE;
    }

    public void addStage() {
        if (this.stage < MAX_STAGE) {
            this.stage++;
            this.adjustPosition();
            final WorldEventPacket packet = new WorldEventPacket(2001, this.getPosition(), this.particleBlock.stateId(), false);
            this.getInstance().sendGroupedPacket(packet);
        }
        if (this.isComplete()) {
            this.remove();
        }
    }

    private void adjustPosition() {
        final BlockDisplayMeta meta = (BlockDisplayMeta) this.getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setScale(new Vec(1, this.getVerticalScale(), 1));
        meta.setNotifyAboutChanges(true);
        this.interactionEntity.updateVerticalScale();
    }

    private float getVerticalScale() {
        return (float) (1 - this.getPercentCompletion()) * 0.5f;
    }

    public double getPercentCompletion() {
        return (double) this.stage / MAX_STAGE;
    }

    @Override
    public DSInstance getDSInstance() {
        return (DSInstance) this.getInstance();
    }

    @Override
    public void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand) {

    }

    @Override
    public void onPunch(DSEntity attacker) {
    }

    public Block displayedBlock() {
        return this.block;
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        this.interactionEntity.setInstance(instance, spawnPosition.add(0.5, 0, 0.5).add(TRANSLATION));
        return super.setInstance(instance, spawnPosition);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Point spawnPosition) {
        this.interactionEntity.setInstance(instance, spawnPosition.add(0.5, 0, 0.5).add(TRANSLATION));
        return super.setInstance(instance, spawnPosition);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance) {
        this.interactionEntity.setInstance(instance);
        return super.setInstance(instance);
    }

    @Override
    public InstancePoint<Pos> getInstancePosition() {
        return new InstancePoint<>(this.getInstance(), this.getPosition());
    }

    @Override
    public @NotNull EntityKey key() {
        return EntityKeys.SIFTER_BLOCK_ENTITY;
    }

    private class Interaction extends Entity implements DSEntity {

        public Interaction() {
            super(EntityType.INTERACTION);
            final InteractionMeta interactionMeta = (InteractionMeta) this.getEntityMeta();
            interactionMeta.setNotifyAboutChanges(false);
            interactionMeta.setHeight(1.0f);
            interactionMeta.setWidth(1.0f);
            interactionMeta.setNotifyAboutChanges(true);
        }

        @Override
        public void update(long time) {
            if (SifterBlockEntity.this.isRemoved()) {
                this.remove();
            }
        }

        private void updateVerticalScale() {
            final InteractionMeta interactionMeta = (InteractionMeta) this.getEntityMeta();
            interactionMeta.setNotifyAboutChanges(false);
            interactionMeta.setHeight(SifterBlockEntity.this.getVerticalScale() + 0.2f);
            interactionMeta.setNotifyAboutChanges(true);
        }

        @Override
        public DSInstance getDSInstance() {
            return (DSInstance) SifterBlockEntity.this.getInstance();
        }

        @Override
        public void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand) {
            if (!(clicker instanceof final DSPlayer player)) {
                return;
            }
            SifterBlockEntity.this.blockHandler.click(player, player.getDSInstance(), SifterBlockEntity.this.sifterPosition, true);
        }

        @Override
        public void onPunch(DSEntity attacker) {
            if (!(attacker instanceof final DSPlayer player)) {
                return;
            }
            SifterBlockEntity.this.blockHandler.click(player, player.getDSInstance(), SifterBlockEntity.this.sifterPosition, true);
        }

        @Override
        public InstancePoint<Pos> getInstancePosition() {
            return new InstancePoint<>(this.getInstance(), this.getPosition());
        }

        @Override
        public @NotNull EntityKey key() {
            return SifterBlockEntity.this.key();
        }

    }

}
