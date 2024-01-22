package com.alfred.modifiers.modifiers.common;

import com.alfred.modifiers.Constants;
import com.alfred.modifiers.Constants.ModifierType;
import com.alfred.modifiers.ItemModifier;
import com.alfred.modifiers.modifiers.Util;
import net.minecraft.item.ItemStack;

public class AgileModifier extends ItemModifier {
    public AgileModifier() {
        super("Agile", ModifierType.COMMON);
    }

    @Override
    public void applyModifier(ItemStack itemStack) {
        if (itemStack.getItem().getMaxCount() != 1)
            return;
        super.applyModifier(itemStack);
        Util.modifyStack(itemStack, null, null, null, 1.1f);
        itemStack.getOrCreateNbt().putDouble(Constants.CRIT, 0.03);
    }
}
