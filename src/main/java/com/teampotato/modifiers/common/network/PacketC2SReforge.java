package com.teampotato.modifiers.common.network;

import com.teampotato.modifiers.common.reforge.SmithingScreenHandlerReforge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PacketC2SReforge {
    public PacketC2SReforge() {}

    PacketC2SReforge(PacketByteBuf buf) {}

    void encode(PacketByteBuf buf) {}

    public static class Handler {
        public static void handle(PacketC2SReforge packet, NetworkHandler.@NotNull PacketContext context) {
            PlayerEntity player = context.player;
            if (player != null && player.currentScreenHandler instanceof SmithingScreenHandlerReforge) {
                ((SmithingScreenHandlerReforge) player.currentScreenHandler).modifiers$tryReforge();
            }
        }
    }
}

