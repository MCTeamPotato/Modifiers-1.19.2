package com.teampotato.modifiers.mixin;

import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.reforge.SmithingScreenHandlerReforge;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(SmithingScreenHandler.class)
public abstract class MixinSmithingTableContainer extends ForgingScreenHandler implements SmithingScreenHandlerReforge {
    public MixinSmithingTableContainer(ScreenHandlerType<?> a, int b, PlayerInventory c, ScreenHandlerContext d) {
        super(a, b, c, d);
    }

    @Override
    public void modifiers$tryReforge() {
        ItemStack stack = input.getStack(0);
        ItemStack material = input.getStack(1);

        if (ModifierHandler.canHaveModifiers(stack)) {
            if (stack.getItem().canRepair(stack, material)) {
                boolean hadModifier = ModifierHandler.hasModifier(stack);
                Modifier modifier = ModifierHandler.rollModifier(stack, new Random());
                if (modifier != null) {
                    ModifierHandler.setModifier(stack, modifier);
                    if (hadModifier) {
                        material.decrement(1);
                        // We do this for markDirty() mostly, I think
                        input.setStack(1, material);
                    }
                }
            }
        }
    }
}

