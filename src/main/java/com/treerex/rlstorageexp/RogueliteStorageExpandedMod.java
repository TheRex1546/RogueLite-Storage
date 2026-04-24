package com.treerex.rlstorageexp;


import com.mojang.logging.LogUtils;
import com.treerex.rlstorageexp.registry.RLRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod("roguelite_storage_expanded")
public class RogueliteStorageExpandedMod {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MOD_ID = "roguelite_storage_expanded";

	public RogueliteStorageExpandedMod(IEventBus modEventBus) {
		RLRegistry.BLOCKS.register(modEventBus);
		RLRegistry.BLOCK_ENTITIES.register(modEventBus);
		RLRegistry.ITEMS.register(modEventBus);

		modEventBus.addListener(this::buildCreativeContents);
	}

	private void buildCreativeContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			event.accept(new ItemStack(RLRegistry.SAFE_ITEM.get()));
		}
	}
}


