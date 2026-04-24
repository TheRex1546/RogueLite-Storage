package com.treerex.rlstorageexp.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.BiFunction;

public class SafeDataStorage {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, SavedData> cache = Maps.newHashMap();
	private final DataFixer fixerUpper;
	private final File dataFolder;
	private final HolderLookup.Provider registries;

	public SafeDataStorage(File file, DataFixer dataFixer, HolderLookup.Provider provider) {
		this.fixerUpper = dataFixer;
		this.dataFolder = file;
		this.registries = provider;
	}

	private File getDataFile(String name) {
		return new File(this.dataFolder, name + ".dat");
	}

	public <T extends SavedData> T computeIfAbsent(SavedData.Factory<T> factory, String id) {
		T t = this.get(factory, id);
		if (t != null) {
			return t;
		} else {
			T t1 = factory.constructor().get();
			this.set(id, t1);
			return t1;
		}
	}

	@Nullable
	public <T extends SavedData> T get(SavedData.Factory factory, String id) {
		SavedData saveddata = this.cache.get(id);
		if (saveddata == net.neoforged.neoforge.common.util.DummySavedData.DUMMY) return null;
		if (saveddata == null && !this.cache.containsKey(id)) {
			saveddata = this.readSavedData(factory.deserializer(), factory.type(), id);
			this.cache.put(id, saveddata);
		} else if (saveddata == null) {
			this.cache.put(id, net.neoforged.neoforge.common.util.DummySavedData.DUMMY);
			return null;
		}
		return (T) saveddata;
	}

	@Nullable
	private SavedData readSavedData(BiFunction<CompoundTag, HolderLookup.Provider, SavedData> function, @Nullable DataFixTypes fixTypes, String id) {
		try {
			File file1 = this.getDataFile(id);
			if (file1.exists()) {
				CompoundTag compoundtag = this.readTagFromDisk(id, fixTypes, SharedConstants.getCurrentVersion().getDataVersion().getVersion());
				return function.apply(compoundtag.getCompound("data"), this.registries);
			}
		} catch (Exception var5) {
			LOGGER.error("Error loading saved data: {}", id, var5);
		}

		return null;
	}

	public void set(String name, SavedData savedData) {
		this.cache.put(name, savedData);
	}

	public CompoundTag readTagFromDisk(String id, @Nullable DataFixTypes fixTypes, int levelVersion) throws IOException {
		File file1 = this.getDataFile(id);

		CompoundTag compoundtag1;
		try (
				FileInputStream fileinputstream = new FileInputStream(file1);
				PushbackInputStream pushbackinputstream = new PushbackInputStream(fileinputstream, 2)
		) {
			CompoundTag compoundtag;
			if (this.isGzip(pushbackinputstream)) {
				compoundtag = NbtIo.readCompressed(pushbackinputstream, NbtAccounter.unlimitedHeap());
			} else {
				try (DataInputStream datainputstream = new DataInputStream(pushbackinputstream)) {
					compoundtag = NbtIo.read(datainputstream);
				}
			}

			if (fixTypes != null) {
				int i = NbtUtils.getDataVersion(compoundtag, 1343);
				compoundtag1 = fixTypes.update(this.fixerUpper, compoundtag, i, levelVersion);
			} else {
				compoundtag1 = compoundtag;
			}
		}

		return compoundtag1;
	}

	private boolean isGzip(PushbackInputStream inputStream) throws IOException {
		byte[] abyte = new byte[2];
		boolean flag = false;
		int i = inputStream.read(abyte, 0, 2);
		if (i == 2) {
			int j = (abyte[1] & 255) << 8 | abyte[0] & 255;
			if (j == 35615) {
				flag = true;
			}
		}

		if (i != 0) {
			inputStream.unread(abyte, 0, i);
		}

		return flag;
	}

	public void save() {
		this.cache.forEach((id, data) -> {
			if (data != null) {
				data.save(this.getDataFile(id), this.registries);
			}
		});
	}
}