/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.mixin;

import ca.weblite.objc.Client;
import com.mojang.blaze3d.platform.GLX;
import com.sschr15.mods.menubar.objc.MenuBar;
import com.sschr15.mods.menubar.objc.Submenu;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.LongSupplier;

@Mixin(GLX.class)
public abstract class SpecifyGlfwHintMixin {
    @Inject(method = "_initGlfw", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwInit()Z"))
    private static void menubar$disableMenubarHint(CallbackInfoReturnable<LongSupplier> cir) {
        GLFW.glfwInitHint(GLFW.GLFW_COCOA_MENUBAR, GLFW.GLFW_FALSE);
    }

    @Inject(method = "_initGlfw", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwInit()Z", shift = At.Shift.AFTER))
    private static void menubar$markAsStandardApp(CallbackInfoReturnable<LongSupplier> cir) {
        // Because we're no longer making GLFW set up the menu bar, it also won't make the dock icon.
        // Fix this by manually setting the activation policy to NSApplicationActivationPolicyRegular (or ordinal 0)
        Client.getInstance().sendProxy("NSApplication", "sharedApplication").send("setActivationPolicy:", 0);

        // Additionally, make a placeholder menu bar so the real one can be made after initialization is complete
        MenuBar temp = new MenuBar();
        Submenu app = temp.menu(Component.literal(""));
        app.option(Component.literal("Quit"), "terminate:", "q");
    }
}
