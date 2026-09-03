package com.skillslotsaddon;

/**
 * Core hooks. The GUI event bridge is only installed when KubeJS is loaded,
 * so this class never touches KubeJS classes directly.
 */
public final class AddonHooks {

	public interface IGuiEvents {
		void useGuiOpened();

		void useGuiClosed();

		void placeGuiOpened();

		void placeGuiClosed();
	}

	private static IGuiEvents guiEvents;

	private AddonHooks() {
	}

	public static void setGuiEvents(IGuiEvents events) {
		guiEvents = events;
	}

	public static void fireUseGuiOpened() {
		if (guiEvents != null) {
			guiEvents.useGuiOpened();
		}
	}

	public static void fireUseGuiClosed() {
		if (guiEvents != null) {
			guiEvents.useGuiClosed();
		}
	}

	public static void firePlaceGuiOpened() {
		if (guiEvents != null) {
			guiEvents.placeGuiOpened();
		}
	}

	public static void firePlaceGuiClosed() {
		if (guiEvents != null) {
			guiEvents.placeGuiClosed();
		}
	}
}
