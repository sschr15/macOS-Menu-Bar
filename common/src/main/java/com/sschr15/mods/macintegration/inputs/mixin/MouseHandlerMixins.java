/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.inputs.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.sschr15.mods.macintegration.inputs.MacosInputs;
import net.minecraft.client.MouseHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixins {
    @ModifyExpressionValue(method = "onPress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ON_OSX:Z", opcode = Opcodes.GETSTATIC))
    private boolean disableCtrlClickIfOpted(boolean onMacOs) {
        return onMacOs && MacosInputs.CONTROL_CLICK_IS_RIGHT_CLICK.get();
    }
}
