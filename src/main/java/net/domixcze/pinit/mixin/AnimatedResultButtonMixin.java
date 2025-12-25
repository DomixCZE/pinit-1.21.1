package net.domixcze.pinit.mixin;

import net.domixcze.pinit.config.ModConfig;
import net.domixcze.pinit.util.PinnedRecipes;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.Identifier;
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
            Identifier id = this.resultCollection.getAllRecipes().getFirst().id();

            if (PinnedRecipes.isPinned(id)) {
                @SuppressWarnings("ConstantConditions")
                ClickableWidget button = (ClickableWidget)(Object)this;
                int x = button.getX() + 16;
                int y = button.getY() + 1;

                ModConfig.PinShape shape = ModConfig.INSTANCE.selectedShape;

                context.getMatrices().push();
                context.getMatrices().translate(0, 0, 200);

                if (shape.base != null) {
                    context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    context.drawTexture(shape.base, x, y, 0, 0, 8, 8, 8, 8);
                }

                if (shape.overlay != null) {
                    int color = ModConfig.INSTANCE.pinColor;
                    float r = (float)(color >> 16 & 255) / 255.0F;
                    float g = (float)(color >> 8 & 255) / 255.0F;
                    float b = (float)(color & 255) / 255.0F;

                    context.setShaderColor(r, g, b, 1.0F);
                    context.drawTexture(shape.overlay, x, y, 0, 0, 8, 8, 8, 8);

                    context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                }

                context.getMatrices().pop();
            }
        }
    }
}