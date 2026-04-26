package net.domixcze.pinit.mixin;

import net.domixcze.pinit.PinItClient;
import net.domixcze.pinit.config.ModConfig;
import net.domixcze.pinit.sound.ModSounds;
import net.domixcze.pinit.util.PinnedRecipes;
import net.domixcze.pinit.util.RecipeBookWidgetDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.recipe.RecipeDisplayEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin implements RecipeBookWidgetDuck {
    @Shadow
    @Final
    private RecipeBookResults recipesArea;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void pinit$onKeyPressed(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.INSTANCE.enableRecipePinning) return;

        if (PinItClient.PIN_RECIPE_KEY.matchesKey(keyInput)) {
            RecipeResultCollection hovered = this.pinit$getHoveredCollection();

            if (hovered != null && !hovered.getAllRecipes().isEmpty()) {
                RecipeDisplayEntry entry = hovered.getAllRecipes().getFirst();
                PinnedRecipes.toggle(entry);

                MinecraftClient.getInstance().getSoundManager().play(
                        PositionedSoundInstance.master(ModSounds.PIN, 1.5F)
                );

                ((RecipeBookWidget<?>)(Object)this).refresh();

                cir.setReturnValue(true);
            }
        }
    }

    @Override
    public RecipeResultCollection pinit$getHoveredCollection() {
        for (AnimatedResultButton button : ((RecipeBookResultsAccessor) recipesArea).getResultButtons()) {
            if (button.isHovered()) {
                return button.getResultCollection();
            }
        }
        return null;
    }
}