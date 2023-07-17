package com.teampotato.modifiers.events;

import com.teampotato.modifiers.common.curios.ICurioProxy;
import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.modifier.Modifiers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static com.teampotato.modifiers.forge.ModifiersModForge.MODIFIER_BOOK;

@Mod.EventBusSubscriber
public class Handler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAvilUpdate(AnvilUpdateEvent event) {
        ItemStack right = event.getRight();
        if (ModifierHandler.canHaveModifiers(event.getLeft()) && right.hasNbt() && right.getItem().getDefaultStack() == MODIFIER_BOOK.get().getDefaultStack()) {
            Modifier modifier = Modifiers.MODIFIERS.get(new Identifier(right.getOrCreateNbt().getString(ModifierHandler.bookTagName)));
            if (modifier != null) {
                ItemStack output = event.getLeft().copy();
                ModifierHandler.setModifier(output, modifier);
                event.setMaterialCost(1);
                event.setCost(1);
                event.setOutput(output);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEquipChange(LivingEquipmentChangeEvent event) {
        EquipmentSlot slotType = event.getSlot();
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        Modifier fromMod = ModifierHandler.getModifier(from);
        if (fromMod != null) ModifierHandler.removeEquipmentModifier(event.getEntity(), fromMod, slotType);
        Modifier toMod = ModifierHandler.getModifier(to);
        if (toMod == null) {
            toMod = ModifierHandler.rollModifier(to, new Random());
            if (toMod == null) return;
            ModifierHandler.setModifier(to, toMod);
        }
        ModifierHandler.applyEquipmentModifier(event.getEntity(), toMod, slotType);
    }
}
