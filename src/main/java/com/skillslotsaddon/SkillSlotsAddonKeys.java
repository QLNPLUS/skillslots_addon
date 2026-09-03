package com.skillslotsaddon;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Owns the addon's GUI-only skill slot keybinds. These are deliberately kept
 * separate from SkillSlots' global use-slot keybinds, which are polled while no
 * screen is open.
 */
@Mod.EventBusSubscriber(modid = SkillSlotsAddon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SkillSlotsAddonKeys {

	public static final int COUNT = 8;
	public static final KeyMapping[] GUI_USES = new KeyMapping[COUNT];

	static {
		for (int i = 0; i < GUI_USES.length; i++) {
			GUI_USES[i] = new KeyMapping(
					"keybind.skillslots_addon.gui_use." + (i + 1),
					GLFW.GLFW_KEY_1 + i,
					"gui.skillslots_addon.keygroup");
		}
	}

	private SkillSlotsAddonKeys() {
	}

	@SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event) {
		for (KeyMapping keyMapping : GUI_USES) {
			event.register(keyMapping);
		}
	}

	/**
	 * Number of slots the wheel currently shows: everything when locked slots
	 * are visible, otherwise only the unlocked ones.
	 */
	public static int visibleCount(int maxSlots, int containerSize) {
		return AddonConfig.hideLockedSlots() ? Math.min(maxSlots, containerSize) : maxSlots;
	}

	/**
	 * Keybind index shown/used for a slot. Top row always uses keys 1-4,
	 * bottom row always uses keys 5-8, so the mapping is positional and does
	 * not shift when the unlocked slot count changes.
	 */
	public static int keyIndexForSlot(int slotIndex, int maxSlots, int containerSize) {
		int count = visibleCount(maxSlots, containerSize);
		if (count <= 4) {
			return slotIndex;
		}
		int top = (count + 1) / 2;
		return slotIndex < top ? slotIndex : 4 + (slotIndex - top);
	}

	/**
	 * Slot activated by a keybind. Keys 1-4 map to the top row, keys 5-8 map
	 * to the bottom row; returns -1 when that position has no slot.
	 */
	public static int slotForKey(int keyIndex, int maxSlots, int containerSize) {
		int count = visibleCount(maxSlots, containerSize);
		if (count <= 4) {
			return keyIndex < count ? keyIndex : -1;
		}
		int top = (count + 1) / 2;
		if (keyIndex < 4) {
			return keyIndex < top ? keyIndex : -1;
		}
		int col = keyIndex - 4;
		return col < count - top ? top + col : -1;
	}
}
