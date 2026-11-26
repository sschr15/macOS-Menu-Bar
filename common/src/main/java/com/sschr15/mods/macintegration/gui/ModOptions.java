/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.gui;

import com.sschr15.mods.macintegration.MacIntegration;
import com.sschr15.mods.macintegration.inputs.MacosInputs;
import com.sschr15.mods.macintegration.notch.LegacyFullscreen;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public class ModOptions extends OptionsSubScreen {
    private LegacyFullscreen.FullscreenImplementation current;

    public ModOptions(Screen parent, Options options) {
        super(parent, options, Component.translatable("macintegration.title"));
    }

    @Override
    protected void init() {
        int x = width / 2 - 150;
        int y = height / 6 - 12;
        addRenderableWidget(MacosInputs.CONTROL_CLICK_IS_RIGHT_CLICK.createButton(options, x, y, 300, ignored -> MacIntegration.saveConfig()));
        y += 24;
        addRenderableWidget(LegacyFullscreen.IMPLEMENTATION.createButton(options, x, y, 300, this::save));

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, ignored -> minecraft.setScreen(lastScreen)).bounds(x, height - 27, 300, 20).build());
        super.init();
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(font, title, width / 2, 15, 0xffffff);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
