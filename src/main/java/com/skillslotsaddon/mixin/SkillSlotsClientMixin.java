package com.skillslotsaddon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import snownee.skillslots.SkillSlotsHandler;
import snownee.skillslots.client.SkillSlotsClient;
import com.skillslotsaddon.AddonConfig;

/** Grows the use keybind array from 4 to 8 so every skill slot has a keybind. */
@Mixin(value = SkillSlotsClient.class, remap = false)
public abstract class SkillSlotsClientMixin {

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$keybindCount(int original) {
		return 8;
	}

	/**
	 * SkillSlots' own use-slot keys remain a separate, global set. They activate
	 * their matching slot only when closed-wheel shortcuts are enabled.
	 */
	@Redirect(
			method = "onKeyInput",
			at = @At(
					value = "INVOKE",
					target = "Lsnownee/skillslots/SkillSlotsHandler;startUsing(I)V"))
	private static void skillslotsaddon$directUse(SkillSlotsHandler handler, int slot) {
		if (AddonConfig.enableClosedWheelShortcuts() && handler.canUseSlot(slot)) {
			handler.startUsing(slot);
		}
	}
}
