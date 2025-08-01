package net.desolatesky.block.entity.custom.crop;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.category.BlockCategories;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.handler.entity.BlockEntityHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.ItemTags;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.RandomUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CropBlockEntity<T extends CropBlockEntity<T>> extends BlockEntity<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CropBlockEntity.class);

    public static DSBlockHandler createHandler(BlockSettings settings) {
        return new Handler<>(settings, CropBlockEntity.class);
    }

    protected Crop crop;
    protected double growthChance;
    protected int age;

    public CropBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
    }

    @Override
    public @Nullable Block save(DSInstance instance, Point point, Block block) {
        return block.withTag(BlockTags.CROP_AGE, this.age)
                .withTag(BlockTags.CROP, this.crop);
    }

    @Override
    public void load(Placement placement, DSInstance instance) {
        final Block block = placement.getBlock();
        this.crop = block.getTag(BlockTags.CROP);
        System.out.println("Loaded crop block: " + block);
        if (this.crop == null) {
            return;
        }
        Double growthChance = block.getTag(BlockTags.CROP_GROWTH_CHANCE);
        if (growthChance == null) {
            growthChance = this.crop.rarity().minGrowthChance();
        }
        this.growthChance = growthChance;
        final Integer readAge = block.getTag(BlockTags.CROP_AGE);
        if (readAge == null) {
            return;
        }
        this.age = readAge;
    }

    protected void setAge(int age) {
        if (this.crop == null) {
            LOGGER.info("Attempted to set the age of a crop entity with no crop set");
            return;
        }
        this.age = Math.min(age, this.crop.maxAge());
    }

    private static class Handler<T extends CropBlockEntity<T>> extends BlockEntityHandler<T> {

        public Handler(BlockSettings blockSettings, Class<T> entityClass) {
            super(blockSettings, entityClass);
        }

        @Override
        public BlockHandlerResult.Place onPlayerPlace(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition, T entity) {
            final Block under = instance.getBlock(blockPosition.sub(0, 1, 0));
            final DSBlockHandler blockHandler = entity.blockRegistry.getHandlerForBlock(under);
            if (blockHandler == null) {
                return BlockHandlerResult.cancelPlace(block, true);
            }
            if (!blockHandler.isCategory(BlockCategories.CROP_GROWABLE)) {
                return BlockHandlerResult.cancelPlace(block, true);
            }
            final ItemStack usedItem = player.getItemInMainHand();
            System.out.println("Crop item placed: " + usedItem);
            final Crop crop = usedItem.getTag(ItemTags.CROP);
            System.out.println("Crop placed: " + crop);
            if (crop == null) {
                return BlockHandlerResult.cancelPlace(block, true);
            }
            entity.crop = crop;
            entity.setAge(0);
            final CropRarity cropRarity = crop.rarity();
            entity.growthChance = RandomUtil.randomDouble(instance.randomSource(), cropRarity.minGrowthChance(), cropRarity.maxGrowthChance());
            return BlockHandlerResult.consumePlace(block, false);
        }

        @Override
        public void onRandomTick(DSInstance instance, Block block, Point blockPosition, T entity) {
            if (entity.crop == null) {
//                System.out.println("Crop entity has no crop set, cannot grow");
                return;
            }
            if (entity.age >= entity.crop.maxAge()) {
//                System.out.println("Crop entity has reached max age, cannot grow further");
                return;
            }
            if (!RandomUtil.checkChance(instance.randomSource(), entity.growthChance)) {
//                System.out.println("Crop entity did not grow this tick, chance failed");
                return;
            }
            System.out.println("Crop entity is growing, current age: " + entity.age);
            final int newAge = entity.age + 1;
            entity.setAge(newAge);
            final Block newBlock = BlockProperties.AGE.set(block, newAge).withTag(BlockTags.CROP_AGE, newAge);
            instance.setBlock(blockPosition, newBlock);
        }

    }


}
