/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration;

import com.sschr15.mods.macintegration.menubar.objc.Submenu;
import com.terraformersmc.modmenu.gui.ModsScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

public class FabricPlatform implements Platform {
    @Override
    public void addModsOption(Submenu appMenu) {
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            ModMenuOption.init(appMenu);
        }
    }

    @Override
    public String modLoaderInfo() {
        return FabricLoader.getInstance().getModContainer("fabricloader").map(mod -> mod.getMetadata().getName() + " " + mod.getMetadata().getVersion()).orElse("Fabric");
    }

    @Override
    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("mac-integration.properties");
    }

    @Override
    public boolean isModLoaded(String mod) {
        return FabricLoader.getInstance().isModLoaded(mod);
    }
}

// separated to avoid potential class loading issues - optional dependencies is hard tm
class ModMenuOption {
    static void init(Submenu appMenu) {
        appMenu.option(Component.translatable("modmenu.title"), "", () -> Minecraft.getInstance().setScreen(new ModsScreen(Minecraft.getInstance().screen)));
    }
}
