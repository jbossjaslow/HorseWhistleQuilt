package io.github.jbossjaslow.horse_whistle.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class NBTUtil {
	/**
	 * @param stack The item stack to which NBT is applied
	 * @param key The key for the NBT data
	 * @param value The value for the NBT data
	 */
	public static void writeNBTTo(ItemStack stack, String key, String value) {
		NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
		if (nbt == null) return;
		nbt.putString(key, value);
		stack.setNbt(nbt);
	}

	public static void removeNBTFrom(ItemStack stack, String key) {
		stack.removeSubNbt(key);
	}

	/**
	 * @param stack The item stack to which NBT was applied
	 * @param key The key associated with the NBT data
	 * @return The value from the NBT. Empty string if not present
	 */
	public static String getNBTFrom(ItemStack stack, String key) {
		if (stack.hasNbt())
			return stack.getNbt().getString(key);
		else
			return "";
	}

	public static boolean hasNBTFor(ItemStack stack, String key) {
		if (stack.hasNbt())
			return !stack.getNbt().getString(key).isEmpty();
		else
			return false;
	}
}
