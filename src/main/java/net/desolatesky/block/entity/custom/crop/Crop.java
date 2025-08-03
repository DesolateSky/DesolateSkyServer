package net.desolatesky.block.entity.custom.crop;

import net.desolatesky.tag.Tags;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Crop(int maxAge, CropRarity rarity) {

    public Crop withRarity(CropRarity rarity) {
        return new Crop(this.maxAge, rarity);
    }

    public static final TagSerializer<Crop> SERIALIZER = new Serializer();

    private static final class Serializer implements TagSerializer<Crop> {

        private static final Tag<Integer> MAX_AGE_TAG = Tags.Integer("max_age");
        private static final Tag<CropRarity> CROP_RARITY_TAG = Tags.Enum("rarity", CropRarity.class);

        @Override
        public @Nullable Crop read(@NotNull TagReadable reader) {
            System.out.println(reader.hasTag(MAX_AGE_TAG) + " " + reader.hasTag(CROP_RARITY_TAG));
            final Integer maxAge = reader.getTag(MAX_AGE_TAG);
            if (maxAge == null) {
                return null;
            }
            final CropRarity rarity = reader.getTag(CROP_RARITY_TAG);
            if (rarity == null) {
                return null;
            }
            return new Crop(maxAge, rarity);
        }

        @Override
        public void write(@NotNull TagWritable writer, @NotNull Crop value) {
            writer.setTag(MAX_AGE_TAG, value.maxAge);
            writer.setTag(CROP_RARITY_TAG, value.rarity);
        }
    }

}
