package com.alfred.modifiers.mixin;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.ItemModifierRegistry;
import com.alfred.modifiers.ModifiersConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

import static net.minecraft.item.BowItem.getPullProgress;

@SuppressWarnings("unused")
public abstract class ItemsMixin {
	@Mixin(Item.class)
	public abstract static class ItemMixin {
		@Inject(method = "onCraft", at = @At("RETURN"))
		private void applyRandomModifier(ItemStack stack, World world, CallbackInfo ci) {
			if (Math.random() < ModifiersConfig.getInstance().craftedItemModifierChance && !(stack.hasNbt() && stack.getNbt().contains(Constants.HAS_MODIFIER) && stack.getNbt().getBoolean(Constants.HAS_MODIFIER))) {
				// get a random modifier that's applicable to this item
				ItemModifier randomModifier = ItemModifierRegistry.getRandomModifier(stack);

				if (randomModifier != null)
					randomModifier.applyModifier(stack);
				else
					stack.getOrCreateNbt().putBoolean(Constants.HAS_MODIFIER, true); // technically doesn't "have" a modifier, but this is here so that the item cannot obtain a modifier through natural means later
			}
		}
	}

	@Mixin(ItemStack.class)
	public abstract static class ItemStackMixin {
		@ModifyReturnValue(method = "getName", at = @At("RETURN")
		private void applyModifierToName(Text original) {
			if (!((ItemStack) (Object) this).hasCustomName() && ((ItemStack) (Object) this).hasNbt() && ((ItemStack) (Object) this).getNbt().contains(Constants.MODIFIER_NAME))
				original = Text.translatable(((ItemStack) (Object) this).getNbt().getString(Constants.MODIFIER_NAME)).append(ScreenTexts.SPACE).append(original);
			return original
		}

		// mixin to ItemStack's get rarity function to modify it based off of modifier

		@ModifyReturnValue(method = "getMiningSpeedMultiplier", at = @At("RETURN")) // maybe only apply mining speed boost if original is greater than 1f (1f = not efficient at mining), so that block breaking is fucked up
		private float modifyMiningSpeed(float original, BlockState state) {
			if (((ItemStack) (Object) this).hasNbt() && ((ItemStack) (Object) this).getNbt().contains(Constants.MINING_SPEED_MULT))
				original *= ((ItemStack) (Object) this).getNbt().getFloat(Constants.MINING_SPEED_MULT);
			if (((ItemStack) (Object) this).hasNbt() && ((ItemStack) (Object) this).getNbt().contains(Constants.MINING_SPEED))
				original += ((ItemStack) (Object) this).getNbt().getFloat(Constants.MINING_SPEED) * 4; // arbitrary multiplication of 4, seems about right
			return original;
		}
	}

	@Mixin(EnchantmentHelper.class)
	public abstract static class EnchantmentHelperMixin {
		@ModifyReturnValue(method = "getAttackDamage", at = @At("RETURN"))
		private static float modifyItemAttackDamage(float original, ItemStack stack, EntityGroup group) {
			if (stack.hasNbt() && stack.getNbt().contains(Constants.DAMAGE_MULT)) {
				double originalDamage = stack.getAttributeModifiers(EquipmentSlot.MAINHAND)
						.get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream()
						.filter(modifier -> modifier.getId().equals(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF")))
						.findFirst().orElse(new EntityAttributeModifier("", 0.0, null)).getValue();
				original += originalDamage * (stack.getNbt().getDouble(Constants.DAMAGE_MULT) - 1);
			}
			if (stack.hasNbt() && stack.getNbt().contains(Constants.DAMAGE))
				original += stack.getNbt().getDouble(Constants.DAMAGE);
			return original;
		}
	}

	@Mixin(BowItem.class)
	public abstract static class BowItemMixin {
		@Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
		private float modifyUseTime(int useTicks, @Local(ordinal = 0) ItemStack stack) {
			if (stack.hasNbt() && stack.getNbt().contains(Constants.RANGED_WEAPON_SPEED))
				return Math.min(getPullProgress(useTicks) * stack.getNbt().getFloat(Constants.RANGED_WEAPON_SPEED), 1); // cap pull at 1
			return getPullProgress(useTicks);
		}
	}

	@Mixin(CrossbowItem.class)
	public abstract static class CrossbowItemMixin {
		@Inject(method = "getPullProgress", at = @At("RETURN"), cancellable = true)
		private static void modifyUseTime(int useTicks, ItemStack stack, CallbackInfoReturnable<Float> cir, @Local float f) {
			if (stack.hasNbt() && stack.getNbt().contains(Constants.RANGED_WEAPON_SPEED))
				cir.setReturnValue(Math.min(f * stack.getNbt().getFloat(Constants.RANGED_WEAPON_SPEED), 1));
		}
	}
}