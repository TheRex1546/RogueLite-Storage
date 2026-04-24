package com.treerex.rlstorageexp.storage;

import com.treerex.rlstorageexp.Reference;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SafeData extends SavedData {
	private static final String DATA_NAME = "roguelitestorage_safe_data";
	private final Map<UUID, SafeInventory> safeMap = new HashMap<>();

	public SafeData() {
		this(new HashMap<>());
	}

	public SafeData(Map<UUID, SafeInventory> safeMap) {
		this.safeMap.clear();
		this.safeMap.putAll(safeMap);
	}

	public static SafeData load(CompoundTag tag, HolderLookup.Provider provider) {
		ListTag safesList = tag.getList("safes", 10);
		Map<UUID, SafeInventory> safeMap = new HashMap<>();

		for (int i = 0; i < safesList.size(); ++i) {
			CompoundTag listTag = safesList.getCompound(i);
			UUID uuid = listTag.getUUID("Owner");
			int safeSize = listTag.getInt("SafeSize");
			ListTag safeTag = listTag.getList("Safe", 10);
			SafeInventory inventory = new SafeInventory(safeSize);
			inventory.fromTag(safeTag);
			safeMap.put(uuid, inventory);
		}

		return new SafeData(safeMap);
	}

	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
		ListTag safeList = new ListTag();

		for (Map.Entry<UUID, SafeInventory> entry : this.safeMap.entrySet()) {
			CompoundTag safesTag = new CompoundTag();
			safesTag.putUUID("Owner", entry.getKey());
			safesTag.putInt("SafeSize", (entry.getValue()).getContainerSize());
			safesTag.put("Safe", (entry.getValue()).of());
			safeList.add(safesTag);
		}

		tag.put("safes", safeList);
		return tag;
	}

	public SafeInventory getInventoryFromUUID(UUID uuid) {
		return this.safeMap.containsKey(uuid) ? this.safeMap.get(uuid) : this.safeMap.put(uuid, new SafeInventory(27));
	}

	public void setDirty() {
		super.setDirty();
		if (Reference.safeDataStorage != null) {
			Reference.safeDataStorage.save();
		}

	}

	public static SafeData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client level. This is wrong.");
		} else {
			return (SafeData) Reference.getVaultDataStorage(level.getServer())
					.computeIfAbsent(new SavedData.Factory<>(SafeData::new, SafeData::load), DATA_NAME);
		}
	}
}