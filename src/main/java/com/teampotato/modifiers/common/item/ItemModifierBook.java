package com.teampotato.modifiers.common.item;

import com.teampotato.modifiers.ModifiersMod;
import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.modifier.Modifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModifierBook extends Item {
    public static final Identifier ID = new Identifier("modifiers", "modifier_book");
    public ItemModifierBook() {
        super(new Settings().rarity(Rarity.EPIC).group(ModifiersMod.GROUP_BOOKS));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if (!stack.hasNbt()) return false;
        NbtCompound tag = stack.getNbt();
        if (tag == null) return false;
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
        if (!stack.hasNbt() || (stack.getNbt() != null &&!stack.getNbt().contains(ModifierHandler.bookTagName))) return base;
        Modifier mod = com.teampotato.modifiers.common.modifier.Modifiers.MODIFIERS.get(new Identifier(stack.getNbt().getString(ModifierHandler.bookTagName)));
        if (mod == null) return base;
        return Text.translatable("misc.modifiers.modifier_prefix").append(MutableText.of(mod.getFormattedName()));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World worldIn,
                              List<Text> tooltip, TooltipContext flagIn) {
        if (stack.getNbt() != null && stack.getNbt().contains(ModifierHandler.bookTagName)) {
            Modifier mod = com.teampotato.modifiers.common.modifier.Modifiers.MODIFIERS.get(
                    new Identifier(stack.getNbt().getString(ModifierHandler.bookTagName)));
            if (mod != null) {
                tooltip.addAll(mod.getInfoLines());
                tooltip.add(Text.translatable(this.getTranslationKey() + ".tooltip.0"));
                tooltip.add(Text.translatable(this.getTranslationKey() + ".tooltip.1"));
                return;
            }
        }
        tooltip.add(Text.translatable(this.getTranslationKey()+".tooltip.invalid"));
    }

    protected List<ItemStack> getStacks() {
        List<Modifier> modifiers = new ObjectArrayList<>();
        modifiers.add(Modifiers.NONE);
        modifiers.addAll(Modifiers.curio_pool.modifiers);
        modifiers.addAll(Modifiers.tool_pool.modifiers);
        modifiers.addAll(Modifiers.shield_pool.modifiers);
        modifiers.addAll(Modifiers.bow_pool.modifiers);

        List<ItemStack> stacks = new ObjectArrayList<>();
        for (Modifier mod : modifiers) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateNbt().putString(ModifierHandler.bookTagName, mod.name.toString());
            stacks.add(stack);
        }
        return stacks;
    }
}

