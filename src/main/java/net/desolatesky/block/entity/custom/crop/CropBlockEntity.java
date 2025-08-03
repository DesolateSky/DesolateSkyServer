package net.desolatesky.block.entity.custom.crop;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.category.BlockCategories;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.handler.entity.BlockEntityHandler;
import net.desolatesky.block.property.type.BlockProperty;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.ItemTags;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.RandomUtil;
import net.kyori.adventure.key.Key;
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
    protected final BlockProperty<Integer> ageProperty;

    public CropBlockEntity(Key key, DesolateSkyServer server, BlockProperty<Integer> ageProperty) {
        super(key, server);
        this.ageProperty = ageProperty;
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
        if (this.crop == null) {
            System.out.println("No crop");
            return;
        }
        System.out.println("Loading crop: " + this.crop);
        Double growthChance = block.getTag(BlockTags.CROP_GROWTH_CHANCE);
        if (growthChance == null) {
            growthChance = this.crop.rarity().minGrowthChance();
        }
        this.growthChance = growthChance;
        final Integer readAge = block.getTag(BlockTags.CROP_AGE);
        System.out.println("Age: " + readAge);
        if (readAge == null) {
            return;
        }
        this.age = readAge;
    }

    protected void onMaxGrowth(DSInstance instance, Block block, Point blockPosition, boolean alreadyMax) {
        if (alreadyMax) {
            return;
        }
        // override
        final Block newBlock = this.ageProperty.set(block, this.age).withTag(BlockTags.CROP_AGE, this.age);
        instance.setBlock(blockPosition, newBlock);
    }

    public boolean isFullyGrown() {
        return this.crop != null && this.age >= this.crop.maxAge();
    }

    protected void setAge(int age) {
        if (this.crop == null) {
            LOGGER.info("Attempted to set the age of a crop entity with no crop set");
            return;
        }
        this.age = Math.min(age, this.crop.maxAge());
    }

    protected static class Handler<T extends CropBlockEntity<T>> extends BlockEntityHandler<T> {

        protected Handler(BlockSettings blockSettings, Class<T> entityClass) {
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
            final Crop crop = usedItem.getTag(ItemTags.CROP);
            if (crop == null) {
                return BlockHandlerResult.cancelPlace(block, true);
            }
            entity.crop = crop;
            entity.setAge(0);
            final CropRarity cropRarity = crop.rarity();
            entity.growthChance = RandomUtil.randomDouble(instance.randomSource(), cropRarity.minGrowthChance(), cropRarity.maxGrowthChance());
            return BlockHandlerResult.consumePlace(block.withTag(BlockTags.CROP, crop).withTag(BlockTags.CROP_AGE, entity.age), false);
        }

        @Override
        public void onRandomTick(DSInstance instance, Block block, Point blockPosition, T entity) {
            if (entity.crop == null) {
                return;
            }
            if (entity.age >= entity.crop.maxAge()) {
                entity.onMaxGrowth(instance, block, blockPosition, true);
                return;
            }
            if (!RandomUtil.checkChance(instance.randomSource(), entity.growthChance)) {
                return;
            }
            final int newAge = entity.age + 1;
            entity.setAge(newAge);
            if (entity.isFullyGrown()) {
                entity.onMaxGrowth(instance, block, blockPosition, false);
                return;
            }
            final Block newBlock = entity.ageProperty.set(block, newAge).withTag(BlockTags.CROP_AGE, newAge);
            instance.setBlock(blockPosition, newBlock);
        }

    }


}
