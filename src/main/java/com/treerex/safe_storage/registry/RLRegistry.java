package com.treerex.safe_storage.registry;

import com.treerex.safe_storage.SafeStorageMod;
import com.treerex.safe_storage.block.DiamondSafeBlock;
import com.treerex.safe_storage.block.IronSafeBlock;
import com.treerex.safe_storage.block.entity.DiamondSafeBlockEntity;
import com.treerex.safe_storage.block.entity.IronSafeBlockEntity;
import com.treerex.safe_storage.block.NetheriteSafeBlock;
import com.treerex.safe_storage.block.entity.NetheriteSafeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RLRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SafeStorageMod.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SafeStorageMod.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SafeStorageMod.MOD_ID);

	public static final DeferredBlock<IronSafeBlock> IRON_SAFE = BLOCKS.register("iron_safe", () ->
			new IronSafeBlock(Properties.ofFullCopy(Blocks.ANVIL).requiresCorrectToolForDrops()
					.strength(5.0F, 1200.0F).sound(SoundType.ANVIL)));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IronSafeBlockEntity>> IRON_SAFE_BLOCK_ENTITY = BLOCK_ENTITIES.register("iron_safe", () ->
			Builder.of(IronSafeBlockEntity::new, IRON_SAFE.get()).build(null));
	public static final DeferredItem<BlockItem> IRON_SAFE_ITEM = ITEMS.register("iron_safe", () ->
			new BlockItem(IRON_SAFE.get(), new Item.Properties()));

	public static final DeferredBlock<DiamondSafeBlock> DIAMOND_SAFE = BLOCKS.register("diamond_safe", () ->
			new DiamondSafeBlock(Properties.ofFullCopy(Blocks.ANVIL).requiresCorrectToolForDrops()
					.strength(5.0F, 1200.0F).sound(SoundType.ANVIL)));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DiamondSafeBlockEntity>> DIAMOND_SAFE_BLOCK_ENTITY = BLOCK_ENTITIES.register("diamond_safe", () ->
			Builder.of(DiamondSafeBlockEntity::new, DIAMOND_SAFE.get()).build(null));
	public static final DeferredItem<BlockItem> DIAMOND_SAFE_ITEM = ITEMS.register("diamond_safe", () ->
			new BlockItem(DIAMOND_SAFE.get(), new Item.Properties()));

	public static final DeferredBlock<NetheriteSafeBlock> NETHERITE_SAFE = BLOCKS.register("netherite_safe", () ->
			new NetheriteSafeBlock(Properties.ofFullCopy(Blocks.ANVIL).requiresCorrectToolForDrops()
					.strength(5.0F, 1200.0F).sound(SoundType.ANVIL)));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NetheriteSafeBlockEntity>> NETHERITE_SAFE_BLOCK_ENTITY = BLOCK_ENTITIES.register("netherite_safe", () ->
			Builder.of(NetheriteSafeBlockEntity::new, NETHERITE_SAFE.get()).build(null));
	public static final DeferredItem<BlockItem> NETHERITE_SAFE_ITEM = ITEMS.register("netherite_safe", () ->
			new BlockItem(NETHERITE_SAFE.get(), new Item.Properties()));
}
