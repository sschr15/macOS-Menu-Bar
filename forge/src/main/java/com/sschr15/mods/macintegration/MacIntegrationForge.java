/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration;

import com.sschr15.mods.macintegration.menubar.DebugMenu;
import com.sschr15.mods.macintegration.menubar.MacosMenuBar;
import com.sschr15.mods.macintegration.menubar.objc.Submenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;

@Mod("mac_integration")
public class MacIntegrationForge implements Platform {
    public MacIntegrationForge() {
        MacIntegration.platform = this;
        MacIntegration.loadOptions();

        MacosMenuBar.registerMenubarSetup(bar -> {
            new DebugMenu().addMenu(bar);
        });
    }

    @Override
    public void addModsOption(Submenu appMenu) {
        appMenu.option(Component.translatable("fml.menu.mods.title"), "", () -> Minecraft.getInstance().setScreen(new ModListScreen(Minecraft.getInstance().screen)));
    }

    @Override
    public String modLoaderInfo() {
        return "MinecraftForge " + FMLLoader.versionInfo().forgeVersion();
    }

    @Override
    public Path getConfigPath() {
        return FMLLoader.getGamePath().resolve("config/mac-integration.properties");
    }
}
