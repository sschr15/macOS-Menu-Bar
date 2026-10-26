/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.mixin.compat.emi;

import com.sschr15.mods.menubar.EmiMenu;
import dev.emi.emi.runtime.EmiSidebars;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EmiSidebars.class, remap = false)
public abstract class EmiHistoryReader {
    @Inject(method = "lookup", at = @At(value = "INVOKE", target = "Ldev/emi/emi/runtime/EmiPersistentData;save()V"))
    private static void updateHistory(CallbackInfo ci) {
        EmiMenu.instance.updateHistory();
    }
}
