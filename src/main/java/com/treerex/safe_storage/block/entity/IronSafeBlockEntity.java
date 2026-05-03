package com.treerex.safe_storage.block.entity;

import com.treerex.safe_storage.registry.RLRegistry;
import com.treerex.safe_storage.storage.IronSafeData;
import com.treerex.safe_storage.storage.SafeInventory;
import com.treerex.safe_storage.storage.VaultMenuOwner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class IronSafeBlockEntity extends BlockEntity implements VaultMenuOwner {
	public IronSafeBlockEntity(BlockPos pos, BlockState state) {
		super(RLRegistry.IRON_SAFE_BLOCK_ENTITY.get(), pos, state);
	}

	public Component getDisplayName() {
		return Component.literal("Iron Safe");
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
		return inventory;
	}

	public IronSafeData getVaultData(Level level) {
		IronSafeData data = IronSafeData.get(level);
		data.setDirty();
		return data;
	}

	@Override
	public void markDirty(Level level) {
		this.getVaultData(level).setDirty();
	}
}
