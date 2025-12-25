package net.domixcze.pinit.sound;

import net.domixcze.pinit.PinIt;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent PIN = registerSoundEvent("pin");
    public static final SoundEvent PIN_DENY = registerSoundEvent("pin_deny");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(PinIt.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        PinIt.LOGGER.info("Registering Sounds for " + PinIt.MOD_ID);
    }
}