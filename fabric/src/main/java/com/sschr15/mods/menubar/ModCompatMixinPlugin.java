/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

public class ModCompatMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("com.sschr15.mods.menubar.mixin.compat.")) {
            String mod = mixinClassName.split("\\.")[5];
            return FabricLoader.getInstance().isModLoaded(mod);
        }

        return true;
    }
}
