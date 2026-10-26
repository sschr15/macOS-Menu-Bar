/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar;

import com.sschr15.mods.menubar.objc.Submenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("macos_menu_bar")
public class MacosMenuBarForge implements Platform {
    public MacosMenuBarForge() {
        MacosMenuBar.platform = this;

        MacosMenuBar.registerMenubarSetup(bar -> {
            new DebugMenu().addMenu(bar);
        });
    }

    @Override
    public void addModsOption(Submenu appMenu) {
        appMenu.option(Component.translatable("fml.menu.mods.title"), "", () -> Minecraft.getInstance().setScreen(new ModListScreen(Minecraft.getInstance().screen)));
    }
}
