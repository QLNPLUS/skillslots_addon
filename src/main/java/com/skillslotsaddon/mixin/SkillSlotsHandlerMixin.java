package com.skillslotsaddon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import snownee.skillslots.SkillSlotsHandler;
import com.skillslotsaddon.AddonConfig;

/**
 * Raises SkillSlotsHandler's hardcoded MAX_SLOTS from 4 to 8. The constant is
 * inlined by javac into every array size, BitSet size and loop bound, so each
 * literal 4 inside the affected methods is replaced.
 */
@Mixin(value = SkillSlotsHandler.class, remap = false)
public abstract class SkillSlotsHandlerMixin {

	@ModifyConstant(
			method = "<init>",
			constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsInit(int original) {
		return 8;
	}

	// Each constant site has its own injection with require=1 (default), so a
	// wrong method name fails loudly at load instead of silently skipping and
	// truncating item persistence to 4 slots.
	@ModifyConstant(method = "setSlots", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsSetSlots(int original) {
		return 8;
	}

	@ModifyConstant(method = "m_6596_", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsSetChanged(int original) {
		return 8;
	}

	@ModifyConstant(method = "updateColors", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsUpdateColors(int original) {
		return 8;
	}

	@ModifyConstant(method = "m_7927_", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsCreateTag(int original) {
		return 8;
	}

	@ModifyConstant(method = "m_7797_", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsFromTag(int original) {
		return 8;
	}

	@ModifyConstant(method = "serializeNBT", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsSerialize(int original) {
		return 8;
	}

	@ModifyConstant(method = "deserializeNBT", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsDeserialize(int original) {
		return 8;
	}

	@ModifyConstant(method = "m_7013_", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsCanPlace(int original) {
		return 8;
	}

	@ModifyConstant(method = "copyFrom", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsCopy(int original) {
		return 8;
	}

	@ModifyConstant(method = "tick", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsTick(int original) {
		return 8;
	}

	@ModifyConstant(method = "findActivatedPassiveSkill", constant = @Constant(intValue = 4))
	private static int skillslotsaddon$maxSlotsFind(int original) {
		return 8;
	}

	// Kiwi reloads SkillSlots' own config (capped at 4) at unpredictable times,
	// overwriting the addon values. Re-apply our config right before every
	// clamp so slot counts, sync and commands always honor the addon settings.
	@Inject(method = "setSlots", at = @At("HEAD"))
	private void skillslotsaddon$applyConfigBeforeSet(int slots, CallbackInfo ci) {
		AddonConfig.apply();
	}

	@Inject(method = "deserializeNBT", at = @At("HEAD"))
	private void skillslotsaddon$applyConfigBeforeLoad(CompoundTag data, CallbackInfo ci) {
		AddonConfig.apply();
	}
}
