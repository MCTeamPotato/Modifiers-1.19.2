package com.teampotato.modifiers.mixin;

import com.teampotato.modifiers.common.config.toml.ReforgeConfig;
import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.reforge.SmithingScreenHandlerReforge;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(SmithingMenu.class)
public abstract class MixinSmithingTableContainer extends ItemCombinerMenu implements SmithingScreenHandlerReforge {
    public MixinSmithingTableContainer(MenuType<?> a, int b, Inventory c, ContainerLevelAccess d) {
        super(a, b, c, d);
    }

    @Override
    public void modifiers$tryReforge() {
        ItemStack stack = this.inputSlots.getItem(0);
        ItemStack material = this.inputSlots.getItem(1);
        if (!ModifierHandler.canHaveModifiers(stack)) return;
        boolean canRepair = stack.getItem().isValidRepairItem(stack, material) && !ReforgeConfig.DISABLE_REPAIR_REFORGED.get();
        boolean isUniversal = ReforgeConfig.UNIVERSAL_REFORGE_ITEM.get().equals(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(material.getItem())).toString());

        if (canRepair|| isUniversal) {
            boolean hadModifier = ModifierHandler.hasModifier(stack);
            Modifier modifier = ModifierHandler.rollModifier(stack, ThreadLocalRandom.current());
            if (modifier != null) {
                ModifierHandler.setModifier(stack, modifier);
                if (hadModifier) {
                    material.shrink(1);
                    this.inputSlots.setItem(1, material);
                }
            }
        }
    }
}