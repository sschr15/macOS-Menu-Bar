/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration;

import com.sschr15.mods.macintegration.inputs.MacosInputs;
import com.sschr15.mods.macintegration.menubar.DebugMenu;
import com.sschr15.mods.macintegration.menubar.EmiMenu;
import com.sschr15.mods.macintegration.menubar.MacosMenuBar;
import com.sschr15.mods.macintegration.menubar.ShadersMenu;
import com.sschr15.mods.macintegration.menubar.objc.Submenu;
import com.terraformersmc.modmenu.gui.ModsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

public class MacIntegrationFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MacosInputs.init();
        MacIntegration.loadConfig();
    }
}
