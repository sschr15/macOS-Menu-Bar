/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.notch;

import com.demonwav.mcdev.annotations.Translatable;
import com.llamalad7.mixinextras.lib.semver.Version;
import com.mojang.serialization.Codec;
import com.sschr15.mods.macintegration.MacIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public sealed interface LegacyFullscreen permits LegacyFullscreenGlfw, LegacyFullscreenObjC, BuiltInFullscreen {
    Version MINIMUM_MACOS = Version.parse("12", false);
    OptionInstance<FullscreenImplementation> IMPLEMENTATION = new OptionInstance<>(
            "option.macintegration.use_legacy_fullscreen",
            option -> Tooltip.create(Component.translatable(option.key + ".tooltip")),
            OptionInstance.forOptionEnum(),
            new OptionInstance.Enum<>(Arrays.asList(FullscreenImplementation.values()), Codec.INT.xmap(FullscreenImplementation.BY_ID::apply, FullscreenImplementation::getId)),
            FullscreenImplementation.NONE,
            ignored -> MacIntegration.saveConfig()
    );

    enum FullscreenImplementation implements OptionEnum {
        NONE(0, "disabled", new BuiltInFullscreen()),
        AUTO_OBJC(1, "auto", new LegacyFullscreenObjC(false)),
        OBJC(2, "objc", new LegacyFullscreenObjC(true)),
        GLFW(3, "glfw", new LegacyFullscreenGlfw()),

        ;

        public final LegacyFullscreen implementation;
        private final int id;
        private final String key;
        static final IntFunction<FullscreenImplementation> BY_ID = ByIdMap.continuous(FullscreenImplementation::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        FullscreenImplementation(int id, @Translatable(prefix = "option.macintegration.use_legacy_fullscreen.") String key, LegacyFullscreen implementation) {
            this.id = id;
            this.key = "option.macintegration.use_legacy_fullscreen." + key;
            this.implementation = implementation;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getKey() {
            return key;
        }
    }

    boolean useLegacyMethod();
    void toggleFullscreen();
    boolean shutdown();
}
