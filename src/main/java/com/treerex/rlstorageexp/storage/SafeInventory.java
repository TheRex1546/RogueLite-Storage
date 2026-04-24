package com.treerex.rlstorageexp.storage;

import com.treerex.rlstorageexp.block.entity.SafeBlockEntity;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SafeInventory extends SimpleContainer {
	private SafeBlockEntity associatedVault;

	public SafeInventory(int slots) {
		super(slots);
	}

	public void setAssociatedVault(SafeBlockEntity safeBlockEntity) {
		this.associatedVault = safeBlockEntity;
	}

	public void fromTag(ListTag listTag) {
		int k;
		for (k = 0; k < this.getContainerSize(); ++k) {
			this.setItem(k, ItemStack.EMPTY);
		}

		for (k = 0; k < listTag.size(); ++k) {
			CompoundTag compoundTag = listTag.getCompound(k);
			int j = compoundTag.getByte("Slot") & 255;
			if (j >= 0 && j < this.getContainerSize()) {
				this.setItem(j, ItemStack.parse(VanillaRegistries.createLookup(), compoundTag).orElse(ItemStack.EMPTY));
			}
		}

	}

	public ListTag of() {
		var lookup = VanillaRegistries.createLookup();
		ListTag listTag = new ListTag();

		for (int i = 0; i < this.getContainerSize(); ++i) {
			ItemStack itemstack = this.getItem(i);
			if (!itemstack.isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte) i);
				listTag.add(itemstack.save(lookup, compoundTag));
				listTag.add(compoundTag);
			}
		}

		return listTag;
	}

	public boolean stillValid(Player player) {
		return (this.associatedVault == null || this.associatedVault.stillValid(player)) && super.stillValid(player);
	}

	public void startOpen(Player player) {
		super.startOpen(player);
	}

	public void stopOpen(Player player) {
		super.stopOpen(player);
		if (!player.level().isClientSide) {
			SafeData.get(player.level()).setDirty();
		}

		this.associatedVault = null;
	}

	public boolean addItemStackToInventory(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		} else {
			int slot = this.getFirstEmptyStack();
			if (slot >= 0) {
				this.setItem(slot, stack.copy());
				stack.setCount(0);
				return true;
			} else {
				return false;
			}
		}
	}

	public int getFirstEmptyStack() {
		for (int i = 0; i < this.getContainerSize(); ++i) {
			if (this.getItem(i).isEmpty()) {
				return i;
			}
		}

		return -1;
	}
}