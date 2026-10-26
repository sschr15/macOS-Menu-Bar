/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.sschr15.mods.menubar;

import ca.weblite.objc.NSObject;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.annotations.Msg;
import com.demonwav.mcdev.annotations.Translatable;
import com.sschr15.mods.menubar.mixin.KeyboardHandlerAccessor;
import com.sschr15.mods.menubar.objc.MenuBar;
import com.sschr15.mods.menubar.objc.Modifiers;
import com.sschr15.mods.menubar.objc.Option;
import com.sschr15.mods.menubar.objc.Submenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.intellij.lang.annotations.MagicConstant;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class DebugMenu extends NSObject {
    public DebugMenu() {
        super("NSObject");
    }

    private static Submenu menu;
    private static Option renderHitboxes;
    private static Option chunkBorders;
    private static Option advancedTooltips;
    private static Option pauseOnLostFocus;
    private static final List<Option> disabledIfReducedInfo = new ArrayList<>();

    public static boolean ready() {
        return menu != null;
    }

    public static void updateRenderHitboxes(boolean state) {
        renderHitboxes.setState(state);
    }

    public static void updateChunkBorders(boolean state) {
        chunkBorders.setState(state);
    }

    public static void updateAdvancedTooltips(boolean state) {
        advancedTooltips.setState(state);
    }

    public static void updatePauseOnLostFocus(boolean state) {
        pauseOnLostFocus.setState(state);
    }

    public static void refresh() {
        menu.update();
    }

    @Msg(selector = "runKey:", signature = Option.ACTION_SIGNATURE)
    public void runKey(Proxy menuItem) {
        int keyCode = menuItem.sendInt("tag") & 0xff;
        ((KeyboardHandlerAccessor) Minecraft.getInstance().keyboardHandler).menubar$runArbitraryRequest(keyCode);
    }

    @Msg(selector = "setGameMode:", signature = Option.ACTION_SIGNATURE)
    public void setGameMode(Proxy menuItem) {
        GameType gameMode = GameType.byId(menuItem.sendInt("tag") & ~0x200);
        if (Minecraft.getInstance().gameMode != null && Minecraft.getInstance().player != null) {
            GameType currentMode = Minecraft.getInstance().gameMode.getPlayerMode();
            if (currentMode != gameMode && Minecraft.getInstance().player.hasPermissions(2)) {
                Minecraft.getInstance().player.connection.sendUnsignedCommand("gamemode " + gameMode.getName());
            }
        }
    }

    @Msg(selector = "validateMenuItem:", signature = "c@:@")
    public boolean validateMenuItem(Proxy menuItem) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return false; // if no player, disable everything

        int tag = menuItem.sendInt("tag");
        boolean isReducedInfo = (tag & 0x100) != 0;
        boolean isGameModeSwitcher = (tag & 0x200) != 0;
        if (isReducedInfo) return !player.isReducedDebugInfo();
        if (isGameModeSwitcher) return player.hasPermissions(2);
        return true;
    }

    private Option option(@Translatable String title, @MagicConstant(valuesFromClass = GLFW.class) int keyCode) {
        Option option = menu.option(Component.translatable(title), "runKey:", "");
        option.setTarget(this);
        option.setTag(keyCode);
        return option;
    }

    public void addMenu(MenuBar bar) {
        menu = bar.menu(Component.translatable("menubar.debug"));

        option("menubar.debug.reload_chunks", GLFW.GLFW_KEY_A);
        renderHitboxes = option("menubar.debug.show_hitboxes", GLFW.GLFW_KEY_B);
        disabledIfReducedInfo.add(option("menubar.debug.copy_location", GLFW.GLFW_KEY_C));
        option("menubar.debug.clear_chat", GLFW.GLFW_KEY_D);
        chunkBorders = option("menubar.debug.chunk_boundaries", GLFW.GLFW_KEY_G);
        advancedTooltips = option("menubar.debug.advanced_tooltips", GLFW.GLFW_KEY_H);
        disabledIfReducedInfo.add(option("menubar.debug.copy_server_data", GLFW.GLFW_KEY_I));
        Option serversideData = option("menubar.debug.copy_client_data", GLFW.GLFW_KEY_I);
        serversideData.setModifiers(Modifiers.SHIFT);
        serversideData.setAlternate(true);
        disabledIfReducedInfo.add(serversideData);
        pauseOnLostFocus = option("menubar.debug.pause_focus", GLFW.GLFW_KEY_P);
        option("menubar.debug.dump_dynamic_textures", GLFW.GLFW_KEY_S);
        option("menubar.debug.reload_resourcepacks", GLFW.GLFW_KEY_T);

        Submenu gameModeSwitcher = menu.submenu(Component.translatable("menubar.debug.set_game_mode"));
        for (GameType gameMode : GameType.values()) {
            Option option = gameModeSwitcher.option(gameMode.getLongDisplayName(), "setGameMode:", "");
            option.setTarget(this);
            option.setTag(gameMode.getId() | 0x200);
        }

        for (Option option : disabledIfReducedInfo) {
            option.setTag(option.getTag() | 0x100);
        }

        Options options = Minecraft.getInstance().options;
        updatePauseOnLostFocus(options.pauseOnLostFocus);
        updateAdvancedTooltips(options.advancedItemTooltips);
    }
}
