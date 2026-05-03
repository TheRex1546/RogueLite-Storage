package com.treerex.safe_storage.storage;

import com.treerex.safe_storage.Reference;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IronSafeData extends SavedData {
	private static final String DATA_NAME = "iron_safe";
	private final Map<UUID, SafeInventory> safeMap = new HashMap<>();

	public IronSafeData() {
		this(new HashMap<>());
	}

	public IronSafeData(Map<UUID, SafeInventory> safeMap) {
		this.safeMap.clear();
		this.safeMap.putAll(safeMap);
	}

	public static IronSafeData load(CompoundTag tag, HolderLookup.Provider provider) {
		ListTag safesList = tag.getList("safes", 10);
		Map<UUID, SafeInventory> safeMap = new HashMap<>();

		for (int i = 0; i < safesList.size(); ++i) {
			CompoundTag listTag = safesList.getCompound(i);
			UUID uuid = listTag.getUUID("Owner");
			ListTag safeTag = listTag.getList("Safe", 10);
			SafeInventory inventory = new SafeInventory(9);
			inventory.fromTag(safeTag);
			safeMap.put(uuid, inventory);
		}

		return new IronSafeData(safeMap);
	}

	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
		ListTag safeList = new ListTag();

		for (Map.Entry<UUID, SafeInventory> entry : this.safeMap.entrySet()) {
			CompoundTag safesTag = new CompoundTag();
			safesTag.putUUID("Owner", entry.getKey());
			safesTag.put("Safe", entry.getValue().of());
			safeList.add(safesTag);
		}

		tag.put("safes", safeList);
		return tag;
	}

	public SafeInventory getInventoryFromUUID(UUID uuid) {
		return this.safeMap.computeIfAbsent(uuid, ignored -> new SafeInventory(9));
	}

	public void setDirty() {
		super.setDirty();
		if (Reference.safeDataStorage != null) {
			Reference.safeDataStorage.save();
		}
	}

	public static IronSafeData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client level. This is wrong.");
		} else {
			return (IronSafeData) Reference.getVaultDataStorage(level.getServer())
					.computeIfAbsent(new SavedData.Factory<>(IronSafeData::new, IronSafeData::load), DATA_NAME);
		}
	}
}
