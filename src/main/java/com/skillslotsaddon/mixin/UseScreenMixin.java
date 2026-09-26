package com.skillslotsaddon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import java.util.Locale;
import snownee.skillslots.SkillSlotsHandler;
import snownee.skillslots.SkillSlotsCommonConfig;
import snownee.skillslots.skill.Skill;
import snownee.skillslots.client.gui.UseScreen;
import com.skillslotsaddon.AddonConfig;
import com.skillslotsaddon.SkillSlotsAddonKeys;

/**
 * Supports up to 8 skill wheel buttons and switches the wheel to a horizontal
 * row when maxSlots >= 5 or when the layoutMode config requests it.
 */
@Mixin(value = UseScreen.class, remap = false)
public abstract class UseScreenMixin {

	@Shadow
	private float openTick;

	@Shadow
	private SkillSlotsHandler handler;

	@Shadow
	private int clickIndex;

	@Shadow
	private boolean closing;

	@Shadow
	private int useIndex;

	@Shadow
	private void drawButton(GuiGraphics graphics, float xCenter, float yCenter, int mouseX, int mouseY, int index, float pTicks) {
	}

	@Shadow
	private void startUse(int index) {
	}

	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 4))
	private int skillslotsaddon$biggerArrays(int original) {
		return 8;
	}

	@Redirect(
			method = "m_88315_",
			at = @At(
					value = "INVOKE",
					target = "Lsnownee/skillslots/client/gui/UseScreen;drawButton(Lnet/minecraft/client/gui/GuiGraphics;FFIIIF)V"))
	private void skillslotsaddon$redirectDrawButton(UseScreen screen,
			GuiGraphics graphics, float xCenter, float yCenter, int mouseX, int mouseY, int index, float pTicks) {
		skillslotsaddon$drawLayout(graphics, xCenter, yCenter, mouseX, mouseY, index, pTicks);
	}

	@Inject(
			method = "m_88315_",
			at = @At(
					value = "FIELD",
					target = "Lsnownee/skillslots/client/gui/UseScreen;clickIndex:I",
					ordinal = 1,
					shift = At.Shift.BEFORE))
	private void skillslotsaddon$drawExtraButtons(GuiGraphics graphics, int mouseX, int mouseY, float pTicks, CallbackInfo ci) {
		int maxSlots = SkillSlotsCommonConfig.maxSlots;
		if (maxSlots <= 4) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		float xCenter = minecraft.getWindow().getGuiScaledWidth() / 2.0F;
		float yCenter = minecraft.getWindow().getGuiScaledHeight() / 2.0F;
		int count = skillslotsaddon$visibleCount(maxSlots);
		for (int i = 0; i < count; i++) {
			skillslotsaddon$drawLayout(graphics, xCenter, yCenter, mouseX, mouseY, i, pTicks);
		}
	}

	@Unique
	private void skillslotsaddon$drawLayout(
			GuiGraphics graphics, float xCenter, float yCenter, int mouseX, int mouseY, int index, float pTicks) {
		if (skillslotsaddon$isHidden(index)) {
			return;
		}
		float[] position = skillslotsaddon$position(xCenter, yCenter, index);
		this.drawButton(graphics, position[0], position[1], mouseX, mouseY, index, pTicks);
	}

	@Unique
	private float[] skillslotsaddon$position(float xCenter, float yCenter, int index) {
		int maxSlots = SkillSlotsCommonConfig.maxSlots;
		int count = skillslotsaddon$visibleCount(maxSlots);
		if (AddonConfig.isRowLayout(maxSlots)) {
			// 1-4 slots: single centered row. 5-8 slots: two rows, staggered for
			// odd counts (bottom slot sits between two top slots), aligned for
			// even counts. Shrink spacing on narrow windows.
			float window = Minecraft.getInstance().getWindow().getGuiScaledWidth();
			if (count <= 4) {
				float spacing = 95.0F;
				if (count > 1) {
					spacing = Math.min(spacing, window * 0.9F / (count - 1));
				}
				float x = xCenter + (index - (count - 1) / 2.0F) * spacing;
				return new float[] {x, yCenter};
			}
			int top = (count + 1) / 2;
			int bottom = count - top;
			float spacing = 95.0F;
			if (top > 1) {
				spacing = Math.min(spacing, window * 0.9F / (top - 1));
			}
			// Tight, just-nesting rows: the diamond edges stay ~15px apart.
			float rowOffset = 24.0F;
			// Staggered for every count: bottom slots sit between top slots.
			// Shift the whole lattice so it stays centered for even counts.
			float shift = count % 2 == 0 ? spacing * 0.25F : 0.0F;
			float relativeX;
			if (index < top) {
				relativeX = (index - (top - 1) / 2.0F) * spacing;
			} else {
				relativeX = (index - top - (top - 1) / 2.0F) * spacing + spacing / 2.0F;
			}
			float x = xCenter + relativeX - shift;
			float y = index < top ? yCenter - rowOffset : yCenter + rowOffset;
			return new float[] {x, y};
		}
		float offset = 35.0F + openTick * 25.0F;
		return switch (count) {
			case 1 -> new float[] {xCenter, yCenter};
			case 2 -> index == 0
					? new float[] {xCenter - offset, yCenter}
					: new float[] {xCenter + offset, yCenter};
			case 3 -> switch (index) {
				case 0 -> new float[] {xCenter - offset, yCenter};
				case 1 -> new float[] {xCenter, yCenter - offset};
				default -> new float[] {xCenter + offset, yCenter};
			};
			default -> switch (index) {
				case 0 -> new float[] {xCenter - offset, yCenter};
				case 1 -> new float[] {xCenter, yCenter - offset};
				case 2 -> new float[] {xCenter + offset, yCenter};
				case 3 -> new float[] {xCenter, yCenter + offset};
				default -> new float[] {xCenter, yCenter};
			};
		};
	}

	@Unique
	private int skillslotsaddon$visibleCount(int maxSlots) {
		if (AddonConfig.hideLockedSlots() && handler != null) {
			return Math.min(maxSlots, handler.getContainerSize());
		}
		return maxSlots;
	}

	@Unique
	private boolean skillslotsaddon$isHidden(int index) {
		return AddonConfig.hideLockedSlots() && handler != null && index >= handler.getContainerSize();
	}

	/**
	 * Optional item tooltip on hover (the release jar does not draw it; this
	 * restores the behaviour that was previously provided by a Hotai patch).
	 * Runs at the start of render's else branch (clickIndex >= 0), right before
	 * the original empty-slot tooltip logic.
	 */
	@Inject(
			method = "m_88315_",
			at = @At(
					value = "FIELD",
					target = "Lsnownee/skillslots/client/gui/UseScreen;handler:Lsnownee/skillslots/SkillSlotsHandler;",
					ordinal = 1,
					shift = At.Shift.BEFORE))
	private void skillslotsaddon$renderItemTooltip(GuiGraphics graphics, int mouseX, int mouseY, float pTicks, CallbackInfo ci) {
		if (!AddonConfig.showItemTooltips() || handler == null || clickIndex < 0) {
			return;
		}
		Skill skill = handler.skills.get(clickIndex);
		if (!skill.isEmpty()) {
			graphics.renderTooltip(Minecraft.getInstance().font, skill.item, mouseX, mouseY);
		}
	}

	/**
	 * SkillSlots draws the skill display name directly on each wheel button.
	 * Draw escaped or parsed newlines as separate centered rows at that call site.
	 */
	@Redirect(
			method = "drawButton",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;m_280653_(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
	private void skillslotsaddon$drawMultilineSkillName(
			GuiGraphics graphics, Font font, Component text, int x, int y, int color) {
		String name = text.getString().replace("\\n", "\n").replace("\r\n", "\n");
		if (!name.contains("\n")) {
			graphics.drawCenteredString(font, text, x, y, color);
			return;
		}

		String[] lines = name.split("\n", -1);
		int lineSpacing = font.lineHeight + 2;
		int firstLineY = y - (lines.length - 1) * lineSpacing / 2;
		for (int i = 0; i < lines.length; i++) {
			Component line = Component.literal(lines[i]).setStyle(text.getStyle());
			graphics.drawCenteredString(font, line, x, firstLineY + i * lineSpacing, color);
		}
	}

	/**
	 * Positional keyboard shortcuts: keys 1-4 always target the top row and
	 * keys 5-8 the bottom row, regardless of how many slots are unlocked.
	 * Empty slots behave like a mouse click (nothing happens, wheel stays open).
	 */
	@Inject(method = "m_7933_", at = @At("HEAD"), cancellable = true)
	private void skillslotsaddon$positionalShortcuts(int key, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (handler == null || closing || useIndex != -1) {
			return;
		}
		InputConstants.Key pressedKey = InputConstants.getKey(key, scanCode);
		for (int k = 0; k < SkillSlotsAddonKeys.GUI_USES.length; k++) {
			KeyMapping keyMapping = SkillSlotsAddonKeys.GUI_USES[k];
			if (keyMapping != null && keyMapping.getKey().equals(pressedKey)) {
				int slot = SkillSlotsAddonKeys.slotForKey(k, SkillSlotsCommonConfig.maxSlots, handler.getContainerSize());
				if (slot >= 0 && slot < handler.getContainerSize() && !handler.skills.get(slot).isEmpty()) {
					startUse(slot);
				}
				cir.setReturnValue(true);
				return;
			}
		}
	}

	/**
	 * Shows the bound key's conventional name (1, A, Ctrl, Shift...) above each
	 * unlocked slot. A dedicated translation key
	 * "key.skillslots_addon.display.<glfw key name>" can override the text for a
	 * single key without touching the global keybind localization.
	 */
	@Inject(method = "drawButton", at = @At("TAIL"))
	private void skillslotsaddon$renderKeyHint(GuiGraphics graphics, float xCenter, float yCenter, int mouseX, int mouseY, int index, float pTicks, CallbackInfo ci) {
		if (handler == null || closing || index >= handler.getContainerSize()) {
			return;
		}
		int keyIndex = SkillSlotsAddonKeys.keyIndexForSlot(index, SkillSlotsCommonConfig.maxSlots, handler.getContainerSize());
		if (keyIndex < 0 || keyIndex >= SkillSlotsAddonKeys.GUI_USES.length) {
			return;
		}
		KeyMapping keyMapping = SkillSlotsAddonKeys.GUI_USES[keyIndex];
		if (keyMapping == null) {
			return;
		}
		int alpha = (int) (openTick * 255);
		if (alpha <= 0) {
			return;
		}
		int color = alpha << 24 | 0xFFFFFF;
		graphics.pose().pushPose();
		graphics.pose().translate(xCenter, yCenter - 32.0F, 0);
		graphics.pose().scale(0.8F, 0.8F, 1.0F);
		graphics.drawCenteredString(Minecraft.getInstance().font, skillslotsaddon$keyLabel(keyMapping), 0, 0, color);
		graphics.pose().popPose();
	}

	@Unique
	private static String skillslotsaddon$keyLabel(KeyMapping keyMapping) {
		InputConstants.Key key = keyMapping.getKey();
		if (key.getType() == InputConstants.Type.KEYSYM) {
			String name = key.getName();
			if (name.startsWith("key.keyboard.")) {
				String suffix = name.substring("key.keyboard.".length());
				String override = "key.skillslots_addon.display." + suffix;
				if (I18n.exists(override)) {
					return I18n.get(override);
				}
				return skillslotsaddon$prettifyKey(suffix);
			}
		}
		return key.getDisplayName().getString();
	}

	@Unique
	private static String skillslotsaddon$prettifyKey(String name) {
		return switch (name) {
			case "left.control" -> "Ctrl";
			case "right.control" -> "RCtrl";
			case "left.shift" -> "Shift";
			case "right.shift" -> "RShift";
			case "left.alt" -> "Alt";
			case "right.alt" -> "RAlt";
			case "left.super" -> "Win";
			case "right.super" -> "RWin";
			case "space" -> "Space";
			case "tab" -> "Tab";
			case "caps.lock" -> "Caps";
			case "enter" -> "Enter";
			case "backspace" -> "Bksp";
			case "delete" -> "Del";
			case "escape" -> "Esc";
			case "insert" -> "Ins";
			case "home" -> "Home";
			case "end" -> "End";
			case "page.up" -> "PgUp";
			case "page.down" -> "PgDn";
			case "up" -> "Up";
			case "down" -> "Down";
			case "left" -> "Left";
			case "right" -> "Right";
			default -> {
				if (name.matches("f\\d+") || name.length() == 1) {
					yield name.toUpperCase(Locale.ROOT);
				}
				yield name;
			}
		};
	}
}
