/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.objc;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.RuntimeUtils;
import ca.weblite.objc.annotations.Msg;
import com.sun.jna.Pointer;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class Submenu extends MenuItem {

    static class Actionable extends NSObject {
        private final Runnable action;

        Actionable(Runnable action) {
            super("NSObject");
            this.action = action;
        }

        @Msg(selector = "performAction:", signature = Option.ACTION_SIGNATURE)
        private void performAction(Proxy ignored) {
            action.run();
        }
    }

    private final Proxy menu;
    private final Proxy app;
    private final List<MenuItem> children = new ArrayList<>();

    Submenu(Client client, Proxy app, Proxy menu, Proxy item) {
        super(client, item);
        this.app = app;
        this.menu = menu;
    }

    public void clear() {
        menu.send("removeAllItems");
    }

    public Option option(Component title, String action, String keyEquivalent) {
        return option(title, RuntimeUtils.sel(action), keyEquivalent);
    }

    public Option option(Component title, Pointer action, String keyEquivalent) {
        Proxy item = client.sendProxy("NSMenuItem", "alloc");
        item.send("initWithTitle:action:keyEquivalent:", title.getString(), action, keyEquivalent);
        menu.send("addItem:", item);
        Option option = new Option(client, item);
        option.setComponent(title);
        children.add(option);
        return option;
    }

    public Option option(Component title, String keyEquivalent, Runnable action) {
        Actionable actionable = new Actionable(action);
        Proxy item = client.sendProxy("NSMenuItem", "alloc");
        item.send("initWithTitle:action:keyEquivalent:", title.getString(), RuntimeUtils.sel("performAction:"), keyEquivalent);
        item.send("setTarget:", actionable);
        menu.send("addItem:", item);
        Option option = new Option(client, item);
        option.setComponent(title);
        children.add(option);
        return option;
    }

    public Submenu submenu(Component title) {
        Proxy item = menu.sendProxy("addItemWithTitle:action:keyEquivalent:", title.getString(), null, "");
        Proxy submenu = client.sendProxy("NSMenu", "alloc");
        item.send("setSubmenu:", submenu);
        Submenu submenu1 = new Submenu(client, app, submenu, item);
        submenu1.setComponent(title);
        children.add(submenu1);
        return submenu1;
    }

    public void services() {
        Option option = option(Component.translatable("menubar.app_menu.services"), (Pointer) null, "");
        Proxy services = client.sendProxy("NSMenu", "alloc");
        app.send("setServicesMenu:", services);
        option.item.send("setSubmenu:", services);
    }

    public void separator() {
        menu.send("addItem:", client.sendProxy("NSMenuItem", "separatorItem"));
    }

    public Option sectionHeader(Component title) {
        Proxy item = client.sendProxy("NSMenuItem", "sectionHeaderWithTitle:", title.getString());
        menu.send("addItem:", item);
        Option option = new Option(client, item);
        option.setComponent(title);
        children.add(option);
        return option;
    }

    public void update() {
        menu.send("update");
    }

    @Override
    public void refresh() {
        super.refresh();
        children.forEach(MenuItem::refresh);
        update();
    }
}
