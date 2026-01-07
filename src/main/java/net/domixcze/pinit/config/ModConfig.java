package net.domixcze.pinit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.domixcze.pinit.PinIt;
import net.minecraft.util.Identifier;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "pinit.json");

    public List<Integer> pinnedSlotIndices = new java.util.ArrayList<>();
    public boolean totalCountMode = false;
    public boolean enableSlotPinning = true;
    public boolean enableRecipePinning = true;

    public HudPosition hudPosition = HudPosition.RIGHT;
    public int hudTextColor = 0xFFFFFF;
    public float hudIconScale = 1.0f;
    public boolean enableHudAnimations = true;

    public PinShape selectedShape = PinShape.SQUARE_PIN;
    public int pinColor = 0x5555ff;
    public java.util.List<String> pinnedRecipeIds = new java.util.ArrayList<>();

    public enum PinShape {
        SQUARE_PIN(Identifier.of(PinIt.MOD_ID, "textures/gui/pin_base.png"), Identifier.of(PinIt.MOD_ID, "textures/gui/square_pin_overlay.png")),
        CIRCLE_PIN(Identifier.of(PinIt.MOD_ID, "textures/gui/pin_base.png"), Identifier.of(PinIt.MOD_ID, "textures/gui/circle_pin_overlay.png")),
        FLOPPY_DISK(Identifier.of(PinIt.MOD_ID, "textures/gui/floppy_disk_base.png"), Identifier.of(PinIt.MOD_ID, "textures/gui/floppy_disk_overlay.png")),
        CREEPER(Identifier.of(PinIt.MOD_ID, "textures/gui/creeper_base.png"), Identifier.of(PinIt.MOD_ID, "textures/gui/creeper_overlay.png")),

        CIRCLE(null, Identifier.of(PinIt.MOD_ID, "textures/gui/circle.png")),
        LOCK(null, Identifier.of(PinIt.MOD_ID, "textures/gui/lock.png")),
        BAT(null, Identifier.of(PinIt.MOD_ID, "textures/gui/bat.png")),
        OCTOPUS(null, Identifier.of(PinIt.MOD_ID, "textures/gui/octopus.png")),
        CROSS(null, Identifier.of(PinIt.MOD_ID, "textures/gui/cross.png")),
        CHECKMARK(null, Identifier.of(PinIt.MOD_ID, "textures/gui/checkmark.png")),

        PAPER_CLIP(null, Identifier.of(PinIt.MOD_ID, "textures/gui/paper_clip.png")),
        STAR(null, Identifier.of(PinIt.MOD_ID, "textures/gui/star.png")),
        HEART(null, Identifier.of(PinIt.MOD_ID, "textures/gui/heart.png"));

        public final Identifier base;
        public final Identifier overlay;

        PinShape(Identifier base, Identifier overlay) {
            this.base = base;
            this.overlay = overlay;
        }
    }

    public enum HudPosition {
        LEFT, RIGHT
    }

    public static ModConfig INSTANCE = new ModConfig();

    // Load config from file
    public static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
                if (INSTANCE == null) { // Handle case where file is empty or malformed
                    INSTANCE = new ModConfig();
                    saveConfig();
                }
            } catch (IOException e) {
                System.err.println("Failed to load config for PinIt: " + e.getMessage());
                INSTANCE = new ModConfig(); // Reset to defaults on error
                saveConfig(); // Save defaults
            }
        } else {
            saveConfig(); // Create default config if file doesn't exist
        }
    }

    // Save config to file
    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent()); // Ensure config directory exists
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save config for PinIt: " + e.getMessage());
        }
    }
}