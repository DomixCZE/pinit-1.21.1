package net.domixcze.pinit.mixin;

import net.domixcze.pinit.config.ModConfig;
import net.domixcze.pinit.util.PinnedRecipes;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(RecipeBookResults.class)
public class RecipeBookResultsMixin {

    @ModifyVariable(method = "setResults", at = @At("HEAD"), argsOnly = true)
    private List<RecipeResultCollection> pinit$sortPinnedToFront(List<RecipeResultCollection> results) {
        if (!ModConfig.INSTANCE.enableRecipePinning || results == null || results.isEmpty()) {
            return results;
        }

        List<RecipeResultCollection> pinned = new ArrayList<>();
        List<RecipeResultCollection> unpinned = new ArrayList<>();

        for (RecipeResultCollection collection : results) {
            boolean isPinned = collection.getAllRecipes().stream()
                    .anyMatch(recipe -> PinnedRecipes.isPinned(recipe.id()));

            if (isPinned) {
                pinned.add(collection);
            } else {
                unpinned.add(collection);
            }
        }

        List<RecipeResultCollection> combined = new ArrayList<>();
        combined.addAll(pinned);
        combined.addAll(unpinned);
        return combined;
    }
}