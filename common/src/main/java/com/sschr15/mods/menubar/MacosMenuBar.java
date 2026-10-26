package com.sschr15.mods.menubar;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sschr15.mods.menubar.objc.MenuBar;
import com.sschr15.mods.menubar.objc.Modifiers;
import com.sschr15.mods.menubar.objc.Submenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MacosMenuBar {
    static final Logger LOGGER = LoggerFactory.getLogger(MacosMenuBar.class);
    private static final List<Consumer<MenuBar>> HANDLERS = new ArrayList<>();
    private static MenuBar menuBar;

    @ApiStatus.Internal
    static Platform platform;

    public static void runOnRenderThread(RenderCall call) {
        if (RenderSystem.isOnRenderThread()) {
            call.execute();
        } else {
            RenderSystem.recordRenderCall(call);
        }
    }

    public static void registerMenubarSetup(Consumer<MenuBar> setup) {
        if (menuBar == null) {
            HANDLERS.add(setup);
        } else {
            runOnRenderThread(() -> setup.accept(menuBar));
        }
    }

    public static void refresh() {
        menuBar.update();
    }

    @ApiStatus.Internal
    public static void setup() {
        runOnRenderThread(() -> {
            menuBar = new MenuBar();

            Submenu appMenu = menuBar.menu(Component.translatable("menu.game"));
            appMenu.services();
            appMenu.option(Component.translatable("menu.options"), ",", () -> Minecraft.getInstance().setScreen(new OptionsScreen(Minecraft.getInstance().screen, Minecraft.getInstance().options)));

            if (platform != null) {
                platform.addModsOption(appMenu);
            }

            appMenu.separator();
            appMenu.option(Component.translatable("menubar.app_menu.hide"), "hide:", "h");
            appMenu.option(Component.translatable("menubar.app_menu.hide_others"), "hideOtherApplications:", "h").setModifiers(Modifiers.OPTION | Modifiers.COMMAND);
            appMenu.option(Component.translatable("menubar.app_menu.unhide_all"), "unhideAllApplications:", "");
            appMenu.separator();
            appMenu.option(Component.translatable("menubar.app_menu.quit"), "terminate:", "q");

            HANDLERS.forEach(handler -> handler.accept(menuBar));

            Submenu window = menuBar.window();
            window.option(Component.translatable("menubar.window.minimize"), "performMiniaturize:", "m");
            window.option(Component.translatable("menubar.window.zoom"), "performZoom:", "z");
            window.option(Component.translatable("menubar.window.enter_full_screen"), "toggleFullScreen:", "f").setModifiers(Modifiers.FUNCTION);

            Submenu help = menuBar.help();
            help.option(Component.translatable("menubar.help.about"), "orderFrontStandardAboutPanel:", "");
            help.option(Component.translatable("options.credits_and_attribution"), "", () -> Minecraft.getInstance().setScreen(new CreditsAndAttributionScreen(Minecraft.getInstance().screen)));

            menuBar.update();
        });
    }
}
