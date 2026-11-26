/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.gui;

import com.sschr15.mods.macintegration.MacIntegration;
import com.sschr15.mods.macintegration.inputs.MacosInputs;
import com.sschr15.mods.macintegration.notch.LegacyFullscreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModOptions extends OptionsSubScreen {
    private LegacyFullscreen.FullscreenImplementation current;

    public ModOptions(Screen parent, Options options) {
        super(parent, options, Component.translatable("macintegration.title"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void addOptions() {
        list.addBig(MacosInputs.CONTROL_CLICK_IS_RIGHT_CLICK);
        list.addBig(LegacyFullscreen.IMPLEMENTATION);
    }

    @Override
    public void onClose() {
        MacIntegration.saveConfig();
        super.onClose();
    }

    void save(LegacyFullscreen.FullscreenImplementation implementation) {
        boolean isFullscreen = current.implementation.shutdown();
        if (isFullscreen) {
            implementation.implementation.toggleFullscreen();
        }
        current = implementation;
    }
}
