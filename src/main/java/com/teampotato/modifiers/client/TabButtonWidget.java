package com.teampotato.modifiers.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TabButtonWidget extends Button {
    public boolean toggled;

    protected ResourceLocation texture;
    protected int u;
    protected int v;
    protected int pressedUOffset;
    protected int hoverVOffset;

    public TabButtonWidget(int i, int j, int k, int l, Component text, OnPress pressAction) {
        super(i, j, k, l, text, pressAction, DEFAULT_NARRATION);
    }

    public TabButtonWidget(int i, int j, int k, int l, Component text, OnPress pressAction, CreateNarration createNarration) {
        super(i, j, k, l, text, pressAction, createNarration);
    }

    public void setTextureUV(int i, int j, int k, int l, ResourceLocation identifier) {
        this.u = i;
        this.v = j;
        this.pressedUOffset = k;
        this.hoverVOffset = l;
        this.texture = identifier;
    }

    @Override
    public void renderWidget(PoseStack matrixStack, int i, int j, float f) {

        Minecraft minecraftClient = Minecraft.getInstance();
        minecraftClient.getTextureManager().bindForSetup(this.texture);
        RenderSystem.disableDepthTest();

        int u = this.u;
        int v = this.v;
        if (this.toggled) {
            u += this.pressedUOffset;
        }

        if (this.isHoveredOrFocused()) {
            v += this.hoverVOffset;
        }

        blit(matrixStack, this.getX(), this.getY(), u, v, this.width, this.height);
        int color = this.active ? 16777215 : 10526880;
        drawCenteredString(matrixStack, minecraftClient.font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color | Mth.ceil(this.alpha * 255.0F) << 24);

        //if (this.isHoveredOrFocused()) this.renderToolTip(matrixStack, i, j);
        RenderSystem.enableDepthTest();
        super.renderWidget(matrixStack, i, j, f);
    }
}