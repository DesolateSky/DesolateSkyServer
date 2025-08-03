package net.desolatesky.tag;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class Tags {

    private Tags() {
        throw new UnsupportedOperationException();
    }

    public static Tag<Key> Key(String name) {
        return Tag.String(name).map(Key::key, Key::asString);
    }

    public static Tag<Key> NamespaceKey(String name) {
        return Tag.String(name).map(Namespace::key, Key::asString);
    }

    public static @NotNull Tag<Byte> Byte(@NotNull String key) {
        return Tag.Byte(key);
    }

    public static @NotNull Tag<Boolean> Boolean(@NotNull String key) {
        return Tag.Boolean(key);
    }

    public static @NotNull Tag<Short> Short(@NotNull String key) {
        return Tag.Short(key);
    }

    public static @NotNull Tag<Integer> Integer(@NotNull String key) {
        return Tag.Integer(key);
    }

    public static @NotNull Tag<Long> Long(@NotNull String key) {
        return Tag.Long(key);
    }

    public static @NotNull Tag<Float> Float(@NotNull String key) {
        return Tag.Float(key);
    }

    public static @NotNull Tag<Double> Double(@NotNull String key) {
        return Tag.Double(key);
    }

    public static @NotNull Tag<String> String(@NotNull String key) {
        return Tag.String(key);
    }

    public static @NotNull Tag<UUID> UUID(@NotNull String key) {
        return Tag.UUID(key);
    }

    public static @NotNull Tag<ItemStack> ItemStack(@NotNull String key) {
        return Tag.ItemStack(key);
    }

    public static @NotNull Tag<Component> Component(@NotNull String key) {
        return Tag.Component(key);
    }

    public static @NotNull Tag<BinaryTag> NBT(@NotNull String key) {
        return Tag.NBT(key);
    }

    public static @NotNull <E extends Enum<E>> Tag<E> Enum(@NotNull String key, @NotNull Class<E> enumClass) {
        return Tag.String(key).map(name -> Enum.valueOf(enumClass, name), E::name);
    }

    public static <T> @NotNull Tag<T> Structure(@NotNull String key, @NotNull TagSerializer<T> serializer) {
        return Tag.Structure(key, serializer);
    }

    public static <T> @NotNull Tag<T> View(@NotNull TagSerializer<T> serializer) {
        return Tag.View(serializer);
    }

    @ApiStatus.Experimental
    public static <T extends Record> @NotNull Tag<T> Structure(@NotNull String key, @NotNull Class<T> type) {
        return Tag.Structure(key, type);
    }

    @ApiStatus.Experimental
    public static <T extends Record> @NotNull Tag<T> View(@NotNull Class<T> type) {
        return Tag.View(type);
    }

    public static <T> @NotNull Tag<T> Transient(@NotNull String key) {
        return Tag.Transient(key);
    }


}
