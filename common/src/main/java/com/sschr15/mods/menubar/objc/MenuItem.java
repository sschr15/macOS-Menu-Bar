/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.objc;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.annotations.Msg;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public sealed class MenuItem permits Option, Submenu {
    static class ComponentUpdater extends NSObject {
        final Component component;

        ComponentUpdater(Component component) {
            super("NSObject");
            this.component = component;
        }

        @Msg(selector = "updateForMenuItem:", signature = "v@:@")
        public void update(Proxy menuItem) {
            menuItem.send("setTitle:", component.getString());
            //TODO: attributedTitle
        }
    }

    protected final Client client;
    protected final Proxy item;

    MenuItem(Client client, Proxy item) {
        this.client = client;
        this.item = item;
    }

    public void show() {
        item.send("setHidden:", false);
    }

    public void hide() {
        item.send("setHidden:", true);
    }

    public int getTag() {
        return item.sendInt("tag");
    }

    public void setTag(int tag) {
        item.send("setTag:", tag);
    }

    public void setTitle(Component title) {
        setComponent(title);
        refresh();
    }

    void setComponent(Component title) {
        item.send("setRepresentedObject:", new ComponentUpdater(title));
    }

    public void setKeyEquivalent(String keyEquivalent) {
        item.send("setKeyEquivalent:", keyEquivalent);
    }

    public void setState(@StateValue int state) {
        item.send("setState:", state);
    }

    public void setState(boolean state) {
        setState(state ? StateValue.ON : StateValue.OFF);
    }

    public void setBadge(String value) {
        Proxy badge = client.sendProxy("NSMenuItemBadge", "alloc");
        badge.send("initWithString:", value);
        item.send("setBadge:", badge);
    }

    public void setBadge(int value) {
        Proxy badge = client.sendProxy("NSMenuItemBadge", "alloc");
        badge.send("initWithCount:", value);
        item.send("setBadge:", badge);
    }

    public void setAlternate(boolean isAlternate) {
        item.send("setAlternate:", isAlternate);
    }

    @ApiStatus.Internal
    public void refresh() {
        Proxy representedObject = item.sendProxy("representedObject");
        representedObject.send("updateForMenuItem:", item);
    }
}
