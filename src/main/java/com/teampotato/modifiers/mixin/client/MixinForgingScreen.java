package com.teampotato.modifiers.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teampotato.modifiers.client.SmithingScreenReforge;
import com.teampotato.modifiers.common.config.toml.ReforgeConfig;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ItemCombinerScreen.class)
public abstract class MixinForgingScreen<T extends ItemCombinerMenu> extends AbstractContainerScreen<T> {


    @Unique
    private static final ResourceLocation modifiers$reforger = new ResourceLocation("modifiers", "textures/gui/reforger.png");

    public MixinForgingScreen(T handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    private void onDrawBackground(PoseStack matrixStack, float f, int i, int j, CallbackInfo ci) {
        Object screen = this;
        if ((screen instanceof SmithingScreen) && ((SmithingScreenReforge) this).modifiers_isOnTab2()) {
            ci.cancel();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, modifiers$reforger);
            this.minecraft.getTextureManager().bindForSetup(modifiers$reforger);
            int k = (this.width - this.imageWidth) / 2;
            int l = (this.height - this.imageHeight) / 2;
            blit(matrixStack, k, l, 0, 0, this.imageWidth, this.imageHeight);
            ItemStack stack1 = this.menu.getSlot(0).getItem();
            ItemStack stack2 = this.menu.getSlot(1).getItem();
            boolean isUniversalReforgeItem = ReforgeConfig.UNIVERSAL_REFORGE_ITEM.get().equals(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack2.getItem())).toString());
            boolean cantReforge = !stack1.isEmpty() && !stack1.getItem().isValidRepairItem(stack1, stack2);
            if (ReforgeConfig.DISABLE_REPAIR_REFORGED.get() && !cantReforge) cantReforge = true;
            if (isUniversalReforgeItem && cantReforge) cantReforge = false;
            // canReforge is also true for empty slot 1. Probably how it should behave.
            ((SmithingScreenReforge) this).modifiers_setCanReforge(!cantReforge);
            if (cantReforge) blit(matrixStack, k + 99 - 53, l + 45, this.imageWidth, 0, 28, 21);
        }
    }
}