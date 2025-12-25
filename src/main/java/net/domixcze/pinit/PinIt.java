package net.domixcze.pinit;

import net.domixcze.pinit.config.ModConfig;
import net.domixcze.pinit.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PinIt implements ModInitializer {
	public static final String MOD_ID = "pinit";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfig.loadConfig();

		ModSounds.registerSounds();
	}
}