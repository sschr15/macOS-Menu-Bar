/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.inputs;

import com.sschr15.mods.macintegration.inputs.objc.PressureHandler;
import net.minecraft.client.OptionInstance;

public class MacosInputs {
    public static final OptionInstance<Boolean> CONTROL_CLICK_IS_RIGHT_CLICK = OptionInstance.createBoolean(
            "option.macintegration.control_click_is_right_click", true
    );

    public static void init() {
        PressureHandler.init();
    }
}
