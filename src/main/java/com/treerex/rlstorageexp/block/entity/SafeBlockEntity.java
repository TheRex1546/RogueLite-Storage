package com.treerex.rlstorageexp.block.entity;

import com.treerex.rlstorageexp.registry.RLRegistry;
import com.treerex.rlstorageexp.storage.SafeData;
import com.treerex.rlstorageexp.storage.SafeInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class SafeBlockEntity extends BlockEntity {
	public SafeBlockEntity(BlockPos pos, BlockState state) {
		super(RLRegistry.SAFE_BLOCK_ENTITY.get(), pos, state);
	}

	public Component getDisplayName() {
		return Component.translatable("Safe");
	}

	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr(
					(double) this.worldPosition.getX() + 0.5D,
					(double) this.worldPosition.getY() + 0.5D,
					(double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	public SafeInventory getInventory(UUID uuid, Level level) {
		if (level.isClientSide) {
			return null;
		}
		SafeInventory inventory = this.getVaultData(level).getInventoryFromUUID(uuid);
		if (inventory == null) {
			//Usually happens the first time the vault nbt file is created
			inventory = this.getVaultData(level).getInventoryFromUUID(uuid);
		}
		return inventory;
	}

	public SafeData getVaultData(Level level) {
		SafeData data = SafeData.get(level);
		data.setDirty();
		return data;
	}
}