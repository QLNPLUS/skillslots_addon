package com.skillslotsaddon.kubejs;

import net.minecraft.client.Minecraft;
import snownee.skillslots.client.gui.UseScreen;

/**
 * KubeJS client binding "SkillSlotsGui": lets client scripts open and close
 * the skill wheel screen programmatically.
 */
public final class SkillSlotsGuiApi {

	public static final SkillSlotsGuiApi INSTANCE = new SkillSlotsGuiApi();

	private SkillSlotsGuiApi() {
	}

	/** Opens the skill wheel. Returns false if there is no controllable player. */
	public boolean open() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.player.isSpectator()) {
			return false;
		}
		mc.setScreen(new UseScreen());
		return true;
	}

	/** Closes the skill wheel if it is open. */
	public boolean close() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.screen instanceof UseScreen) {
			mc.setScreen(null);
			return true;
		}
		return false;
	}

	/** Returns true while the skill wheel is open. */
	public boolean isOpen() {
		return Minecraft.getInstance().screen instanceof UseScreen;
	}
}
