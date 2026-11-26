/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.notch;

import ca.weblite.objc.Proxy;
import com.sun.jna.Pointer;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFWNativeCocoa;

public final class BuiltInFullscreen implements LegacyFullscreen {
    private static final int FULLSCREEN_MASK = 1 << 14;

    @Override
    public boolean useLegacyMethod() {
        return false;
    }

    @Override
    public void toggleFullscreen() {
        Proxy window = new Proxy(new Pointer(GLFWNativeCocoa.glfwGetCocoaWindow(Minecraft.getInstance().getWindow().getWindow())));
        window.send("toggleFullScreen:", Pointer.NULL);
    }

    @Override
    public boolean shutdown() {
        Proxy window = new Proxy(new Pointer(GLFWNativeCocoa.glfwGetCocoaWindow(Minecraft.getInstance().getWindow().getWindow())));
        int styleMask = window.sendInt("styleMask");
        if ((styleMask & FULLSCREEN_MASK) != 0) {
            window.send("toggleFullScreen:", Pointer.NULL);
            return true;
        }
        return false;
    }
}
