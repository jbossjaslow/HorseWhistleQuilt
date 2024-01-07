package io.github.jbossjaslow.horse_whistle.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HotBarUtil {
	public static void displayActionBarText(String text) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.inGameHud.setOverlayMessage(Text.translatable(text).formatted(Formatting.GREEN), false);
	}
}
