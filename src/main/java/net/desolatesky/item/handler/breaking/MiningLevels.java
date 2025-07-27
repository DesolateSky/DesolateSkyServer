package net.desolatesky.item.handler.breaking;

public final class MiningLevels {

    private MiningLevels() {
        throw new UnsupportedOperationException();
    }

    public static final MiningLevel NONE = new MiningLevel(0, 1.0);
    public static final MiningLevel WOOD = new MiningLevel(1, 1.1);
    public static final MiningLevel STONE = new MiningLevel(2, 1.2);
    public static final MiningLevel COPPER = new MiningLevel(3, 1.3);
    public static final MiningLevel IRON = new MiningLevel(4, 1.4);
    public static final MiningLevel REDSTONE = new MiningLevel(5, 1.5);
    public static final MiningLevel GOLD = new MiningLevel(6, 1.6);
    public static final MiningLevel EMERALD = new MiningLevel(7, 1.7);
    public static final MiningLevel DIAMOND = new MiningLevel(8, 1.8);
    public static final MiningLevel OBSIDIAN = new MiningLevel(9, 1.9);
    public static final MiningLevel NETHERITE = new MiningLevel(10, 2.0);

}
