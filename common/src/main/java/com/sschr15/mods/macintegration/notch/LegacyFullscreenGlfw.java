/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.notch;

import ca.weblite.objc.Proxy;
import com.sschr15.mods.macintegration.objc.ObjCUtils;
import com.sschr15.mods.macintegration.objc.StructHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeCocoa;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.util.JavaVersion;
import oshi.jna.platform.mac.CoreGraphics;

import java.nio.IntBuffer;

// likely to function better if other mods do weird stuff
public final class LegacyFullscreenGlfw implements ObjCUtils, LegacyFullscreen {
    private long macbookMonitor = -1;
    private Proxy macbookMonitorProxy;
    private WindowData savedData;

    private long getWindowHandle() {
        return Minecraft.getInstance().getWindow().getWindow();
    }

    private long identifyMacbookScreen() {
        if (macbookMonitor != -1) return macbookMonitor;

        // step 1: find the NSScreen corresponding to the macbook screen
        Proxy macbookScreen = null;

        Proxy screens = C.sendProxy("NSScreen", "screens");
        int screenCount = screens.sendInt("count");
        for (int i = 0; i < screenCount; i++) {
            Proxy screen = screens.sendProxy("objectAtIndex:", i);

            if (checkIfMacbookScreen(screen)) {
                macbookScreen = screen;
                break;
            }
        }

        if (macbookScreen == null) return -1;

        // step 2: associate it with glfw monitor
        PointerBuffer monitors = GLFW.glfwGetMonitors();
        for (int i = 0; i < monitors.capacity(); i++) {
            long monitor = monitors.get(i);
            int displayId = GLFWNativeCocoa.glfwGetCocoaMonitor(monitor);
            if (displayId == macbookScreen.sendInt("CGDirectDisplayID")) {
                macbookMonitorProxy = macbookScreen;
                return macbookMonitor = monitor;
            }
        }

        return -1;
    }

    private boolean checkIfMacbookScreen(Proxy screen) {
        if (JavaVersion.current() >= JavaVersion.JAVA_22) {
            CoreGraphics.CGRect auxTopLeftAreaPointer = StructHandler.performSelector(CoreGraphics.CGRect.class, screen, "auxiliaryTopLeftArea");
            return auxTopLeftAreaPointer.size.width != 0;
        }

        // technology didn't want us to have a chance (and by technology i mean aarch64 calling convention)
        return false;
    }

    public boolean useLegacyMethod() {
        return true;
    }

    public void toggleFullscreen() {
        if (savedData == null) {
            enterFullscreen();
        } else {
            exitFullscreen();
        }
    }

    @Override
    public boolean shutdown() {
        if (savedData != null) {
            exitFullscreen();
            return true;
        }
        return false;
    }

    private long findMonitor() {
        if (identifyMacbookScreen() != -1) return macbookMonitor;

        return GLFW.glfwGetWindowMonitor(getWindowHandle());
    }

    private void enterFullscreen() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long window = getWindowHandle();
            IntBuffer x = stack.mallocInt(1), y = stack.mallocInt(1),
                    w = stack.mallocInt(1), h = stack.mallocInt(1);

            GLFW.glfwGetWindowPos(window, x, y);
            GLFW.glfwGetWindowSize(window, w, h);

            savedData = new WindowData(x.get(), y.get(), w.get(), h.get(), NSApp.sendInt("presentationOptions"));

            int newOptions = savedData.presentationOptions() | ApplicationPresentationOptions.AUTO_HIDE_DOCK | ApplicationPresentationOptions.AUTO_HIDE_MENU_BAR;
            NSApp.send("setPresentationOptions:", newOptions);

            long monitor = findMonitor();
            x.rewind();
            y.rewind();
            GLFW.glfwGetMonitorPos(monitor, x, y);
            GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);

            GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
            GLFW.glfwSetWindowSize(window, mode.width(), mode.height());
            GLFW.glfwSetWindowPos(window, x.get(), y.get());

            GLFW.glfwFocusWindow(window);
        }
    }

    private void exitFullscreen() {
        NSApp.sendInt("setPresentationOptions:", savedData.presentationOptions());

        long window = getWindowHandle();
        GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
        GLFW.glfwSetWindowPos(window, savedData.x(), savedData.y());
        GLFW.glfwSetWindowSize(window, savedData.w(), savedData.h());
        GLFW.glfwFocusWindow(window);

        savedData = null;
    }

    private record WindowData(int x, int y, int w, int h, int presentationOptions) {}
}
