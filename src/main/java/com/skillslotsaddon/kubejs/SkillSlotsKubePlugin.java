package com.skillslotsaddon.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

public final class SkillSlotsKubePlugin extends KubeJSPlugin {

	@Override
	public void registerEvents() {
		SkillSlotsKubeEvents.GROUP.register();
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		if (event.getType() == ScriptType.CLIENT) {
			event.add("SkillSlotsGui", SkillSlotsGuiApi.INSTANCE);
		}
	}
}
