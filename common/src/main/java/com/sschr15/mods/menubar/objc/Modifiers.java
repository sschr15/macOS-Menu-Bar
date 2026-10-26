/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.objc;

import org.intellij.lang.annotations.MagicConstant;

@MagicConstant(flagsFromClass = Modifiers.class)
public @interface Modifiers {
    int CAPS_LOCK = 1 << 16;
    int SHIFT = 1 << 17;
    int CONTROL = 1 << 18;
    int OPTION = 1 << 19;
    int COMMAND = 1 << 20;
    int NUMPAD = 1 << 21;
    int HELP = 1 << 22;
    int FUNCTION = 1 << 23;
}
