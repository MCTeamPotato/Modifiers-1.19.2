package com.teampotato.modifiers.common.network;

import com.teampotato.modifiers.common.reforge.SmithingScreenHandlerReforge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PacketC2SReforge {
    public PacketC2SReforge() {}

    PacketC2SReforge(FriendlyByteBuf buf) {}

    void encode(FriendlyByteBuf buf) {}

    public static class Handler {
        public static void handle(PacketC2SReforge packet, NetworkHandler.@NotNull PacketContext context) {
            Player player = context.player;
            if (player != null && player.containerMenu instanceof SmithingScreenHandlerReforge) {
                ((SmithingScreenHandlerReforge) player.containerMenu).modifiers$tryReforge();
            }
        }
    }
}

