package com.treerex.safe_storage.block.entity;

import com.treerex.safe_storage.registry.RLRegistry;
import com.treerex.safe_storage.storage.DiamondSafeData;
import com.treerex.safe_storage.storage.SafeInventory;
import com.treerex.safe_storage.storage.VaultMenuOwner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class DiamondSafeBlockEntity extends BlockEntity implements VaultMenuOwner {
	public DiamondSafeBlockEntity(BlockPos pos, BlockState state) {
		super(RLRegistry.DIAMOND_SAFE_BLOCK_ENTITY.get(), pos, state);
	}

	public Component getDisplayName() {
		return Component.literal("Diamond Safe");
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
		return this.getVaultData(level).getInventoryFromUUID(uuid);
	}

	public DiamondSafeData getVaultData(Level level) {
		DiamondSafeData data = DiamondSafeData.get(level);
		data.setDirty();
		return data;
	}

	@Override
	public void markDirty(Level level) {
		this.getVaultData(level).setDirty();
	}
}
