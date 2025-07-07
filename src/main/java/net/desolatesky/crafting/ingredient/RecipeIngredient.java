package net.desolatesky.crafting.ingredient;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface RecipeIngredient extends Keyed {

    RecipeIngredient EMPTY = new RecipeIngredient() {

        private static final Key KEY = Namespace.key("empty");

        @Override
        public @NotNull Key key() {
            return KEY;
        }

    };

}
