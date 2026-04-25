package net.domixcze.pinit.util;

import net.domixcze.pinit.config.ModConfig;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;
import java.util.stream.Collectors;

public class PinnedRecipes {

    public static void toggle(RecipeDisplayEntry entry) {
        String key = getPinKey(entry);

        if (ModConfig.INSTANCE.pinnedRecipeIds.contains(key)) {
            ModConfig.INSTANCE.pinnedRecipeIds.remove(key);
        } else {
            ModConfig.INSTANCE.pinnedRecipeIds.add(key);
        }

        ModConfig.saveConfig();
    }

    public static boolean isPinned(RecipeDisplayEntry entry) {
        return ModConfig.INSTANCE.pinnedRecipeIds.contains(getPinKey(entry));
    }

    private static String getPinKey(RecipeDisplayEntry entry) {
        String category = entry.category().toString();
        String result = entry.display().result().toString();

        String ingredients = entry.craftingRequirements()
                .map(PinnedRecipes::getIngredientsKey)
                .orElse("no_ingredients");

        return category + "|" + result + "|" + ingredients;
    }

    private static String getIngredientsKey(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(PinnedRecipes::getIngredientKey)
                .collect(Collectors.joining(";"));
    }

    @SuppressWarnings("deprecation")
    private static String getIngredientKey(Ingredient ingredient) {
        return ingredient.getMatchingItems()
                .map(PinnedRecipes::getItemId)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private static String getItemId(RegistryEntry<Item> itemEntry) {
        return itemEntry.getKey()
                .map(key -> key.getValue().toString())
                .orElseGet(() -> Registries.ITEM.getId(itemEntry.value()).toString());
    }
}
