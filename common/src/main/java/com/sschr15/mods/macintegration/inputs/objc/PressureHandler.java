/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.inputs.objc;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;
import com.mojang.blaze3d.platform.InputConstants;
import com.sschr15.mods.macintegration.Platform;
import com.sschr15.mods.macintegration.inputs.mixin.InputConstantsKeyMixin;
import com.sschr15.mods.macintegration.inputs.mixin.KeyBindsScreenAccessor;
import com.sschr15.mods.macintegration.objc.Block;
import com.sun.jna.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;

public class PressureHandler {
    interface EventCallback extends Callback {
        Pointer handleEvent(Pointer block, Pointer event);
    }

    public static final InputConstants.Key FORCE_TOUCH = InputConstantsKeyMixin.macintegration$createKey("key.mouse.force", InputConstants.Type.MOUSE, -1);
    public static final InputConstants.Key LEFT_CLICK = InputConstants.Type.MOUSE.getOrCreate(0);
    private static final long NSEventMaskPressure = 1L << 34;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private static Object blockRef, handlerRef;

    public static void init() {
        EventCallback handler = new EventCallback() {
            int previousStage;
            @Override
            public Pointer handleEvent(Pointer ignored, Pointer pointer) {
                Proxy event = Proxy.load(pointer);
                int stage = event.sendInt("stage");
                boolean forceTouched = stage == 2;
                KeyMapping.set(FORCE_TOUCH, forceTouched);
                if (forceTouched) {
                    if (previousStage != 2) {
                        (FabricLoader.getInstance().isDevelopmentEnvironment() ? Platform.LOGGER.atInfo() : Platform.LOGGER.atDebug()).log("Force touched!");
                        KeyMapping.click(FORCE_TOUCH);
                        handleKeyBindingScreen();
                    }
                    KeyMapping.set(LEFT_CLICK, false);
                } else if (stage != 1 && previousStage == 1) {
                    // if we're force touching or releasing the trackpad, let go of the click
                    KeyMapping.set(LEFT_CLICK, false);
                }
                previousStage = stage;

                return pointer;
            }
        };

        Structure block = Block.createBlockForCallback(handler);
        Client.getInstance().send("NSEvent", "addLocalMonitorForEventsMatchingMask:handler:", NSEventMaskPressure, block.getPointer());

        // Prevent garbage collection
        handlerRef = handler;
        blockRef = block;
    }

    private static void handleKeyBindingScreen() {
        if (!(Minecraft.getInstance().screen instanceof KeyBindsScreen screen)) return;
        if (screen.selectedKey != null) {
            Minecraft.getInstance().options.setKey(screen.selectedKey, FORCE_TOUCH);
            screen.selectedKey = null;
            ((KeyBindsScreenAccessor) screen).macintegration$getKeyBindsList().resetMappingAndUpdateButtons();
        }
    }
}
