package net.desolatesky.entity.type;

import net.desolatesky.block.entity.custom.SifterBlockEntity;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.entity.EntityKeys;
import net.desolatesky.entity.SimpleEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.PacketUtil;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.WorldEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class SifterBlockDisplayEntity extends SimpleEntity {

    public static final int MAX_STAGE = 8;

    private static final Vec TRANSLATION = new Vec(0, 0, 0);

    private final Block block;
    private final Block displayBlock;
    private final DSBlockHandler displayBlockHandler;
    private final Point sifterPosition;
    private final SifterBlockEntity blockEntity;
    private Interaction interactionEntity;

    public SifterBlockDisplayEntity(Block block, Block displayBlock, DSBlockHandler displayBlockHandler, Point sifterPosition, SifterBlockEntity blockEntity) {
        super(EntityType.BLOCK_DISPLAY, EntityKeys.SIFTER_BLOCK_ENTITY);
        this.block = block;
        this.displayBlock = displayBlock;
        this.displayBlockHandler = displayBlockHandler;
        this.sifterPosition = sifterPosition;
        this.blockEntity = blockEntity;
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
        PacketUtil.sendBlockBreak(this.instance, this.displayBlockHandler, this.getPosition(), this.displayBlock);
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
        return super.setInstance(instance, spawnPosition).thenRun(() -> this.interactionEntity.setInstance(instance, spawnPosition.add(0.5, 0, 0.5).add(TRANSLATION)).join());
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Point spawnPosition) {
        return this.setInstance(instance, spawnPosition.asPos());
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

    private class Interaction extends SimpleEntity {

        public Interaction() {
            super(EntityType.INTERACTION, EntityKeys.SIFTER_BLOCK_ENTITY);
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
        public DSInstance getInstance() {
            return (DSInstance) SifterBlockDisplayEntity.this.getInstance();
        }

        @Override
        public void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand) {
            if (!(clicker instanceof final DSPlayer player)) {
                return;
            }
            SifterBlockDisplayEntity.this.blockEntity.click(player, player.getInstance(), SifterBlockDisplayEntity.this.sifterPosition, true);
        }

        @Override
        public void onPunch(DSEntity attacker) {
            if (!(attacker instanceof final DSPlayer player)) {
                return;
            }
            SifterBlockDisplayEntity.this.blockEntity.click(player, player.getInstance(), SifterBlockDisplayEntity.this.sifterPosition, true);
        }

        @Override
        public InstancePoint<Pos> getInstancePosition() {
            return new InstancePoint<>(this.getInstance(), this.getPosition());
        }

    }

}
