/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar;

import ca.weblite.objc.NSObject;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.annotations.Msg;
import com.sschr15.mods.menubar.objc.MenuBar;
import com.sschr15.mods.menubar.objc.Option;
import com.sschr15.mods.menubar.objc.Submenu;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.config.SidebarType;
import dev.emi.emi.runtime.EmiHistory;
import dev.emi.emi.runtime.EmiSidebars;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class EmiMenu extends NSObject {
    public static EmiMenu instance;
    private Submenu history;
    private Submenu menu;

    public EmiMenu() {
        super("NSObject");
        instance = this;
    }

    @Msg(selector = "emiBack:", signature = Option.ACTION_SIGNATURE)
    public void emiBack(Proxy ignored) {
        EmiHistory.pop();
    }

    @Msg(selector = "emiForward:", signature = Option.ACTION_SIGNATURE)
    public void emiForward(Proxy ignored) {
        EmiHistory.forward();
    }

    @Msg(selector = "validateMenuItem:", signature = "c@:@")
    public boolean validateMenuItem(Proxy item) {
        if (item.sendInt("tag") == 100) {
            return !EmiHistory.isEmpty();
        }
        if (item.sendInt("tag") == 101) {
            return !EmiHistory.isForwardEmpty();
        }
        return true;
    }

    public void show() {
        menu.show();
    }

    public void hide() {
        menu.hide();
    }

    public void addMenu(MenuBar menuBar) {
        menu = menuBar.menu(Component.literal("EMI"));

        Option back = menu.option(Component.translatable("key.emi.back"), "emiBack:", "");
        back.setTag(100);
        back.setTarget(this);
        Option forward = menu.option(Component.translatable("key.emi.forward"), "emiForward:", "");
        forward.setTag(101);
        forward.setTarget(this);

        menu.separator();

        history = menu.submenu(Component.translatable("emi.sidebar.type.lookup_history"));
        // history added via mixin injects

        menu.hide();
    }

    public void updateHistory() {
        history.clear();
        EmiSidebars.getStacks(SidebarType.LOOKUP_HISTORY).stream().limit(20).forEach(stack -> {
            EmiStack itemStack = stack.getEmiStacks().get(0);
            MutableComponent name = itemStack.getName().copy();
            if (stack.getEmiStacks().size() > 1) {
                name.append(" (+" + (stack.getEmiStacks().size() - 1) + ")");
            }
            history.option(name, "", () -> EmiApi.displayRecipes(stack));
        });
    }
}
