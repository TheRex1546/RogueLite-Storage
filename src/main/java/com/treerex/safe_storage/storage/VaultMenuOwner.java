package com.treerex.safe_storage.storage;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface VaultMenuOwner {
	boolean stillValid(Player player);

	void markDirty(Level level);
}
