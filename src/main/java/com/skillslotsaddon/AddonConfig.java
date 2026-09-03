package com.skillslotsaddon;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import snownee.skillslots.SkillSlotsCommonConfig;

public final class AddonConfig {

	public enum LayoutMode {
		AUTO, DIAMOND, HORIZONTAL
	}

	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final ForgeConfigSpec CLIENT_SPEC;

	public static final ForgeConfigSpec.IntValue MAX_SLOTS;
	public static final ForgeConfigSpec.IntValue BEGINNER_SLOTS;
	public static final ForgeConfigSpec.BooleanValue BLOCK_RIGHT_CLICK;
	public static final ForgeConfigSpec.BooleanValue ENTITY_RIGHT_CLICK;
	public static final ForgeConfigSpec.BooleanValue ITEM_RIGHT_CLICK;
	public static final ForgeConfigSpec.EnumValue<LayoutMode> LAYOUT_MODE;
	public static final ForgeConfigSpec.BooleanValue HIDE_LOCKED_SLOTS;
	public static final ForgeConfigSpec.BooleanValue SHOW_ITEM_TOOLTIPS;
	public static final ForgeConfigSpec.BooleanValue ENABLE_CLOSED_WHEEL_SHORTCUTS;

	static {
		COMMON_BUILDER.comment("SkillSlots Addon common settings (server side).").push("general");
		MAX_SLOTS = COMMON_BUILDER
				.comment(
						"Maximum number of skill slots. SkillSlots originally caps this at 4; this addon raises the limit to 8.",
						"Range: 1-8. Changing it requires restarting the world (SkillSlots treats it as level-restart).")
				.defineInRange("maxSlots", 8, 1, 8);
		BEGINNER_SLOTS = COMMON_BUILDER
				.comment("How many slots are unlocked from the start. Range: 0-8.")
				.defineInRange("beginnerSlots", 0, 0, 8);
		BLOCK_RIGHT_CLICK = COMMON_BUILDER
				.comment(
						"If false, using a skill while looking at a block will NOT fire the block right-click action.",
						"The skill falls through to the item right-click action when that is enabled.")
				.define("enableBlockRightClick", true);
		ENTITY_RIGHT_CLICK = COMMON_BUILDER
				.comment(
						"If false, using a skill while looking at an entity will NOT fire the entity right-click action.",
						"The skill falls through to the item right-click action when that is enabled.")
				.define("enableEntityRightClick", true);
		ITEM_RIGHT_CLICK = COMMON_BUILDER
				.comment(
						"If false, using a skill without a block/entity target (or after a passed block/entity use) will NOT fire the item right-click action.")
				.define("enableItemRightClick", true);
		COMMON_SPEC = COMMON_BUILDER.build();

		CLIENT_BUILDER.comment("SkillSlots Addon client settings.").push("general");
		LAYOUT_MODE = CLIENT_BUILDER
				.comment(
						"Skill wheel button layout.",
						"AUTO (default): diamond (up/down/left/right) for 1-4 slots, staggered double row for 5-8 slots.",
						"DIAMOND: always use the original diamond layout. Forced to the row layout when maxSlots >= 5.",
						"HORIZONTAL: single row for 1-4 slots, staggered double row for 5-8 slots.")
				.defineEnum("layoutMode", LayoutMode.AUTO);
		HIDE_LOCKED_SLOTS = CLIENT_BUILDER
				.comment(
						"If true, the skill wheel only shows unlocked slots.",
						"If false (default), locked slots are shown with the original 'locked' label.",
						"The slot placement screen always shows every slot position.")
				.define("hideLockedSlots", false);
		SHOW_ITEM_TOOLTIPS = CLIENT_BUILDER
				.comment(
						"If true, hovering a skill slot in the skill wheel shows the item's tooltip.",
						"Original SkillSlots releases do not display item tooltips on hover.")
				.define("showItemTooltips", true);
		ENABLE_CLOSED_WHEEL_SHORTCUTS = CLIENT_BUILDER
				.comment(
						"If true, SkillSlots' own use-slot keys activate skills while the skill wheel is closed.",
						"This does not affect the addon's separate GUI-only slot keys.",
						"If false (default), SkillSlots' global use-slot keys do not activate skills.")
				.define("enableClosedWheelShortcuts", false);
		CLIENT_SPEC = CLIENT_BUILDER.build();
	}

	private AddonConfig() {
	}

	public static void register() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
	}

	/**
	 * Pushes the addon values into SkillSlots' own config fields. SkillSlots' Kiwi
	 * config caps maxSlots at 4, so this must be applied after Kiwi finished loading.
	 */
	public static void apply() {
		int maxSlots = MAX_SLOTS.get();
		int beginnerSlots = BEGINNER_SLOTS.get();
		if (SkillSlotsCommonConfig.maxSlots != maxSlots || SkillSlotsCommonConfig.beginnerSlots != beginnerSlots) {
			SkillSlotsCommonConfig.maxSlots = maxSlots;
			SkillSlotsCommonConfig.beginnerSlots = beginnerSlots;
			SkillSlotsAddon.LOGGER.info(
					"Applied SkillSlots addon settings: maxSlots={}, beginnerSlots={}",
					maxSlots, beginnerSlots);
		}
	}

	public static boolean blockRightClickEnabled() {
		return BLOCK_RIGHT_CLICK.get();
	}

	public static boolean entityRightClickEnabled() {
		return ENTITY_RIGHT_CLICK.get();
	}

	public static boolean itemRightClickEnabled() {
		return ITEM_RIGHT_CLICK.get();
	}

	public static boolean isRowLayout(int maxSlots) {
		if (maxSlots >= 5) {
			return true;
		}
		return LAYOUT_MODE.get() == LayoutMode.HORIZONTAL;
	}

	public static boolean hideLockedSlots() {
		return HIDE_LOCKED_SLOTS.get();
	}

	public static boolean showItemTooltips() {
		return SHOW_ITEM_TOOLTIPS.get();
	}

	public static boolean enableClosedWheelShortcuts() {
		return ENABLE_CLOSED_WHEEL_SHORTCUTS.get();
	}

	public static void onModConfig(ModConfigEvent event) {
		if (event instanceof ModConfigEvent.Loading || event instanceof ModConfigEvent.Reloading) {
			apply();
		}
	}
}
