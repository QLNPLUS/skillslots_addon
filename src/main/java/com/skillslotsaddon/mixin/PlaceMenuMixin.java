package com.skillslotsaddon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import snownee.skillslots.menu.PlaceMenu;

/**
 * Shows up to 8 slot positions in the placement menu and shifts the row left so
 * the close button stays inside the 176px wide hopper texture.
 */
@Mixin(value = PlaceMenu.class, remap = false)
public abstract class PlaceMenuMixin {

	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 4))
	private int skillslotsaddon$moreSlots(int original) {
		return 8;
	}

	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 44))
	private int skillslotsaddon$slotRowX(int original) {
		return 8;
	}

	// The close button x is compiled as xStart + 72 (4 * 18 is constant-folded).
	// With 8 positions the close button must sit at xStart + 144.
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 72))
	private int skillslotsaddon$closeButtonX(int original) {
		return 144;
	}
}
