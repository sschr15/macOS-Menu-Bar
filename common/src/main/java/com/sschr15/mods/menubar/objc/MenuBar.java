/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar.objc;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public final class MenuBar {
    private final Client client = Client.getInstance();
    private final Proxy app = client.sendProxy("NSApplication", "sharedApplication");
    private final Proxy menuBar = client.sendProxy("NSMenu", "alloc");
    private final List<Submenu> children = new ArrayList<>();

    private int index = 0;

    public MenuBar() {
        menuBar.send("initWithTitle:", "");
        app.send("setMainMenu:", menuBar);
    }

    public Submenu menu(Component title) {
        Proxy item = client.sendProxy("NSMenuItem", "alloc");
        item.send("initWithTitle:action:keyEquivalent:", title.getString(), null, "");
        Proxy menu = client.sendProxy("NSMenu", "alloc");
        menu.send("setTitle:", title.getString());
        item.send("setSubmenu:", menu);
        int i = index++;
        menuBar.send("insertItem:atIndex:", item, i);
        Submenu submenu = new Submenu(client, app, menu, item);
        submenu.setComponent(title);
        children.add(i, submenu);
        return submenu;
    }

    public Submenu window() {
        Proxy item = menuBar.sendProxy("addItemWithTitle:action:keyEquivalent:", I18n.get("menubar.window"), null, "");
        Proxy menu = client.sendProxy("NSMenu", "alloc");
        app.send("setWindowsMenu:", menu);
        item.send("setSubmenu:", menu);
        Submenu submenu = new Submenu(client, app, menu, item);
        submenu.setComponent(Component.translatable("menubar.window"));
        children.add(submenu);
        return submenu;
    }

    public Submenu help() {
        Proxy item = menuBar.sendProxy("addItemWithTitle:action:keyEquivalent:", I18n.get("menubar.help"), null, "");
        Proxy menu = client.sendProxy("NSMenu", "alloc");
        app.send("setHelpMenu:", menu);
        item.send("setSubmenu:", menu);
        Submenu submenu = new Submenu(client, app, menu, item);
        submenu.setComponent(Component.translatable("menubar.help"));
        children.add(submenu);
        return submenu;
    }

    public void update() {
        menuBar.send("update");
    }

    @ApiStatus.Internal
    public void refresh() {
        children.forEach(MenuItem::refresh);
        update();
    }
}
