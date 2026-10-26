/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.mixin;

import com.sschr15.mods.menubar.DebugMenu;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = {"handleLogin", "handleRespawn"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/player/LocalPlayer;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void updateDebugMenuFromPlayerLogin(CallbackInfo ci) {
        DebugMenu.refresh();
    }
}
