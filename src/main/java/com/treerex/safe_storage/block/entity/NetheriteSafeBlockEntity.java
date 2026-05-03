package com.treerex.safe_storage.block.entity;

import com.treerex.safe_storage.registry.RLRegistry;
import com.treerex.safe_storage.storage.NetheriteSafeData;
import com.treerex.safe_storage.storage.SafeInventory;
import com.treerex.safe_storage.storage.VaultMenuOwner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class NetheriteSafeBlockEntity extends BlockEntity implements VaultMenuOwner {
    public NetheriteSafeBlockEntity(BlockPos pos, BlockState state) {
        super(RLRegistry.NETHERITE_SAFE_BLOCK_ENTITY.get(), pos, state);
    }

    public Component getDisplayName() {
        return Component.literal("Netherite Safe");
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

        return getVaultData(level).getInventoryFromUUID(uuid);
    }

    public NetheriteSafeData getVaultData(Level level) {
        NetheriteSafeData data = NetheriteSafeData.get(level);
        data.setDirty();
        return data;
    }

    @Override
    public void markDirty(Level level) {
        this.getVaultData(level).setDirty();
    }
}