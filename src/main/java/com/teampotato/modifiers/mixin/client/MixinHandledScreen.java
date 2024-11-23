package com.teampotato.modifiers.mixin.client;

import com.teampotato.modifiers.client.SmithingScreenReforge;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinHandledScreen extends Screen {

    protected MixinHandledScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (modifiers$getThis() instanceof SmithingScreen) {
            ((SmithingScreenReforge) this).modifiers_init();
        }
    }

    @Unique
    @SuppressWarnings("rawtypes")
    private AbstractContainerScreen modifiers$getThis() {
        return (AbstractContainerScreen) (Object) this;
    }
}
