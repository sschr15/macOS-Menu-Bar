/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.objc;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;

public final class Option extends MenuItem {
    public static final String ACTION_SIGNATURE = "v@:@";

    Option(Client client, Proxy item) {
        super(client, item);
    }

    public void setModifiers(@Modifiers int modifiers){
        item.send("setKeyEquivalentModifierMask:", modifiers);
    }

    public void setTarget(Object target){
        item.send("setTarget:", target);
    }
}
