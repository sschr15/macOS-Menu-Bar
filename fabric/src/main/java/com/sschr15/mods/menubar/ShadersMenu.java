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
import com.sschr15.mods.menubar.objc.StateValue;
import com.sschr15.mods.menubar.objc.Submenu;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.*;

public class ShadersMenu extends NSObject {
    public ShadersMenu() {
        super("NSObject");
    }
    private Option currentOption;
    private Option theDisabledOption;
    private final Map<String, Option> options = new HashMap<>();

    @Msg(selector = "setShaderPack:", signature = "v@:@")
    public void setShaderPack(Proxy selectedMenuItem) {
        String pack = selectedMenuItem.sendString("title");
        Iris.getIrisConfig().setShaderPackName(pack);
        IrisApi.getInstance().getConfig().setShadersEnabledAndApply(true);
        currentOption.setState(StateValue.OFF);
        Option newOption = options.get(pack);
        if (newOption != null) {
            newOption.setState(StateValue.ON);
            currentOption = newOption;
        }
    }

    @Msg(selector = "disableShaders:", signature = "v@:@")
    public void disableShaders(Proxy ignored) {
        IrisApi.getInstance().getConfig().setShadersEnabledAndApply(false);
        currentOption.setState(StateValue.OFF);
        theDisabledOption.setState(StateValue.ON);
        currentOption = theDisabledOption;
    }

    public void addMenu(MenuBar bar) {
        Submenu shaders = bar.menu(Component.translatable("options.iris.shaderPackSelection.title"));

        new Thread(() -> {
            try {
                WatchService watcher = Iris.getShaderpacksDirectory().getFileSystem().newWatchService();
                Iris.getShaderpacksDirectory().register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {
                    Option disabled = shaders.option(Component.translatable("label.iris.false"), "disableShaders:", "");
                    disabled.setTarget(this);
                    theDisabledOption = disabled;
                    if (!Iris.getIrisConfig().areShadersEnabled()) {
                        disabled.setState(StateValue.ON);
                        currentOption = disabled;
                    }

                    List<String> shaderPacks;
                    shaderPacks = Iris.getShaderpacksDirectoryManager().enumerate();

                    for (String shaderPack : shaderPacks) {
                        Option option = shaders.option(Component.literal(shaderPack), "setShaderPack:", "");
                        option.setTarget(this);
                        boolean enabled = Iris.getIrisConfig().areShadersEnabled() && shaderPack.equals(Iris.getCurrentPackName());
                        option.setState(enabled ? StateValue.ON : StateValue.OFF);
                        if (enabled) currentOption = option;
                        options.put(shaderPack, option);
                    }

                    watcher.take();
                    shaders.clear();
                    options.clear();
                }
            } catch (IOException e) {
                MacosMenuBar.LOGGER.error("Failed to watch shaderpacks directory", e);
            } catch (InterruptedException e) {
                MacosMenuBar.LOGGER.warn("Interrupted while watching, aborting", e);
            }
        }, "Shader pack checker").start();
    }
}
