package net.desolatesky.block;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;

public final class BlockAttributes {

    private BlockAttributes() {}

    public static final Key PICKAXE_MINEABLE = Namespace.minecraftKey("mineable/pickaxe");
    public static final Key AXE_MINEABLE = Namespace.minecraftKey("mineable/axe");
    public static final Key SHOVEL_MINEABLE = Namespace.minecraftKey("mineable/shovel");
    public static final Key HOE_MINEABLE = Namespace.minecraftKey("mineable/hoe");
    public static final Key SWORD_MINEABLE = Namespace.minecraftKey("mineable/sword");
    public static final Key FLAMMABLE = Namespace.key("flammable");
    public static final Key FIRE_STARTER = Namespace.key("fire_starter");

}
