package com.teampotato.modifiers.client.events;

import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGetTooltip(ItemTooltipEvent event) {
        Modifier modifier = ModifierHandler.getModifier(event.getItemStack());
        if (modifier != null) {
            event.getToolTip().addAll(modifier.getInfoLines());
        }
    }
}
