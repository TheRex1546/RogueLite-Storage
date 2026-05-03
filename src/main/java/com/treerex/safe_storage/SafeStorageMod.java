package com.treerex.safe_storage;

import com.mojang.logging.LogUtils;
import com.treerex.safe_storage.datagen.PatchDatagen;
import com.treerex.safe_storage.registry.RLRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod("safe_storage")
public class SafeStorageMod {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MOD_ID = "safe_storage";

	public SafeStorageMod(IEventBus modEventBus) {
		RLRegistry.BLOCKS.register(modEventBus);
		RLRegistry.BLOCK_ENTITIES.register(modEventBus);
		RLRegistry.ITEMS.register(modEventBus);

		modEventBus.addListener(this::buildCreativeContents);
		modEventBus.addListener(PatchDatagen::gatherData);
	}

	private void buildCreativeContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(new ItemStack(RLRegistry.IRON_SAFE_ITEM.get()));
			event.accept(new ItemStack(RLRegistry.DIAMOND_SAFE_ITEM.get()));
			event.accept(new ItemStack(RLRegistry.NETHERITE_SAFE_ITEM.get()));
		}
	}
}
