/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar;

import com.sschr15.mods.menubar.objc.Submenu;
import com.terraformersmc.modmenu.gui.ModsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class MacosMenuBarClient implements ClientModInitializer, Platform {
    public MacosMenuBarClient() {
        MacosMenuBar.platform = this;
    }

    @Override
    public void onInitializeClient() {
        MacosMenuBar.registerMenubarSetup(bar -> {
            if (FabricLoader.getInstance().isModLoaded("iris")) {
                new ShadersMenu().addMenu(bar);
            }
            if (FabricLoader.getInstance().isModLoaded("emi")) {
                new EmiMenu().addMenu(bar);
            }

            new DebugMenu().addMenu(bar);
        });
    }

    @Override
    public void addModsOption(Submenu appMenu) {
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            ModMenuOption.init(appMenu);
        }
    }
}

// separated to avoid potential class loading issues - optional dependencies is hard tm
class ModMenuOption {
    static void init(Submenu appMenu) {
        appMenu.option(Component.translatable("modmenu.title"), "", () -> Minecraft.getInstance().setScreen(new ModsScreen(Minecraft.getInstance().screen)));
    }
}
