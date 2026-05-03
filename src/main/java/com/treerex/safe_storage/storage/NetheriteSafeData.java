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

public class NetheriteSafeData extends SavedData {
    private static final String DATA_NAME = "safe_storage_netherite_safe_data";
    private final Map<UUID, SafeInventory> safeMap = new HashMap<>();

    public NetheriteSafeData() {
        this(new HashMap<>());
    }

    public NetheriteSafeData(Map<UUID, SafeInventory> safeMap) {
        this.safeMap.clear();
        this.safeMap.putAll(safeMap);
    }

    public static NetheriteSafeData load(CompoundTag tag, HolderLookup.Provider provider) {
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

        return new NetheriteSafeData(safeMap);
    }

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag safeList = new ListTag();

        for (Map.Entry<UUID, SafeInventory> entry : this.safeMap.entrySet()) {
            CompoundTag safesTag = new CompoundTag();
            safesTag.putUUID("Owner", entry.getKey());
            safesTag.putInt("SafeSize", entry.getValue().getContainerSize());
            safesTag.put("Safe", entry.getValue().of());
            safeList.add(safesTag);
        }

        tag.put("safes", safeList);
        return tag;
    }

    public SafeInventory getInventoryFromUUID(UUID uuid) {
        SafeInventory inventory = this.safeMap.get(uuid);
        if (inventory == null) {
            inventory = new SafeInventory(54);
            this.safeMap.put(uuid, inventory);
        }
        return inventory;
    }

    public void setDirty() {
        super.setDirty();
        if (Reference.safeDataStorage != null) {
            Reference.safeDataStorage.save();
        }
    }

    public static NetheriteSafeData get(Level level) {
        if (!(level instanceof ServerLevel)) {
            throw new RuntimeException("Attempted to get the data from a client level. This is wrong.");
        }
        return (NetheriteSafeData) Reference.getVaultDataStorage(level.getServer())
                .computeIfAbsent(new SavedData.Factory<>(NetheriteSafeData::new, NetheriteSafeData::load), DATA_NAME);
    }
}