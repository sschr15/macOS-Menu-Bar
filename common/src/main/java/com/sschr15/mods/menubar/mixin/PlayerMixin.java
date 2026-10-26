/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.mixin;

import com.sschr15.mods.menubar.DebugMenu;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "setReducedDebugInfo", at = @At("RETURN"))
    private void refreshDebugMenu(CallbackInfo ci) {
        DebugMenu.refresh();
    }

    @Inject(method = "handleEntityEvent", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;reducedDebugInfo:Z", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void refreshDebugMenuButFromEntityEvents(CallbackInfo ci) {
        DebugMenu.refresh();
    }
}
