/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.menubar;

import com.sschr15.mods.macintegration.MacIntegration;

public class StandardMenus {
    public static void init() {
        MacosMenuBar.registerMenubarSetup(bar -> {
            if (MacIntegration.platform.isModLoaded("iris")) {
                new ShadersMenu().addMenu(bar);
            }
            if (MacIntegration.platform.isModLoaded("emi")) {
                new EmiMenu().addMenu(bar);
            }

            new DebugMenu().addMenu(bar);
        });
    }
}
