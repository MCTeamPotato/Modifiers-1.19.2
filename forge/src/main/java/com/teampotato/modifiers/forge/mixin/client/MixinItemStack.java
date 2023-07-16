package com.teampotato.modifiers.forge.mixin.client;

import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Inject(method = "getTooltip", at = @At("RETURN"), locals= LocalCapture.CAPTURE_FAILHARD)
    private void onGetTooltip(PlayerEntity playerIn, TooltipContext advanced, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        Modifier modifier = ModifierHandler.getModifier((ItemStack) (Object) this);
        if (modifier != null) {
            list.addAll(modifier.getInfoLines());
        }
    }
}