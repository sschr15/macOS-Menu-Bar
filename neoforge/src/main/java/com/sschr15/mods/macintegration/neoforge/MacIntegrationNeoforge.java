/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.neoforge;

import com.sschr15.mods.macintegration.MacIntegration;
import com.sschr15.mods.macintegration.Platform;
import com.sschr15.mods.macintegration.gui.ModOptions;
import com.sschr15.mods.macintegration.inputs.MacosInputs;
import com.sschr15.mods.macintegration.menubar.DebugMenu;
import com.sschr15.mods.macintegration.menubar.MacosMenuBar;
import com.sschr15.mods.macintegration.menubar.objc.Submenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.ModListScreen;

import java.nio.file.Path;

@Mod("mac_integration")
public class MacIntegrationNeoforge implements Platform {
    public MacIntegrationNeoforge(ModContainer mod) {
        MacIntegration.platform = this;
        MacIntegration.loadConfig();

        MacosMenuBar.registerMenubarSetup(bar -> {
            new DebugMenu().addMenu(bar);
        });

        MacosInputs.init();

        mod.registerExtensionPoint(IConfigScreenFactory.class, (ignored, screen) -> new ModOptions(screen, Minecraft.getInstance().options));
    }

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
}
