package io.github.jbossjaslow.horse_whistle.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.List;

public class HorseWhistleItem extends Item {
	private static final String HORSE_TAG_KEY = "associatedHorse";

	public HorseWhistleItem(QuiltItemSettings settings) {
		super(
			settings
				.maxDamage(16)
				.rarity(Rarity.RARE)
		);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		super.use(world, user, hand);

		ItemStack stack = user.getStackInHand(hand);

		if (user.getWorld().isClient()) { // We cannot be on the client to check the UUID of the player
			return TypedActionResult.fail(stack);
		}

		if (user.getPose() == EntityPose.CROUCHING) {
			removeHorseNBTFrom(stack);
			return TypedActionResult.consume(stack);
		}

		if (!getHorseNBTFrom(stack).isEmpty()) {
			stack.setCooldown(3);
			stack.damage(1, user, (p) -> {
				p.sendToolBreakStatus(hand);
			});

			String associatedHorseIdString = getHorseNBTFrom(stack);
			double radius = 100;
			double xPos = user.getX();
			double yPos = user.getY();
			double zPos = user.getZ();
			Box searchArea = new Box(xPos - radius, yPos - radius, zPos - radius, xPos + radius, yPos + radius, zPos + radius);
			List<HorseEntity> horses = world.getEntitiesByType(EntityType.HORSE, searchArea, EntityPredicates.VALID_LIVING_ENTITY);
			for (HorseEntity h : horses) {
				if (h.getUuidAsString().equals(associatedHorseIdString)) {
					teleportHorse(h, user, world);
					return TypedActionResult.consume(stack);
				}
			}
		}

//		user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 0.5f, 1f);
		return TypedActionResult.fail(stack);
	}

	private void teleportHorse(HorseEntity horse, PlayerEntity player, World world) {
		double xPos = player.getX();
		double yPos = player.getY();
		double zPos = player.getZ();
		// make teleport random, ensure it teleports to a valid block

		double tr = 5; // teleport radius
		horse.teleport(xPos + tr, yPos, zPos + tr);
		world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.MASTER, 1f, 1f);
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		super.useOnEntity(stack, user, entity, hand);
		stack.setCooldown(3);

		if (user.getWorld().isClient()) { // We cannot be on the client to check the UUID of the player
			return ActionResult.FAIL;
		}

		if (entity.getType() != EntityType.HORSE || !getHorseNBTFrom(stack).isEmpty()) {
			return ActionResult.PASS;
		}

		HorseEntity horseEntity = (HorseEntity) entity;

		if (horseEntity.isTame() && horseEntity.getOwnerUuid() == user.getUuid()) {
			user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 0.5f, 0.5f);

			writeHorseNBTTo(stack, horseEntity);

			return ActionResult.success(false);
		} else {
			return ActionResult.PASS;
		}
	}

	private void writeHorseNBTTo(ItemStack stack, HorseEntity horse) {
		NbtCompound nbt = new NbtCompound();
		nbt.putString(HORSE_TAG_KEY, horse.getUuidAsString());
		stack.setNbt(nbt);
	}

	private void removeHorseNBTFrom(ItemStack stack) {
		stack.removeSubNbt(HORSE_TAG_KEY);
	}

	private String getHorseNBTFrom(ItemStack stack) {
		if (stack.hasNbt())
			return stack.getNbt().getString(HORSE_TAG_KEY);
        else
			return "";
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);

		if (!getHorseNBTFrom(stack).isEmpty()) {
			tooltip.add(Text.translatable("Attuned to horse").formatted(Formatting.GRAY));
		}
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return !getHorseNBTFrom(stack).isEmpty();
	}
}
