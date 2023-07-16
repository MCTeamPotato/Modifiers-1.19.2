package com.teampotato.modifiers.common.item;

import com.teampotato.modifiers.ModifiersMod;
import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.modifier.Modifiers;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemModifierBook extends Item {
    public ItemModifierBook() {
        super(new Settings().rarity(Rarity.EPIC).group(ModifiersMod.GROUP_BOOKS));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (!stack.hasNbt()) return false;
        NbtCompound tag = stack.getOrCreateNbt();
        return tag.contains(ModifierHandler.bookTagName) && !tag.getString(ModifierHandler.bookTagName).equals("modifiers:none");
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
        // FIXME make variants display in JEI
        if (this.isIn(group)) {
            items.addAll(getStacks());
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        Text base = super.getName(stack);
        if (!stack.hasNbt() || !stack.getOrCreateNbt().contains(ModifierHandler.bookTagName)) return base;
        Modifier mod = Modifiers.modifiers.get(new Identifier(stack.getOrCreateNbt().getString(ModifierHandler.bookTagName)));
        if (mod == null) return base;
        return MutableText.of(new TranslatableTextContent("misc.modifiers.modifier_prefix")).append(MutableText.of(mod.getFormattedName()));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn,
                              List<Text> tooltip, TooltipContext flagIn) {
        if (stack.hasNbt() && stack.getOrCreateNbt().contains(ModifierHandler.bookTagName)) {
            Modifier mod = Modifiers.modifiers.get(
                    new Identifier(stack.getOrCreateNbt().getString(ModifierHandler.bookTagName)));
            if (mod != null) {
                tooltip.addAll(mod.getInfoLines());
                tooltip.add(MutableText.of(new TranslatableTextContent(this.getTranslationKey()+".tooltip.0")));
                tooltip.add(MutableText.of(new TranslatableTextContent(this.getTranslationKey()+".tooltip.1")));
                return;
            }
        }
        tooltip.add(MutableText.of(new TranslatableTextContent(this.getTranslationKey()+".tooltip.invalid")));
    }

    protected List<ItemStack> getStacks() {
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(Modifiers.NONE);
        modifiers.addAll(Modifiers.curio_pool.modifiers);
        modifiers.addAll(Modifiers.tool_pool.modifiers);

        List<ItemStack> stacks = new ArrayList<>();
        for (Modifier mod : modifiers) {
            ItemStack stack = new ItemStack(this);
            NbtCompound tag = stack.getOrCreateNbt();
            tag.putString(ModifierHandler.bookTagName, mod.name.toString());
            stacks.add(stack);
        }
        return stacks;
    }
}

