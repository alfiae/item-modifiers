package com.alfred.modifiers;

import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import com.alfred.modifiers.Constants.ModifierType;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public abstract class ItemModifier {
    private final String name;
    public final ModifierType type;

    public ItemModifier(String name) {
        this(name, ModifierType.ITEM);
    }
    public ItemModifier(String name, ModifierType type) {
        this.name = name;
        this.type = type;
    }

    public void applyModifier(ItemStack itemStack) {
        removeModifier(itemStack);
        NbtCompound modifier = itemStack.getOrCreateNbt();
        modifier.putString(Constants.MODIFIER_NAME, "modifiers.itemmodifier.%s".formatted(getName().toLowerCase()));
        modifier.putBoolean(Constants.HAS_MODIFIER, true);
    }

    public static void removeModifier(ItemStack itemStack) {
        if (itemStack.hasNbt() && itemStack.getNbt().contains(Constants.HAS_MODIFIER) && itemStack.getNbt().getBoolean(Constants.HAS_MODIFIER))
            for (String nbtId : Constants.values())
                if (itemStack.getNbt().contains(nbtId))
                    itemStack.getNbt().remove(nbtId); // remove all custom NBT tags this mod implements, if you have a custom modifier implementation and don't want your custom values to be removed, do not include HasModifier:1b in your custom NBT data
    }

    public boolean canApplyModifier(ItemStack itemStack) { // for custom modifier application logic
        return (itemStack.getNbt() == null || !itemStack.getNbt().getBoolean(Constants.HAS_MODIFIER)) && // make sure a modifier hasn't already been applied to item
                Arrays.stream(ModifiersConfig.getInstance().disabledModifiers).filter(string -> string.equals("modifier.%s".formatted(getName().toLowerCase())))
                        .findAny().orElse("").isEmpty() &&
               (type == ModifierType.ITEM ||
                type == ModifierType.UNIVERSAL && (itemStack.getItem() instanceof ToolItem || itemStack.getItem() instanceof RangedWeaponItem) ||
                type == ModifierType.COMMON && (itemStack.getItem() instanceof ToolItem || itemStack.getItem() instanceof RangedWeaponItem) ||
                type == ModifierType.MELEE_TOOL && (itemStack.getItem() instanceof  SwordItem || itemStack.getItem() instanceof AxeItem || itemStack.getItem() instanceof PickaxeItem || itemStack.getItem() instanceof ShovelItem) ||
                type == ModifierType.MELEE && (itemStack.getItem() instanceof  SwordItem || itemStack.getItem() instanceof AxeItem) ||
                type == ModifierType.SWORD && itemStack.getItem() instanceof SwordItem ||
                type == ModifierType.PICKAXE && itemStack.getItem() instanceof PickaxeItem ||
                type == ModifierType.AXE && itemStack.getItem() instanceof AxeItem ||
                type == ModifierType.HOE && itemStack.getItem() instanceof HoeItem ||
                type == ModifierType.SHOVEL && itemStack.getItem() instanceof ShovelItem ||
                type == ModifierType.BOW && itemStack.getItem() instanceof BowItem ||
                type == ModifierType.CROSSBOW && itemStack.getItem() instanceof CrossbowItem ||
                type == ModifierType.FISHING_ROD && itemStack.getItem() instanceof FishingRodItem ||
                type == ModifierType.SHIELD && itemStack.getItem() instanceof ShieldItem ||
                type == ModifierType.TOOL && itemStack.getItem() instanceof ToolItem ||
                type == ModifierType.MINING_TOOL && itemStack.getItem() instanceof MiningToolItem ||
                type == ModifierType.RANGED && itemStack.getItem() instanceof RangedWeaponItem ||
                type == ModifierType.ARMOR && itemStack.getItem() instanceof ArmorItem ||
                type == ModifierType.TRIDENT && itemStack.getItem() instanceof TridentItem);
    }

    public String getName() {
        return name;
    }
}
