/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.inputs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.sschr15.mods.macintegration.inputs.MacosInputs;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsScreen.class)
public abstract class ControlsScreenMixin extends OptionsSubScreen {
    ControlsScreenMixin() {
        super(null, null, null);
    }

//    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/controls/ControlsScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 5))
//    private void addControlClickButton(CallbackInfo ci, @Local(ordinal = 0) int x, @Local(ordinal = 2) LocalIntRef y) {
//        y.set(y.get() + 24);
//        addRenderableWidget(MacosInputs.CONTROL_CLICK_IS_RIGHT_CLICK.createButton(options, x, y.get(), 150));
//    }
    @Inject(method = "addOptions", at = @At("RETURN"))
    private void addControlClickButton(CallbackInfo ci) {
        list.addBig(MacosInputs.CONTROL_CLICK_IS_RIGHT_CLICK);
    }
}
