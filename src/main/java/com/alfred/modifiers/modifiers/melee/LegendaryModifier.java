package com.alfred.modifiers.modifiers.melee;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class LegendaryModifier extends ItemModifier {
    public LegendaryModifier() {
        super("Legendary", ModifierType.MELEE_TOOL);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, 1.5f, null, null, 1.1f);
        itemStack.getOrCreateNbt().putDouble(Constants.KNOCKBACK, 1.25);
        itemStack.getOrCreateNbt().putDouble(Constants.SIZE, 1.1);
        itemStack.getOrCreateNbt().putDouble(Constants.CRIT, 0.05);
    }
}
