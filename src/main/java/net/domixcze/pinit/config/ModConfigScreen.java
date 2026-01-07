package net.domixcze.pinit.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModConfigScreen {
    public static Screen getScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("pinit.config.title"));

        builder.setSavingRunnable(ModConfig::saveConfig);

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("pinit.config.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // shape of the pin
        general.addEntry(entryBuilder.startEnumSelector(
                        Text.translatable("pinit.config.option.pinShape"),
                        ModConfig.PinShape.class,
                        ModConfig.INSTANCE.selectedShape)
                .setDefaultValue(ModConfig.PinShape.SQUARE_PIN)
                .setEnumNameProvider(value -> Text.translatable("pinit.shape." + value.name().toLowerCase()))
                .setTooltip(Text.translatable("pinit.config.option.pinShape.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.selectedShape = newValue)
                .build());

        // HUD position
        general.addEntry(entryBuilder.startEnumSelector(
                        Text.translatable("pinit.config.option.hudPosition"),
                        ModConfig.HudPosition.class,
                        ModConfig.INSTANCE.hudPosition)
                .setDefaultValue(ModConfig.HudPosition.RIGHT)
                .setEnumNameProvider(value -> Text.translatable("pinit.position." + value.name().toLowerCase()))
                .setTooltip(Text.translatable("pinit.config.option.hudPosition.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.hudPosition = newValue)
                .build());

        // pinning inventory slots
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("pinit.config.option.enableSlotPinning"),
                        ModConfig.INSTANCE.enableSlotPinning)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("pinit.config.option.enableSlotPinning.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableSlotPinning = newValue)
                .build());

        // pinning recipes in the vanilla recipe book
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("pinit.config.option.enableRecipePinning"),
                        ModConfig.INSTANCE.enableRecipePinning)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("pinit.config.option.enableRecipePinning.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableRecipePinning = newValue)
                .build());

        // count only items in pinned slot or the whole inventory
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("pinit.config.option.totalCountMode"),
                        ModConfig.INSTANCE.totalCountMode)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("pinit.config.option.totalCountMode.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.totalCountMode = newValue)
                .build());

        // HUD animations
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("pinit.config.option.enableHudAnimations"),
                        ModConfig.INSTANCE.enableHudAnimations)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("pinit.config.option.enableHudAnimations.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enableHudAnimations = newValue)
                .build());

        // HUD icon scale
        general.addEntry(entryBuilder.startFloatField(
                        Text.translatable("pinit.config.option.hudIconScale"),
                        ModConfig.INSTANCE.hudIconScale)
                .setDefaultValue(1.0f)
                .setMin(0.3f)
                .setMax(1.3f)
                .setTooltip(Text.translatable("pinit.config.option.hudIconScale.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.hudIconScale = newValue)
                .build());

        // color of the pin
        general.addEntry(entryBuilder.startColorField(
                        Text.translatable("pinit.config.option.pinColor"),
                        ModConfig.INSTANCE.pinColor)
                .setDefaultValue(0x5555ff)
                .setTooltip(Text.translatable("pinit.config.option.pinColor.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.pinColor = newValue)
                .build());

        // HUD text color
        general.addEntry(entryBuilder.startColorField(
                        Text.translatable("pinit.config.option.hudTextColor"),
                        ModConfig.INSTANCE.hudTextColor)
                .setDefaultValue(0xFFFFFF)
                .setTooltip(Text.translatable("pinit.config.option.hudTextColor.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.hudTextColor = newValue)
                .build());

        return builder.build();
    }
}
