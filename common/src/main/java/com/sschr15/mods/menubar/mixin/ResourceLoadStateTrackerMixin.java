/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.mixin;

import com.sschr15.mods.menubar.MacosMenuBar;
import net.minecraft.client.ResourceLoadStateTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourceLoadStateTracker.class)
public abstract class ResourceLoadStateTrackerMixin {
    @Inject(method = "finishReload", at = @At("RETURN"))
    private void onResourcesLoadFinish(CallbackInfo ci) {
        MacosMenuBar.setup();
    }
}
