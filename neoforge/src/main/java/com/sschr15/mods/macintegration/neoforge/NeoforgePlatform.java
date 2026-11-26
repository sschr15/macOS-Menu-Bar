/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.neoforge;

import com.sschr15.mods.macintegration.Platform;
import com.sschr15.mods.macintegration.menubar.objc.Submenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforge.client.gui.ModListScreen;

import java.nio.file.Path;

public class NeoforgePlatform implements Platform {
    @Override
    public void addModsOption(Submenu appMenu) {
        appMenu.option(Component.translatable("fml.menu.mods.title"), "", () -> Minecraft.getInstance().setScreen(new ModListScreen(Minecraft.getInstance().screen)));
    }

    @Override
    public String modLoaderInfo() {
        return "NeoForge " + FMLLoader.versionInfo().neoForgeVersion() + "\n" +
                "NeoForm " + FMLLoader.versionInfo().neoFormVersion();
    }

    @Override
    public Path getConfigPath() {
        return FMLLoader.getGamePath().resolve("config/mac-integration.properties");
    }

    @Override
    public boolean isModLoaded(String mod) {
        if (ModList.get() == null) {
            // too early (mixin), go the hard way
            return FMLLoader.getLoadingModList().getModFiles()
                    .stream()
                    .flatMap(info -> info.getMods().stream())
                    .anyMatch(info -> mod.equals(info.getModId()));
        }
        return ModList.get().isLoaded(mod);
    }
}
