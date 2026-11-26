/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration;

import com.sschr15.mods.macintegration.inputs.MacosInputs;
import com.sschr15.mods.macintegration.notch.LegacyFullscreen;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.ServiceLoader;

@ApiStatus.Internal
public class MacIntegration {
    public static final Logger LOGGER = LoggerFactory.getLogger(MacIntegration.class);
    public static final Platform platform = loadPlatform();
    
    private static Platform loadPlatform() {
        return ServiceLoader.load(Platform.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(p -> p.getClass().getPackageName().startsWith("com.sschr15.mods.macintegration"))
                .findAny()
                .orElseThrow();
    }

    public static void loadConfig() {
        try {
            Path config = MacIntegration.platform.getConfigPath();
            if (!Files.isRegularFile(config)) return;
            Properties properties = new Properties();
            try (var is = Files.newInputStream(config)) {
                properties.load(is);
            }

            if (properties.containsKey("ctrl_click")) {
                MacosInputs.CONTROL_CLICK_IS_RIGHT_CLICK.set(Boolean.parseBoolean(properties.getProperty("ctrl_click")));
            }
            if (properties.containsKey("fullscreen_mode")) {
                LegacyFullscreen.IMPLEMENTATION.set(LegacyFullscreen.FullscreenImplementation.valueOf(properties.getProperty("fullscreen_mode").toUpperCase(Locale.ROOT)));
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to load saved config", t);
        }
    }

    public static void saveConfig() {
        Properties properties = new Properties();
        properties.setProperty("ctrl_click", MacosInputs.CONTROL_CLICK_IS_RIGHT_CLICK.get().toString());
        properties.setProperty("fullscreen_mode", LegacyFullscreen.IMPLEMENTATION.get().name().toLowerCase(Locale.ROOT));

        Path config = MacIntegration.platform.getConfigPath();
        try (var os = Files.newOutputStream(config)) {
            properties.store(os, null);
        } catch (IOException e) {
            MacIntegration.LOGGER.error("Failed to write config", e);
        }
    }
}
