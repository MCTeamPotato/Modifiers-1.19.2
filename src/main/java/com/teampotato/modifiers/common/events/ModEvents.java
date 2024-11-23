package com.teampotato.modifiers.common.events;

import com.teampotato.modifiers.ModifiersMod;
import com.teampotato.modifiers.common.item.ItemModifierBook;
import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.modifier.Modifiers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ModifiersMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(ModifiersMod.MOD_ID, ModifiersMod.MOD_ID), (builder) -> builder
                .title(Component.translatable("itemGroup.modifiers.books"))
                .icon(() -> ModifiersMod.MODIFIER_BOOK.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    List<Modifier> modifiers = new ObjectArrayList<>();
                    modifiers.add(Modifiers.NONE);
                    modifiers.addAll(Modifiers.curioPool.modifiers);
                    modifiers.addAll(Modifiers.toolPool.modifiers);
                    modifiers.addAll(Modifiers.shieldPool.modifiers);
                    modifiers.addAll(Modifiers.bowPool.modifiers);
                    modifiers.addAll(Modifiers.armorPool.modifiers);

                    for (Modifier mod : modifiers) {
                        ItemStack stack = ModifiersMod.MODIFIER_BOOK.get().getDefaultInstance();
                        stack.getOrCreateTag().putString(ModifierHandler.bookTagName, mod.name.toString());
                        output.accept(stack);
                    }
                }));
    }
}
