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

import java.util.*;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique
    private final Map<Integer, Long> pinit$animationTimers = new HashMap<>();
    @Unique
    private final Map<Integer, Integer> pinit$lastCounts = new HashMap<>();

    @Inject(method = "render", at = @At("TAIL"))
    private void pinit$renderInventoryPins(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enableSlotPinning) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        List<Integer> pinnedIndices = ModConfig.INSTANCE.pinnedSlotIndices;
        if (pinnedIndices.isEmpty()) return;

        boolean isLeft = ModConfig.INSTANCE.hudPosition == ModConfig.HudPosition.LEFT;
        float baseScale = ModConfig.INSTANCE.hudIconScale;

        int xPos = isLeft ? 10 : (int) (context.getScaledWindowWidth() - (20 * baseScale) - 10);
        int yPos = 10;

        Set<Item> processedItems = new HashSet<>();

        for (int invIndex : pinnedIndices) {
            ItemStack stack = client.player.getInventory().getStack(invIndex);

            if (stack.isEmpty()) {
                pinit$lastCounts.remove(invIndex);
                continue;
            }

            if (ModConfig.INSTANCE.totalCountMode) {
                if (processedItems.contains(stack.getItem())) continue;
                processedItems.add(stack.getItem());
            }

            int count = ModConfig.INSTANCE.totalCountMode ?
                    pinit$getTotalCount(client.player, stack.getItem()) : stack.getCount();

            if (pinit$lastCounts.getOrDefault(invIndex, -1) != count) {
                pinit$lastCounts.put(invIndex, count);
                if (ModConfig.INSTANCE.enableHudAnimations) {
                    pinit$animationTimers.put(invIndex, System.currentTimeMillis());
                }
            }

            float bounceScale = ModConfig.INSTANCE.enableHudAnimations ? pinit$getBounceScale(invIndex) : 1.0f;
            float finalScale = baseScale * bounceScale;

            pinit$drawHudEntry(context, client, stack, count, xPos, yPos, isLeft, finalScale);

            yPos += (int) (20 * baseScale);
        }
    }

    @Unique
    private float pinit$getBounceScale(int invIndex) {
        Long startTime = pinit$animationTimers.get(invIndex);
        if (startTime == null) return 1.0f;

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 300) {
            pinit$animationTimers.remove(invIndex);
            return 1.0f;
        }

        return 1.0f + (float) Math.sin((elapsed / 300.0) * Math.PI) * 0.2f;
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
    private void pinit$drawHudEntry(DrawContext context, MinecraftClient client, ItemStack stack, int count, int x, int y, boolean isLeft, float scale) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);

        context.drawItem(stack, 0, 0);

        String text = String.valueOf(count);
        int textWidth = client.textRenderer.getWidth(text);

        int textX = isLeft ? 18 : -textWidth - 2;

        context.drawTextWithShadow(client.textRenderer, text, textX, 4, ModConfig.INSTANCE.hudTextColor);

        int pinX = isLeft ? 0 : 8;
        pinit$renderPinOverlay(context, pinX, 0);

        context.getMatrices().pop();
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