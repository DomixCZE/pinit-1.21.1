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

        // color of the pin
        general.addEntry(entryBuilder.startColorField(
                        Text.translatable("pinit.config.option.pinColor"),
                        ModConfig.INSTANCE.pinColor)
                .setDefaultValue(0x5555ff)
                .setTooltip(Text.translatable("pinit.config.option.pinColor.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.pinColor = newValue)
                .build());

        // count only items in pinned slot or the whole inventory
        general.addEntry(entryBuilder.startBooleanToggle(
                        Text.translatable("pinit.config.option.totalCountMode"),
                        ModConfig.INSTANCE.totalCountMode)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("pinit.config.option.totalCountMode.tooltip"))
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.totalCountMode = newValue)
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

        return builder.build();
    }
}
