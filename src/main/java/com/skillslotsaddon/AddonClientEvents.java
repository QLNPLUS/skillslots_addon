package com.skillslotsaddon;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import snownee.skillslots.client.gui.PlaceScreen;
import snownee.skillslots.client.gui.UseScreen;

/** Client-only listeners that feed GUI open/close events to the KubeJS bridge. */
public final class AddonClientEvents {

	private AddonClientEvents() {
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(AddonClientEvents::onScreenOpening);
		MinecraftForge.EVENT_BUS.addListener(AddonClientEvents::onScreenClosing);
	}

	private static void onScreenOpening(ScreenEvent.Opening event) {
		AddonConfig.apply();
		Screen screen = event.getNewScreen();
		if (screen instanceof UseScreen) {
			AddonHooks.fireUseGuiOpened();
		} else if (screen instanceof PlaceScreen) {
			AddonHooks.firePlaceGuiOpened();
		}
	}

	private static void onScreenClosing(ScreenEvent.Closing event) {
		Screen screen = event.getScreen();
		if (screen instanceof UseScreen) {
			AddonHooks.fireUseGuiClosed();
		} else if (screen instanceof PlaceScreen) {
			AddonHooks.firePlaceGuiClosed();
		}
	}
}
