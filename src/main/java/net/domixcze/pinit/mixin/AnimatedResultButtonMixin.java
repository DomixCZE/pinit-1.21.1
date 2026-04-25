package net.domixcze.pinit.mixin;

import net.domixcze.pinit.config.ModConfig;
import net.domixcze.pinit.util.PinnedRecipes;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.recipe.RecipeDisplayEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimatedResultButton.class)
public abstract class AnimatedResultButtonMixin {

    @Shadow private RecipeResultCollection resultCollection;

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void pinit$renderPinIcon(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enableRecipePinning) return;

        if (this.resultCollection != null && !this.resultCollection.getAllRecipes().isEmpty()) {
            RecipeDisplayEntry entry = this.resultCollection.getAllRecipes().getFirst();

            if (PinnedRecipes.isPinned(entry)) {
                @SuppressWarnings("ConstantConditions")
                ClickableWidget button = (ClickableWidget)(Object)this;
                int x = button.getX() + 16;
                int y = button.getY() + 1;

                ModConfig.PinShape shape = ModConfig.INSTANCE.selectedShape;

                context.getMatrices().push();
                context.getMatrices().translate(0, 0, 200);

                if (shape.base != null) {
                    context.drawTexture(
                            RenderLayer::getGuiTextured,
                            shape.base,
                            x, y,
                            0.0F, 0.0F,
                            8, 8,
                            8, 8,
                            0xFFFFFFFF
                    );
                }

                if (shape.overlay != null) {
                    int color = ModConfig.INSTANCE.pinColor;
                    int argbColor = 0xFF000000 | color;

                    context.drawTexture(
                            RenderLayer::getGuiTextured,
                            shape.overlay,
                            x, y,
                            0.0F, 0.0F,
                            8, 8,
                            8, 8,
                            argbColor
                    );
                }

                context.getMatrices().pop();
            }
        }
    }
}
