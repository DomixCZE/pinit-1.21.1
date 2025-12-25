package net.domixcze.pinit;

import net.domixcze.pinit.config.ModConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PinItClient implements ClientModInitializer {
    public static KeyBinding PIN_RECIPE_KEY;
    public static KeyBinding OPEN_CONFIG_KEY;

    @Override
    public void onInitializeClient() {

        PIN_RECIPE_KEY = new KeyBinding(
                "key.pinit.pin_recipe",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,  // default keybind R
                "category.pinit.general"
        );

        KeyBindingHelper.registerKeyBinding(PIN_RECIPE_KEY);


        OPEN_CONFIG_KEY = new KeyBinding(
                "key.pinit.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U, // default keybind U
                "category.pinit.general"
        );
        KeyBindingHelper.registerKeyBinding(OPEN_CONFIG_KEY);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (OPEN_CONFIG_KEY.wasPressed()) {
                client.setScreen(ModConfigScreen.getScreen(null));
            }
        });
    }
}