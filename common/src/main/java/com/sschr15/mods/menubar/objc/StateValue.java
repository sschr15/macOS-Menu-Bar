/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.objc;

import org.intellij.lang.annotations.MagicConstant;

@MagicConstant(valuesFromClass = StateValue.class)
public @interface StateValue {
    int OFF = 0;
    int ON = 1;
    int MIXED = -1;
}
