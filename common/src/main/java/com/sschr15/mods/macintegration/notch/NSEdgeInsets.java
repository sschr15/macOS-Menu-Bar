/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.notch;

import com.sun.jna.Structure;

@Structure.FieldOrder({"top", "left", "bottom", "right"})
public class NSEdgeInsets extends Structure {
    public double top, left, bottom, right;
}
