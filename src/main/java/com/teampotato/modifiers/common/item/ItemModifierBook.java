package com.teampotato.modifiers.common.item;

import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.modifier.Modifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ItemModifierBook extends Item {
    public ItemModifierBook() {
        //super(new Properties().rarity(Rarity.EPIC).tab(ModifiersMod.GROUP_BOOKS));
        super(new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        if (!stack.hasTag()) return false;
        CompoundTag tag = stack.getTag();
        if (tag == null) return false;
        return tag.contains(ModifierHandler.bookTagName) && !tag.getString(ModifierHandler.bookTagName).equals("modifiers:none");
    }

//    @Override
//    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
//        if (this.allowedIn(group)) items.addAll(getStacks());
//    }

    @Override
    public Component getName(ItemStack stack) {
        Component base = super.getName(stack);
        if (!stack.hasTag() || (stack.getTag() != null && !stack.getTag().contains(ModifierHandler.bookTagName))) return base;
        Modifier mod = Modifiers.MODIFIERS.get(new ResourceLocation(stack.getTag().getString(ModifierHandler.bookTagName)));
        if (mod == null) return base;
        return Component.translatable("misc.modifiers.modifier_prefix").append(mod.getTranslate());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        String translationKey = this.getDescriptionId();
        if (stack.getTag() != null && stack.getTag().contains(ModifierHandler.bookTagName)) {
            Modifier mod = Modifiers.MODIFIERS.get(new ResourceLocation(stack.getTag().getString(ModifierHandler.bookTagName)));
            if (mod != null) {
                tooltip.addAll(mod.getInfoLines());
                tooltip.add(Component.translatable(translationKey + ".tooltip.0"));
                tooltip.add(Component.translatable(translationKey + ".tooltip.1"));
                return;
            }
        }
        tooltip.add(Component.translatable(translationKey + ".tooltip.invalid"));
    }

    public List<ItemStack> getStacks() {
        List<Modifier> modifiers = new ObjectArrayList<>();
        modifiers.add(Modifiers.NONE);
        modifiers.addAll(Modifiers.curioPool.modifiers);
        modifiers.addAll(Modifiers.toolPool.modifiers);
        modifiers.addAll(Modifiers.shieldPool.modifiers);
        modifiers.addAll(Modifiers.bowPool.modifiers);
        modifiers.addAll(Modifiers.armorPool.modifiers);

        List<ItemStack> stacks = new ObjectArrayList<>();
        for (Modifier mod : modifiers) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putString(ModifierHandler.bookTagName, mod.name.toString());
            stacks.add(stack);
        }
        return stacks;
    }
}

