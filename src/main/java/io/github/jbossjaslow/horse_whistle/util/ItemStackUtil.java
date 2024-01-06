package io.github.jbossjaslow.horse_whistle.util;

import net.minecraft.item.ItemStack;

public class ItemStackUtil {
	public static boolean isItemEqual(ItemStack i1, ItemStack i2) {
		return i1.getItem().equals(i2.getItem());
	}
}
