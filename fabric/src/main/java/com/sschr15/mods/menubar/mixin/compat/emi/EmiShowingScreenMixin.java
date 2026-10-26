/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.mixin.compat.emi;

import com.sschr15.mods.menubar.EmiMenu;
import com.sschr15.mods.menubar.MacosMenuBar;
import dev.emi.emi.screen.RecipeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class EmiShowingScreenMixin {
    @Inject(method = "setScreen", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;"))
    private void checkScreenType(Screen guiScreen, CallbackInfo ci) {
        if (EmiMenu.instance == null) return; // before menu init
        if (guiScreen instanceof AbstractContainerScreen<?> || guiScreen instanceof RecipeScreen) {
            EmiMenu.instance.show();
        } else {
            EmiMenu.instance.hide();
        }
        MacosMenuBar.refresh();
    }
}
