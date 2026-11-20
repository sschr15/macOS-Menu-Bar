/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.macintegration.menubar;

import ca.weblite.objc.NSObject;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.annotations.Msg;
import com.sschr15.mods.macintegration.MacIntegration;
import com.sschr15.mods.macintegration.menubar.objc.Option;
import net.minecraft.SharedConstants;

public class AboutPanelHandler extends NSObject {
    private final Proxy optionsDictionary;

    public AboutPanelHandler() {
        super("NSObject");

        Proxy dictionary = getClient().sendProxy("NSMutableDictionary", "dictionaryWithCapacity:", 6);
        dictionary.send("setObject:forKey:", "Minecraft", "ApplicationName");
        dictionary.send("setObject:forKey:", getClient().sendProxy("NSApplication", "sharedApplication").send("applicationIconImage"), "ApplicationIcon");
        dictionary.send("setObject:forKey:", SharedConstants.getCurrentVersion().getName(), "ApplicationVersion");
        dictionary.send("setObject:forKey:", "Java " + System.getProperty("java.version"), "Version");
        dictionary.send("setObject:forKey:", buildCredits(), "Credits");
        dictionary.send("setObject:forKey:", "Copyright Mojang AB. Do not distribute!", "Copyright");
        optionsDictionary = getClient().sendProxy("NSDictionary", "dictionaryWithDictionary:", dictionary);
//        optionsDictionary = dictionary;
    }

    private Object buildCredits() {
//        String text = """
//                %s %s
//                %s
//                """.formatted(System.getProperty("java.vm.vendor"), System.getProperty("java.vm.version"), MacosMenuBar.platform.modLoaderInfo());
        String text = System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.version") + "\n" +
                      MacIntegration.platform.modLoaderInfo();

        return getClient().sendProxy("NSAttributedString", "alloc").send("initWithString:", text);
    }

    @Msg(selector = "loadAboutPanel:", signature = Option.ACTION_SIGNATURE)
    public void loadAboutPanel(Proxy ignored) {
        getClient().sendProxy("NSApplication", "sharedApplication")
                .send("orderFrontStandardAboutPanelWithOptions:", optionsDictionary);
    }
}
