package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.util.BlockUtil;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.registry.RegistryTag;
import net.minestom.server.registry.TagKey;
import net.minestom.server.utils.Direction;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SupportedBlockBehavior implements PlaceRequirementsBehavior {

    public static final class Serializer extends BlockBehaviorSerializer<SupportedBlockBehavior> {

        public Serializer() {
            super(Namespace.key("supported_block"));
        }

        private static final String SUPPORT_DIRECTION_KEY = "support-direction";
        private static final String SUPPORTING_BLOCK_TAGS_KEY = "supporting-block-tags";
        private static final String SUPPORTING_BLOCKS_KEY = "supporting-blocks";
        private static final String SUPPORTS_SELF_KEY = "supports-self";

        @Override
        public SupportedBlockBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final Direction supportDirection = node.node(SUPPORT_DIRECTION_KEY).get(Direction.class);
            final Collection<RegistryTag<Block>> supportingBlockTags = node.node(SUPPORTING_BLOCK_TAGS_KEY).getList(String.class, new ArrayList<>())
                    .stream()
                    .map(TagKey::<Block>ofHash)
                    .map(Block.staticRegistry()::getTag)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            final Set<Key> supportingBlocks = new HashSet<>(node.node(SUPPORTING_BLOCKS_KEY).getList(Key.class, new ArrayList<>()));
            final boolean supportsSelf = node.node(SUPPORTS_SELF_KEY).getBoolean();
            return new SupportedBlockBehavior(supportDirection, supportingBlockTags, supportingBlocks, _ -> true, supportsSelf);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable SupportedBlockBehavior obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) {
                return;
            }
            node.node(SUPPORT_DIRECTION_KEY).set(obj.supportDirection);
            node.node(SUPPORTING_BLOCK_TAGS_KEY).setList(Key.class, obj.supportingBlockTags.stream()
                    .map(RegistryTag::key)
                    .filter(Objects::nonNull)
                    .map(TagKey::key)
                    .toList());
            node.node(SUPPORTING_BLOCKS_KEY).setList(Key.class, new ArrayList<>(obj.supportingBlocks));
            node.node(SUPPORTS_SELF_KEY).set(obj.supportsSelf);
        }

        @Override
        public Class<SupportedBlockBehavior> behaviorClass() {
            return SupportedBlockBehavior.class;
        }
    }

    private final Direction supportDirection;
    private final Collection<RegistryTag<Block>> supportingBlockTags;
    private final Set<Key> supportingBlocks;
    private final Predicate<Block> predicate;
    private final boolean supportsSelf;

    public SupportedBlockBehavior(
            Direction supportDirection,
            Collection<RegistryTag<Block>> supportingBlockTags,
            Set<Key> supportingBlocks,
            Predicate<Block> predicate,
            boolean supportsSelf
    ) {
        this.supportDirection = supportDirection;
        this.supportingBlockTags = supportingBlockTags;
        this.supportingBlocks = supportingBlocks;
        this.predicate = predicate;
        this.supportsSelf = supportsSelf;
    }

    public SupportedBlockBehavior(Direction supportDirection, Collection<RegistryTag<Block>> supportingBlockTags, boolean supportsSelf) {
        this(supportDirection, supportingBlockTags, Collections.emptySet(), _ -> true, supportsSelf);
    }

    public SupportedBlockBehavior(Direction supportDirection, boolean supportsSelf, Set<Key> supportingBlocks) {
        this(supportDirection, Collections.emptySet(), supportingBlocks, _ -> true, supportsSelf);
    }

    @Override
    public Result checkState(DSWorld world, Point pos, Block block) {
        final Point toCheck = pos.add(this.supportDirection.vec());
        final Block at = world.getBlock(toCheck);
        if (!this.predicate.test(at)) {
            return Result.DESTROY_AND_DROP;
        }
        if (this.supportsSelf && BlockUtil.isSameBlock(block, at)) {
            return Result.GOOD;
        }
        if (this.supportingBlocks.contains(at.key())) {
            return Result.GOOD;
        }
        final RegistryKey<Block> blockKey = at.asKey();
        if (blockKey == null) {
            return this.supportingBlocks.isEmpty() ? Result.GOOD : Result.DESTROY_AND_DROP;
        }
        return this.supportingBlockTags.stream().anyMatch(regKey -> regKey.contains(blockKey)) ?
                Result.GOOD :
                Result.DESTROY_AND_DROP;
    }

    @Override
    public boolean isValidForInitialPlace(DSWorld world, Point pos, Block block) {
        return this.checkState(world, pos, block) == Result.GOOD;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.PLACE_REQUIREMENTS);
    }
}
