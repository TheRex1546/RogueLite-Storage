package com.treerex.rlstorageexp.datagen;

import com.treerex.rlstorageexp.RogueliteStorageExpandedMod;
import com.treerex.rlstorageexp.registry.RLRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PatchDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new Loots(packOutput, lookupProvider));
		}
		if (event.includeClient()) {
			generator.addProvider(event.includeClient(), new Language(packOutput));
		}
	}

	private static class Loots extends LootTableProvider {
		public Loots(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(packOutput, Set.of(), List.of(
					new SubProviderEntry(PatchBlockLoot::new, LootContextParamSets.BLOCK)
			), completableFuture);
		}

		public static class PatchBlockLoot extends BlockLootSubProvider {
			protected PatchBlockLoot(HolderLookup.Provider provider) {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
			}

			@Override
			protected void generate() {
				dropSelf(RLRegistry.SAFE.get());
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) RLRegistry.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
			}
		}

		@Override
		protected void validate(WritableRegistry<LootTable> writableRegistry, ValidationContext validationContext,
		                        ProblemReporter.Collector collector) {
			super.validate(writableRegistry, validationContext, collector);
		}
	}

	private static class Language extends LanguageProvider {
		public Language(PackOutput packOutput) {
			super(packOutput, RogueliteStorageExpandedMod.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.addBlock(RLRegistry.SAFE, "Safe");
			this.add("roguelite_storage_expanded.container.safe", "Safe");
		}
	}
}