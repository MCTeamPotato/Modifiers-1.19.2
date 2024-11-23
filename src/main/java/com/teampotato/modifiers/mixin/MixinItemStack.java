package com.teampotato.modifiers.mixin;

import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.modifier.Modifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Shadow public abstract Item getItem();

    @Inject(method = "getHoverName", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Component> cir) {
        Modifier modifier = ModifierHandler.getModifier((ItemStack) (Object)this);
        if (modifier != null && modifier != Modifiers.NONE) {
            MutableComponent modifierText = modifier.getTranslate();
            Component itemText = this.getItem().getDescription();
            MutableComponent newItemName = modifierText.append(" ").append(itemText);
            cir.setReturnValue(newItemName);
        }
    }
}
