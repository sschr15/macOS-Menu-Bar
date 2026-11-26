/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.notch;

import ca.weblite.objc.Proxy;
import com.llamalad7.mixinextras.lib.semver.Version;
import com.sschr15.mods.macintegration.objc.ObjCUtils;
import com.sschr15.mods.macintegration.objc.StructHandler;
import com.sun.jna.Pointer;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFWNativeCocoa;
import oshi.jna.platform.mac.CoreGraphics;

// looks nice tm
public final class LegacyFullscreenObjC implements LegacyFullscreen, ObjCUtils {
    private CoreGraphics.CGRect oldFrame;
    private int oldMask;
    private final boolean force;

    public LegacyFullscreenObjC(boolean force) {
        this.force = force;
    }

    private static Proxy window() {
        long handle = Minecraft.getInstance().getWindow().getWindow();
        Pointer nsWindowPtr = new Pointer(GLFWNativeCocoa.glfwGetCocoaWindow(handle));
        return new Proxy(nsWindowPtr);
    }

    @Override
    public boolean useLegacyMethod() {
        if (force) return true;
        String arch = System.getProperty("os.arch");
        Version currentMacos = Version.parse(System.getProperty("os.version"));
        if (!"aarch64".equals(arch) || !currentMacos.isHigherThan(MINIMUM_MACOS)) {
            return false;
        }

        Proxy window = window();
        Proxy screen = window.sendProxy("screen");
        NSEdgeInsets insets = StructHandler.performSelector(NSEdgeInsets.class, screen, "safeAreaInsets");
        return insets.top != 0.0;
    }

    @Override
    public void toggleFullscreen() {
        if (oldFrame == null) {
            enterFullscreen();
        } else {
            exitFullscreen();
        }
    }

    @Override
    public boolean shutdown() {
        if (oldFrame != null) {
            exitFullscreen();
            return true;
        }

        return false;
    }

    private void enterFullscreen() {
        Proxy window = NSApp.sendProxy("mainWindow");

        oldMask = window.sendInt("styleMask");
        window.send("setStyleMask:", 2 << 14); // borderless, fullscreen

        int options = NSApp.sendInt("presentationOptions");
        options |= ApplicationPresentationOptions.AUTO_HIDE_DOCK | ApplicationPresentationOptions.AUTO_HIDE_MENU_BAR;
        NSApp.send("setPresentationOptions:", options);

        oldFrame = StructHandler.performSelector(CoreGraphics.CGRect.class, window, "frame");

        Proxy screen = window.sendProxy("screen");
        CoreGraphics.CGRect screenFrame = StructHandler.performSelector(CoreGraphics.CGRect.class, screen, "frame");
        StructHandler.performSelector(window, "setFrame:display:animate:", screenFrame, true, true);

        window.send("makeKeyAndOrderFront:", Pointer.NULL);
    }

    private void exitFullscreen() {
        Proxy window = NSApp.sendProxy("mainWindow");

        int options = NSApp.sendInt("presentationOptions");
        options &= ~(ApplicationPresentationOptions.AUTO_HIDE_DOCK | ApplicationPresentationOptions.AUTO_HIDE_MENU_BAR);
        NSApp.send("setPresentationOptions:", options);

        Proxy screen = window.sendProxy("screen");
        StructHandler.performSelector(window, "setFrame:display:animate:", oldFrame, true, true);

        window.send("setStyleMask:", oldMask);
        window.send("makeKeyAndOrderFront:", Pointer.NULL);
        oldFrame = null;
    }
}
