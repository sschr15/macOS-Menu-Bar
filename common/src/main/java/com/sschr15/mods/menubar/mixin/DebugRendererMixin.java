/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.mixin;

import com.sschr15.mods.menubar.DebugMenu;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugRenderer.class)
public abstract class DebugRendererMixin {
    @Inject(method = "switchRenderChunkborder", at = @At("RETURN"))
    private void updateChunkBorders(CallbackInfoReturnable<Boolean> cir) {
        DebugMenu.updateChunkBorders(cir.getReturnValueZ());
    }
}
