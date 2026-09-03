package com.skillslotsaddon.kubejs;

import dev.latvian.mods.kubejs.client.ClientEventJS;

/** KubeJS event object for SkillSlots GUI open/close events (client side). */
public class SkillSlotGuiEventJS extends ClientEventJS {

	private final String guiId;

	public SkillSlotGuiEventJS(String guiId) {
		this.guiId = guiId;
	}

	/** "use" for the skill wheel, "place" for the slot placement screen. */
	public String getGuiId() {
		return guiId;
	}

	public boolean isUseGui() {
		return "use".equals(guiId);
	}

	public boolean isPlaceGui() {
		return "place".equals(guiId);
	}
}
