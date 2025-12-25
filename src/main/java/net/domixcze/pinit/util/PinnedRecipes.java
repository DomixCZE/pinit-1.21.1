package net.domixcze.pinit.util;

import net.domixcze.pinit.config.ModConfig;
import net.minecraft.util.Identifier;

public class PinnedRecipes {
    public static void toggle(Identifier id) {
        String idStr = id.toString();
        if (ModConfig.INSTANCE.pinnedRecipeIds.contains(idStr)) {
            ModConfig.INSTANCE.pinnedRecipeIds.remove(idStr);
        } else {
            ModConfig.INSTANCE.pinnedRecipeIds.add(idStr);
        }
        ModConfig.saveConfig();
    }

    public static boolean isPinned(Identifier id) {
        return ModConfig.INSTANCE.pinnedRecipeIds.contains(id.toString());
    }
}