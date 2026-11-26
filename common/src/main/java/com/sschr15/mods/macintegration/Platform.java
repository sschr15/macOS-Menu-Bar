/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration;

import com.sschr15.mods.macintegration.menubar.objc.Submenu;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@ApiStatus.Internal
public interface Platform {
    Logger LOGGER = LoggerFactory.getLogger("mac-integration");

    void addModsOption(Submenu appMenu);

    String modLoaderInfo();

    Path getConfigPath();

    boolean isModLoaded(String mod);
}
