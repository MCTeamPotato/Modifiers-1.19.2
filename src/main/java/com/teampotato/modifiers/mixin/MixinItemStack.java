package com.teampotato.modifiers.mixin;

import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.modifier.Modifiers;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Shadow public abstract Item getItem();

    @Inject(method = "getName", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        Modifier modifier = ModifierHandler.getModifier((ItemStack) (Object)this);
        if (modifier != null && modifier != Modifiers.NONE) {
            cir.setReturnValue(MutableText.of(modifier.getFormattedName()).append(" ").append(MutableText.of(new TranslatableTextContent(this.getItem().getTranslationKey((ItemStack) (Object)this)))));
        }
    }
}
