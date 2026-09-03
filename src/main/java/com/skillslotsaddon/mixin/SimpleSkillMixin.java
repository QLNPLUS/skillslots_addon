package com.skillslotsaddon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import snownee.skillslots.skill.SimpleSkill;
import com.skillslotsaddon.AddonConfig;

/**
 * Config-gated right-click triggers inside SimpleSkill.finishUsing: block use,
 * entity interact, and plain item use can each be turned off from the config.
 */
@Mixin(value = SimpleSkill.class, remap = false)
public abstract class SimpleSkillMixin {

	@Redirect(
			method = "finishUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;m_41661_(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult skillslotsaddon$blockRightClickClient(ItemStack stack, UseOnContext context) {
		return AddonConfig.blockRightClickEnabled() ? stack.useOn(context) : InteractionResult.PASS;
	}

	@Redirect(
			method = "finishUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;m_7179_(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult skillslotsaddon$blockRightClickServer(
			ServerPlayerGameMode gameMode, ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hit) {
		return AddonConfig.blockRightClickEnabled() ? gameMode.useItemOn(player, level, stack, hand, hit) : InteractionResult.PASS;
	}

	@Redirect(
			method = "finishUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;m_5797_(Lnet/minecraft/network/protocol/game/ServerGamePacketListener;)V"))
	private void skillslotsaddon$entityRightClick(ServerboundInteractPacket packet, net.minecraft.network.protocol.game.ServerGamePacketListener listener) {
		if (AddonConfig.entityRightClickEnabled()) {
			packet.handle(listener);
		}
	}

	@Redirect(
			method = "finishUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;m_41682_(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
	private InteractionResultHolder<ItemStack> skillslotsaddon$itemRightClickClient(
			ItemStack stack, Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
		return AddonConfig.itemRightClickEnabled() ? stack.use(level, player, hand) : InteractionResultHolder.pass(stack);
	}

	@Redirect(
			method = "finishUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayerGameMode;m_6261_(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult skillslotsaddon$itemRightClickServer(
			ServerPlayerGameMode gameMode, ServerPlayer player, Level level, ItemStack stack, InteractionHand hand) {
		return AddonConfig.itemRightClickEnabled() ? gameMode.useItem(player, level, stack, hand) : InteractionResult.PASS;
	}

	@Redirect(
			method = "finishUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;m_41671_(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack skillslotsaddon$finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		return AddonConfig.itemRightClickEnabled() ? stack.finishUsingItem(level, entity) : stack;
	}
}
