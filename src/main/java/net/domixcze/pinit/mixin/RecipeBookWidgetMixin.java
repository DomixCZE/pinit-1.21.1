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
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public abstract class RecipeBookWidgetMixin implements RecipeBookWidgetDuck {
    @Shadow
    @Final
    private RecipeBookResults recipesArea;

    @Invoker("refreshResults")
    public abstract void pinit$invokeRefreshResults(boolean resetPage);

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void pinit$onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.INSTANCE.enableRecipePinning) return;

        if (PinItClient.PIN_RECIPE_KEY.matchesKey(keyCode, scanCode)) {

            RecipeResultCollection hovered = this.pinit$getHoveredCollection();
            if (hovered != null && !hovered.getAllRecipes().isEmpty()) {
                Identifier id = hovered.getAllRecipes().getFirst().id();
                PinnedRecipes.toggle(id);

                MinecraftClient.getInstance().getSoundManager().play(
                        PositionedSoundInstance.master(ModSounds.PIN, 1.5F)
                );

                this.pinit$refresh();
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

    @Override
    public void pinit$refresh() {
        this.pinit$invokeRefreshResults(false);
    }
}