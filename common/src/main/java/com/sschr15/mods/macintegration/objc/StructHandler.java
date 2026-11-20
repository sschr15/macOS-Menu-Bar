/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.objc;

import ca.weblite.objc.Proxy;
import ca.weblite.objc.RuntimeUtils;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class StructHandler implements ObjCUtils {
    public static <T extends Structure> T performSelector(Class<T> returnType, Proxy target, String selector, Object... args) {
        Pointer sel = RuntimeUtils.sel(selector);
        Proxy signature = target.sendProxy("methodSignatureForSelector:", sel);

        Proxy invocation = C.sendProxy("NSInvocation", "invocationWithMethodSignature:", signature);
        invocation.send("setSelector:", sel);
        invocation.send("setTarget:", target);
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Pointer ptr;
            if (arg instanceof Structure struct) {
                struct.write();
                ptr = struct.getPointer();
            } else {
                ptr = RuntimeUtils.getAsReference(arg, signature.sendString("getArgumentTypeAtIndex:", i + 2));
            }
            invocation.send("setArgument:atIndex:", ptr, i + 2);
        }

        invocation.send("invoke");

        T result = Structure.newInstance(returnType);
        invocation.send("getReturnValue:", result.getPointer());
        result.read();

        return result;
    }

    public static void performSelector(Proxy target, String selector, Object... args) {
        Pointer sel = RuntimeUtils.sel(selector);
        Proxy signature = target.sendProxy("methodSignatureForSelector:", sel);

        Proxy invocation = C.sendProxy("NSInvocation", "invocationWithMethodSignature:", signature);
        invocation.send("setSelector:", sel);
        invocation.send("setTarget:", target);
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Pointer ptr;
            if (arg instanceof Structure struct) {
                struct.write();
                ptr = struct.getPointer();
            } else {
                ptr = RuntimeUtils.getAsReference(arg, signature.sendString("getArgumentTypeAtIndex:", i + 2));
            }
            invocation.send("setArgument:atIndex:", ptr, i + 2);
        }

        invocation.send("invoke");
    }
}
