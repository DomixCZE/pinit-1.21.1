package net.domixcze.pinit.mixin;

import net.domixcze.pinit.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void pinit$renderInventoryPins(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enableSlotPinning) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        List<Integer> pinnedIndices = ModConfig.INSTANCE.pinnedSlotIndices;
        if (pinnedIndices.isEmpty()) return;

        int xPos = context.getScaledWindowWidth() - 30;
        int yPos = 10;
        Set<Item> processedItems = new HashSet<>();

        for (int invIndex : pinnedIndices) {
            ItemStack stack = client.player.getInventory().getStack(invIndex);

            if (stack.isEmpty()) continue;

            if (ModConfig.INSTANCE.totalCountMode) {
                if (processedItems.contains(stack.getItem())) continue;
                processedItems.add(stack.getItem());
            }

            int count = ModConfig.INSTANCE.totalCountMode ?
                    pinit$getTotalCount(client.player, stack.getItem()) : stack.getCount();

            pinit$drawHudEntry(context, client, stack, count, xPos, yPos);
            yPos += 20;
        }
    }

    @Unique
    private int pinit$getTotalCount(PlayerEntity player, Item item) {
        int total = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.isOf(item)) total += s.getCount();
        }
        return total;
    }

    @Unique
    private void pinit$drawHudEntry(DrawContext context, MinecraftClient client, ItemStack stack, int count, int x, int y) {
        context.drawItem(stack, x, y);

        String text = String.valueOf(count);
        int textWidth = client.textRenderer.getWidth(text);
        context.drawTextWithShadow(client.textRenderer, text, x - textWidth - 2, y + 4, 0xFFFFFF);

        pinit$renderPinOverlay(context, x + 8, y);
    }

    @Unique
    private void pinit$renderPinOverlay(DrawContext context, int x, int y) {
        ModConfig.PinShape shape = ModConfig.INSTANCE.selectedShape;
        int color = ModConfig.INSTANCE.pinColor;
        float r = (float)(color >> 16 & 255) / 255.0f;
        float g = (float)(color >> 8 & 255) / 255.0f;
        float b = (float)(color & 255) / 255.0f;

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 200);

        if (shape.base != null) {
            context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            context.drawTexture(shape.base, x, y, 0, 0, 8, 8, 8, 8);
        }
        if (shape.overlay != null) {
            context.setShaderColor(r, g, b, 1.0f);
            context.drawTexture(shape.overlay, x, y, 0, 0, 8, 8, 8, 8);
            context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        context.getMatrices().pop();
    }
}