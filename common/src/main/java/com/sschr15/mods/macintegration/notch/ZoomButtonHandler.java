/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.notch;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.Runtime;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sschr15.mods.macintegration.objc.Block;
import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoomButtonHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoomButtonHandler.class);

    interface ReplacementCallback extends Callback {
        void invoke(Pointer self, Pointer sel, Pointer arg);
    }

    interface EventCallback extends Callback {
        Pointer callback(Pointer block, Pointer event);
    }

    private static final int ZOOM_BUTTON = 2;
    private static ReplacementCallback replacementCallback;
    private static EventCallback eventCallback;

    public static void init(long windowHandle) {
        RenderSystem.assertOnRenderThreadOrInit();

        Proxy window = new Proxy(new Pointer(GLFWNativeCocoa.glfwGetCocoaWindow(windowHandle)));
        Proxy zoomButton = window.sendProxy("standardWindowButton:", ZOOM_BUTTON);

        Proxy target = zoomButton.sendProxy("target");
        Pointer actionSelector = new Pointer((Long) zoomButton.sendRaw("action"));

        Runtime rt = Runtime.INSTANCE;
        Pointer targetClass = new Pointer((Long) target.sendRaw("class"));
        Pointer method = rt.class_getInstanceMethod(targetClass, actionSelector);
        Function existingFunction = Function.getFunction(rt.method_getImplementation(method));
        Pointer newFunction = CallbackReference.getFunctionPointer(replacementCallback = (self, sel, arg) -> {
            boolean optionPressed = (((Long) Client.getInstance().send("NSEvent", "modifierFlags")).intValue() & (1 << 19)) != 0;
            if (!optionPressed) {
                LegacyFullscreen impl = LegacyFullscreen.IMPLEMENTATION.get().implementation;
                if (impl.useLegacyMethod()) {
                    impl.toggleFullscreen();
                    return;
                }
            }

            existingFunction.invoke(new Object[]{self, sel, arg});
        });
        rt.method_setImplementation(method, newFunction);
    }
}
