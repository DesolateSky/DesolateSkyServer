package com.fisherl.desolatesky.block.tag;

import com.fisherl.desolatesky.util.KeyUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Direction;

public final class BlockTags {

    private BlockTags() {}

    public static final Tag<Key> ID = Tag.String(KeyUtil.desolateSky("id").asString()).map(Key::key, Key::asString);
    public static final Tag<Direction> FACING = Tag.String("direction").map(Direction::valueOf, Direction::name);

}
