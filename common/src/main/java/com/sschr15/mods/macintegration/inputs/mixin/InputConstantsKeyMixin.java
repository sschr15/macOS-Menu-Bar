/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.inputs.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.throwables.MixinException;

@Mixin(InputConstants.Key.class)
public interface InputConstantsKeyMixin {
    @Invoker("<init>")
    static InputConstants.Key macintegration$createKey(String name, InputConstants.Type type, int value) {
        throw new MixinException("Mixin failed to apply");
    }
}
