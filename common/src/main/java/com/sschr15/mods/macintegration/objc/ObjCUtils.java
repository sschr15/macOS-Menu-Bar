/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.objc;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;

public interface ObjCUtils {
    Client C = Client.getInstance();
    Proxy NSApp = C.sendProxy("NSApplication", "sharedApplication");
}
