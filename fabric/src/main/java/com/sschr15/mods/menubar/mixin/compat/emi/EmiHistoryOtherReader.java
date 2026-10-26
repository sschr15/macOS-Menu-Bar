/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.mixin.compat.emi;

import com.sschr15.mods.menubar.EmiMenu;
import dev.emi.emi.runtime.EmiPersistentData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EmiPersistentData.class, remap = false)
public abstract class EmiHistoryOtherReader {
    @Inject(method = "load", at = @At(value = "INVOKE", target = "Ldev/emi/emi/runtime/EmiSidebars;load(Lcom/google/gson/JsonObject;)V", shift = At.Shift.AFTER))
    private static void updateHistoryAfterLoad(CallbackInfo ci) {
        EmiMenu.instance.updateHistory();
    }
}
