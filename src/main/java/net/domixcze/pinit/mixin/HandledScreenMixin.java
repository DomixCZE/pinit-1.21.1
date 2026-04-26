package net.domixcze.pinit.mixin;

import net.domixcze.pinit.PinItClient;
import net.domixcze.pinit.config.ModConfig;
import net.domixcze.pinit.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow @Nullable protected Slot focusedSlot;
    @Shadow protected int x;
    @Shadow protected int y;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void pinit$onInventoryKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.INSTANCE.enableSlotPinning) return;
        if (PinItClient.PIN_RECIPE_KEY.matchesKey(keyCode, scanCode)) {
            if (this.focusedSlot != null) {
                Slot targetSlot = this.focusedSlot;

                if (targetSlot instanceof CreativeInventoryScreen.CreativeSlot) {
                    targetSlot = ((CreativeSlotAccessor) targetSlot).pinit$getParentSlot();
                }

                if (targetSlot.inventory instanceof PlayerInventory) {
                    int invIndex = targetSlot.getIndex();

                    if (invIndex >= 0 && invIndex <= 35) {
                        List<Integer> pinned = ModConfig.INSTANCE.pinnedSlotIndices;
                        boolean success = false;

                        if (pinned.contains(invIndex)) {
                            pinned.remove(Integer.valueOf(invIndex));
                            success = true;
                        } else if (pinned.size() < 9) {
                            pinned.add(invIndex);
                            success = true;
                        } else {
                            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(ModSounds.PIN_DENY, 0.5f));
                        }

                        if (success) {
                            ModConfig.saveConfig();
                            MinecraftClient.getInstance().getSoundManager().play(
                                    PositionedSoundInstance.master(ModSounds.PIN, 1.2f)
                            );
                            cir.setReturnValue(true);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void pinit$renderPinsOnSlots(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ModConfig.INSTANCE.enableSlotPinning) return;

        @SuppressWarnings("ConstantConditions")
        HandledScreen<?> screen = (HandledScreen<?>)(Object)this;

        for (Slot slot : screen.getScreenHandler().slots) {
            Slot targetSlot = slot;

            if (targetSlot instanceof CreativeInventoryScreen.CreativeSlot) {
                targetSlot = ((CreativeSlotAccessor) targetSlot).pinit$getParentSlot();
            }

            if (targetSlot.inventory instanceof PlayerInventory) {
                int invIndex = targetSlot.getIndex();

                if (invIndex >= 0 && invIndex <= 35 && ModConfig.INSTANCE.pinnedSlotIndices.contains(invIndex)) {

                    if (screen instanceof CreativeInventoryScreen creativeScreen) {
                        if (creativeScreen.isInventoryTabSelected() || invIndex < 9) {
                            pinit$drawPin(context, this.x + slot.x, this.y + slot.y);
                        }
                    } else {
                        pinit$drawPin(context, this.x + slot.x, this.y + slot.y);
                    }
                }
            }
        }
    }

    @Unique
    private void pinit$drawPin(DrawContext context, int x, int y) {
        ModConfig.PinShape shape = ModConfig.INSTANCE.selectedShape;
        int color = ModConfig.INSTANCE.pinColor;
        int argbColor = 0xFF000000 | color;

        context.getMatrices().pushMatrix();

        if (shape.base != null) {
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    shape.base,
                    x + 8, y,
                    0.0F, 0.0F,
                    8, 8,
                    8, 8,
                    0xFFFFFFFF
            );
        }

        if (shape.overlay != null) {
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    shape.overlay,
                    x + 8, y,
                    0.0F, 0.0F,
                    8, 8,
                    8, 8,
                    argbColor
            );
        }

        context.getMatrices().popMatrix();
    }
}
