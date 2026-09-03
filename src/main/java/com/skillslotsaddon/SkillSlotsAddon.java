package com.skillslotsaddon;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(SkillSlotsAddon.MOD_ID)
public final class SkillSlotsAddon {

	public static final String MOD_ID = "skillslots_addon";
	public static final Logger LOGGER = LogUtils.getLogger();

	public SkillSlotsAddon() {
		AddonConfig.register();

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(AddonConfig::onModConfig);
		modBus.addListener(this::commonSetup);
		modBus.addListener(this::clientSetup);

		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::playerLoggedIn);

		if (ModList.get().isLoaded("kubejs")) {
			registerKubeJsIntegration();
		}
	}

	private static void registerKubeJsIntegration() {
		try {
			Class.forName("com.skillslotsaddon.kubejs.KubeJsGuiBridge")
					.getMethod("register")
					.invoke(null);
			LOGGER.info("Enabled SkillSlots Addon KubeJS integration");
		} catch (ReflectiveOperationException exception) {
			LOGGER.error("Failed to enable SkillSlots Addon KubeJS integration", exception);
		}
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(AddonConfig::apply);
	}

	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			AddonConfig.apply();
			AddonClientEvents.register();
		});
	}

	private void serverStarting(ServerAboutToStartEvent event) {
		AddonConfig.apply();
	}

	private void playerLoggedIn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
		AddonConfig.apply();
	}
}
