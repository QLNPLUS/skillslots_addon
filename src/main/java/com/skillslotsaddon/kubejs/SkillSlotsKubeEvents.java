package com.skillslotsaddon.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/** Event group for SkillSlots addon KubeJS events. */
public interface SkillSlotsKubeEvents {

	EventGroup GROUP = EventGroup.of("SkillSlotsEvents");

	EventHandler USE_GUI_OPENED = GROUP.client("useGuiOpened", () -> SkillSlotGuiEventJS.class);
	EventHandler USE_GUI_CLOSED = GROUP.client("useGuiClosed", () -> SkillSlotGuiEventJS.class);
	EventHandler PLACE_GUI_OPENED = GROUP.client("placeGuiOpened", () -> SkillSlotGuiEventJS.class);
	EventHandler PLACE_GUI_CLOSED = GROUP.client("placeGuiClosed", () -> SkillSlotGuiEventJS.class);
}
