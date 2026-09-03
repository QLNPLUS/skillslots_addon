package com.skillslotsaddon.kubejs;

import com.skillslotsaddon.AddonHooks;
import dev.latvian.mods.kubejs.event.EventHandler;

/** Installed reflectively by the mod when KubeJS is present. */
public final class KubeJsGuiBridge implements AddonHooks.IGuiEvents {

	public static final KubeJsGuiBridge INSTANCE = new KubeJsGuiBridge();

	private KubeJsGuiBridge() {
	}

	public static void register() {
		AddonHooks.setGuiEvents(INSTANCE);
	}

	@Override
	public void useGuiOpened() {
		post(SkillSlotsKubeEvents.USE_GUI_OPENED, "use");
	}

	@Override
	public void useGuiClosed() {
		post(SkillSlotsKubeEvents.USE_GUI_CLOSED, "use");
	}

	@Override
	public void placeGuiOpened() {
		post(SkillSlotsKubeEvents.PLACE_GUI_OPENED, "place");
	}

	@Override
	public void placeGuiClosed() {
		post(SkillSlotsKubeEvents.PLACE_GUI_CLOSED, "place");
	}

	private static void post(EventHandler handler, String guiId) {
		if (handler.hasListeners()) {
			handler.post(new SkillSlotGuiEventJS(guiId));
		}
	}
}
