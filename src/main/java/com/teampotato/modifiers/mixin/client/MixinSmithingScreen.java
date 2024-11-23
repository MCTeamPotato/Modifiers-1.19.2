package com.teampotato.modifiers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teampotato.modifiers.client.SmithingScreenReforge;
import com.teampotato.modifiers.client.TabButtonWidget;
import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.network.NetworkHandler;
import com.teampotato.modifiers.common.network.PacketC2SReforge;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreen.class)
public abstract class MixinSmithingScreen extends ItemCombinerScreen<SmithingMenu> implements SmithingScreenReforge {
    @Unique
    private TabButtonWidget modifiers_reforgeButton;
    @Unique
    private TabButtonWidget modifiers_tabButton1;
    @Unique
    private TabButtonWidget modifiers_tabButton2;
    @Unique
    private boolean modifiers_onTab2 = false;
    @Unique
    private boolean modifiers_canReforge = false;

    @Unique
    private Component modifiers_tab1Title;
    @Unique
    private Component modifiers_tab2Title;

    @Unique
    private int modifiers_outputSlotX;
    @Unique
    private int modifiers_outputSlotY;

    public MixinSmithingScreen(SmithingMenu handler, Inventory playerInventory, Component title, ResourceLocation texture) {
        super(handler, playerInventory, title, texture);
    }

    @Unique
    private void modifiers_toTab1() {
        modifiers_onTab2 = false;
        modifiers_reforgeButton.visible = false;
        this.title = modifiers_tab1Title;
        Slot slot = this.getMenu().slots.get(2);
        slot.x = modifiers_outputSlotX;
        slot.y = modifiers_outputSlotY;
        this.modifiers_tabButton1.toggled = true;
        this.modifiers_tabButton2.toggled = false;
    }

    @Unique
    private void modifiers_toTab2() {
        modifiers_onTab2 = true;
        modifiers_reforgeButton.visible = true;
        this.title = modifiers_tab2Title;
        Slot slot = this.getMenu().slots.get(2);
        slot.x = 152;
        slot.y = 8;
        this.modifiers_tabButton1.toggled = false;
        this.modifiers_tabButton2.toggled = true;
    }

    @Override
    public void modifiers_init() {
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        Slot slot = this.getMenu().slots.get(2);
        modifiers_outputSlotX = slot.x;
        modifiers_outputSlotY = slot.y;
        this.modifiers_tabButton1 = new TabButtonWidget(k-70, l+2, 70, 18, Component.translatable("container.modifiers.reforge.tab1"), (button) -> modifiers_toTab1());
        this.modifiers_tabButton2 = new TabButtonWidget(k-70, l+22, 70, 18, Component.translatable("container.modifiers.reforge.tab2"), (button) -> modifiers_toTab2());
        this.modifiers_tabButton1.setTextureUV(0, 166, 70, 18, new ResourceLocation("modifiers", "textures/gui/reforger.png"));
        this.modifiers_tabButton2.setTextureUV(0, 166, 70, 18, new ResourceLocation("modifiers", "textures/gui/reforger.png"));
        MutableComponent reforge = Component.translatable("container.modifiers.reforge.reforge");
        this.modifiers_reforgeButton = new TabButtonWidget(k+132, l+45, 20, 20, Component.nullToEmpty(""),
                (button) -> NetworkHandler.sendToServer(new PacketC2SReforge()),
                (supplier) -> reforge);
        modifiers_reforgeButton.setTooltip(Tooltip.create(reforge));
        this.modifiers_reforgeButton.setTextureUV(0, 202, 20, 20, new ResourceLocation("modifiers", "textures/gui/reforger.png"));

        this.addRenderableWidget(this.modifiers_tabButton1);
        this.addRenderableWidget(this.modifiers_tabButton2);
        this.addRenderableWidget(this.modifiers_reforgeButton);

        modifiers_tab1Title = this.title;
        modifiers_tab2Title = Component.translatable("container.modifiers.reforge");
        this.modifiers_toTab1();
    }

    @Override
    public boolean modifiers_isOnTab2() {
        return modifiers_onTab2;
    }

    @Override
    public void modifiers_setCanReforge(boolean canReforge) {
        this.modifiers_canReforge = canReforge;
        this.modifiers_reforgeButton.toggled = canReforge;
        this.modifiers_reforgeButton.active = canReforge;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onDrawForeground(PoseStack matrixStack, int i, int j, float partialTick, CallbackInfo ci) {
        if (this.modifiers_onTab2) {
            ItemStack stack = this.menu.getSlot(0).getItem();
            Modifier modifier = ModifierHandler.getModifier(stack);
            if (modifier != null) {
                this.font.draw(matrixStack, Component.translatable("misc.modifiers.modifier_prefix").append(modifier.getTranslate()), (float)this.titleLabelX-15, (float)this.titleLabelY+15, 4210752);
            }
        }
    }
}