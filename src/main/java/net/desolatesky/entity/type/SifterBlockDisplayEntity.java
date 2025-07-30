package net.desolatesky.entity.type;

import net.desolatesky.entity.DSEntity;
import net.desolatesky.entity.EntityKey;
import net.desolatesky.entity.EntityKeys;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.player.DSPlayer;
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

public final class SifterBlockDisplayEntity extends Entity implements DSEntity {

    public static final int MAX_STAGE = 8;

    private static final Vec TRANSLATION = new Vec(0, 0, 0);

    private final Block block;
    private final Block displayBlock;
    private final Point sifterPosition;
    private final net.desolatesky.block.entity.custom.SifterBlockEntity blockHandler;
    //    private int stage = 0;
    private Interaction interactionEntity;

    public SifterBlockDisplayEntity(Block block, Block displayBlock, Point sifterPosition, net.desolatesky.block.entity.custom.SifterBlockEntity blockHandler) {
        super(EntityType.BLOCK_DISPLAY);
        this.block = block;
        this.displayBlock = displayBlock;
        this.sifterPosition = sifterPosition;
        this.blockHandler = blockHandler;
        this.setup();
    }

    public SifterBlockDisplayEntity(Block block, Block displayBlock, Point sifterPosition, net.desolatesky.block.entity.custom.SifterBlockEntity blockHandler, UUID uuid) {
        super(EntityType.BLOCK_DISPLAY, uuid);
        this.block = block;
        this.displayBlock = displayBlock;
        this.sifterPosition = sifterPosition;
        this.blockHandler = blockHandler;
        this.setup();
    }

    private void setup() {
        final BlockDisplayMeta meta = (BlockDisplayMeta) this.getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setBlockState(this.displayBlock);
        meta.setHasNoGravity(true);
        meta.setTranslation(TRANSLATION);
        meta.setNotifyAboutChanges(true);

        this.interactionEntity = new Interaction();
    }

    public void setStage(int stage) {
        this.adjustPosition(stage);
        final WorldEventPacket packet = new WorldEventPacket(2001, this.getPosition(), this.displayBlock.stateId(), false);
        this.getInstance().sendGroupedPacket(packet);
    }

    private void adjustPosition(int stage) {
        final BlockDisplayMeta meta = (BlockDisplayMeta) this.getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setScale(new Vec(1, this.getVerticalScale(stage), 1));
        meta.setNotifyAboutChanges(true);
        this.interactionEntity.updateVerticalScale(stage);
    }

    private float getVerticalScale(int stage) {
        return (float) (1 - this.getPercentCompletion(stage)) * 0.5f;
    }

    public double getPercentCompletion(int stage) {
        return (double) stage / MAX_STAGE;
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
        return this.displayBlock;
    }

    public Block block() {
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
            if (SifterBlockDisplayEntity.this.isRemoved()) {
                this.remove();
            }
        }

        private void updateVerticalScale(int stage) {
            final InteractionMeta interactionMeta = (InteractionMeta) this.getEntityMeta();
            interactionMeta.setNotifyAboutChanges(false);
            interactionMeta.setHeight(SifterBlockDisplayEntity.this.getVerticalScale(stage) + 0.2f);
            interactionMeta.setNotifyAboutChanges(true);
        }

        @Override
        public DSInstance getDSInstance() {
            return (DSInstance) SifterBlockDisplayEntity.this.getInstance();
        }

        @Override
        public void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand) {
            if (!(clicker instanceof final DSPlayer player)) {
                return;
            }
            SifterBlockDisplayEntity.this.blockHandler.click(player, player.getDSInstance(), SifterBlockDisplayEntity.this.sifterPosition, true);
        }

        @Override
        public void onPunch(DSEntity attacker) {
            if (!(attacker instanceof final DSPlayer player)) {
                return;
            }
            SifterBlockDisplayEntity.this.blockHandler.click(player, player.getDSInstance(), SifterBlockDisplayEntity.this.sifterPosition, true);
        }

        @Override
        public InstancePoint<Pos> getInstancePosition() {
            return new InstancePoint<>(this.getInstance(), this.getPosition());
        }

        @Override
        public @NotNull EntityKey key() {
            return SifterBlockDisplayEntity.this.key();
        }

    }

}
