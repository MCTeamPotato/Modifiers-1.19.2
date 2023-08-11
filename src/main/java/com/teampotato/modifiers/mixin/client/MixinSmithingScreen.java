package com.teampotato.modifiers.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teampotato.modifiers.client.SmithingScreenReforge;
import com.teampotato.modifiers.client.TabButtonWidget;
import com.teampotato.modifiers.common.config.ReforgeConfig;
import com.teampotato.modifiers.common.modifier.Modifier;
import com.teampotato.modifiers.common.modifier.ModifierHandler;
import com.teampotato.modifiers.common.network.NetworkHandler;
import com.teampotato.modifiers.common.network.PacketC2SReforge;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(SmithingScreen.class)
public abstract class MixinSmithingScreen extends ForgingScreen<SmithingScreenHandler> implements SmithingScreenReforge {
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
    private Text modifiers_tab1Title;
    @Unique
    private Text modifiers_tab2Title;

    @Unique
    private int modifiers_outputSlotX;
    @Unique
    private int modifiers_outputSlotY;
    @Unique
    private static final Identifier modifiers$reforger = new Identifier("modifiers", "textures/gui/reforger.png");

    public MixinSmithingScreen(SmithingScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title, texture);
    }

    @Unique
    private void modifiers_toTab1() {
        modifiers_onTab2 = false;
        modifiers_reforgeButton.visible = false;
        this.title = modifiers_tab1Title;
        Slot slot = this.getScreenHandler().slots.get(2);
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
        Slot slot = this.getScreenHandler().slots.get(2);
        slot.x = 152;
        slot.y = 8;
        this.modifiers_tabButton1.toggled = false;
        this.modifiers_tabButton2.toggled = true;
    }

    @Override
    public void modifiers_init() {
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        Slot slot = this.getScreenHandler().slots.get(2);
        modifiers_outputSlotX = slot.x;
        modifiers_outputSlotY = slot.y;
        this.modifiers_tabButton1 = new TabButtonWidget(k-70, l+2, 70, 18, Text.translatable("container.modifiers.reforge.tab1"), (button) -> modifiers_toTab1());
        this.modifiers_tabButton2 = new TabButtonWidget(k-70, l+22, 70, 18, Text.translatable("container.modifiers.reforge.tab2"), (button) -> modifiers_toTab2());
        this.modifiers_tabButton1.setTextureUV(0, 166, 70, 18, new Identifier("modifiers", "textures/gui/reforger.png"));
        this.modifiers_tabButton2.setTextureUV(0, 166, 70, 18, new Identifier("modifiers", "textures/gui/reforger.png"));
        this.modifiers_reforgeButton = new TabButtonWidget(k+132, l+45, 20, 20, Text.of(""),
                (button) -> NetworkHandler.sendToServer(new PacketC2SReforge()),
                (button, matrixStack, i, j) -> this.renderTooltip(matrixStack, Text.translatable("container.modifiers.reforge.reforge"), i, j));
        this.modifiers_reforgeButton.setTextureUV(0, 202, 20, 20, new Identifier("modifiers", "textures/gui/reforger.png"));

        this.addDrawableChild(this.modifiers_tabButton1);
        this.addDrawableChild(this.modifiers_tabButton2);
        this.addDrawableChild(this.modifiers_reforgeButton);

        modifiers_tab1Title = this.title;
        modifiers_tab2Title = Text.translatable("container.modifiers.reforge");
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

    @SuppressWarnings("rawtypes")
    @Redirect(method = "drawForeground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ForgingScreen;drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V"))
    private void onRender(ForgingScreen instance, MatrixStack matrixStack, int i, int j) {
        if (this.client == null) return;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, modifiers$reforger);
        this.client.getTextureManager().bindTexture(modifiers$reforger);
        int k = (this.width - this.backgroundWidth) / 2;
        int l = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrixStack, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        ItemStack stack1 = this.handler.getSlot(0).getStack();
        ItemStack stack2 = this.handler.getSlot(1).getStack();
        boolean isUniversal = ReforgeConfig.UNIVERSAL_REFORGE_ITEM.get().equals(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack2.getItem())).toString());

        boolean cantReforge = !stack1.isEmpty() && !stack1.getItem().canRepair(stack1, stack2);
        if (isUniversal && cantReforge) cantReforge = false;
        // canReforge is also true for empty slot 1. Probably how it should behave.
        ((SmithingScreenReforge) this).modifiers_setCanReforge(!cantReforge);
        if (!stack1.isEmpty() && !(stack1.getItem().canRepair(stack1, stack2) || isUniversal)) {
            this.drawTexture(matrixStack, k + 99 - 53, l + 45, this.backgroundWidth, 0, 28, 21);
        }

        if (this.modifiers_onTab2) {
            Modifier modifier = ModifierHandler.getModifier(this.handler.getSlot(0).getStack());
            if (modifier != null) {
                this.textRenderer.draw(matrixStack, Text.translatable("misc.modifiers.modifier_prefix").append(MutableText.of(modifier.getFormattedName())), (float)this.titleX-15, (float)this.titleY+15, 4210752);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        this.modifiers_init();
    }
}