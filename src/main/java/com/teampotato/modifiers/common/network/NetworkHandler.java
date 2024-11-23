package com.teampotato.modifiers.common.network;

import com.teampotato.modifiers.ModifiersMod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;

public class NetworkHandler {
    private static NetworkHandlerProxy proxy;

    public static void setProxy(NetworkHandlerProxy proxy) {
        NetworkHandler.proxy = proxy;
    }

    public static void register() {
        // Commented out example until I actually add any packets
        registerMessage(new ResourceLocation(ModifiersMod.MOD_ID, "reforge"), 0, Side.ClientToServer,
                PacketC2SReforge.class, PacketC2SReforge::encode,
                PacketC2SReforge::new, mainThreadHandler(PacketC2SReforge.Handler::handle));
    }

    @Contract(pure = true)
    private static <T> @NotNull BiConsumer<T, PacketContext> mainThreadHandler(Consumer<? super T> handler) {
        return (packet, ctx) -> ctx.threadExecutor.submit(() -> handler.accept(packet));
    }

    @Contract(pure = true)
    private static <T> @NotNull BiConsumer<T, PacketContext> mainThreadHandler(BiConsumer<? super T, PacketContext> handler) {
        return (packet, ctx) -> ctx.threadExecutor.submit(() -> handler.accept(packet, ctx));
    }


    /** id only used on fabric, discrim only used on forge */
    public static <MSG> void registerMessage(ResourceLocation id, int discrim, Side side,
                                             Class<MSG> clazz,
                                             BiConsumer<MSG, FriendlyByteBuf> encode,
                                             Function<FriendlyByteBuf, MSG> decode,
                                             BiConsumer<MSG, NetworkHandler.PacketContext> handler) {
        proxy.registerMessage(id, discrim, side, clazz, encode, decode, handler);
    }

    public static <MSG> void sendToServer(MSG packet) {
        proxy.sendToServer(packet);
    }
    public static <MSG> void sendTo(MSG packet, ServerPlayer player) {
        proxy.sendTo(packet, player);
    }
    public static <MSG> void sendToAllPlayers(MSG packet) {
        proxy.sendToAllPlayers(packet);
    }

    // Based on Fabric's PacketContext
    @SuppressWarnings("ClassCanBeRecord")
    public static class PacketContext {
        @Nullable
        public final Player player;
        public final BlockableEventLoop<?> threadExecutor;
        public PacketContext(@Nullable Player player, BlockableEventLoop<?> threadExecutor) {
            this.player = player;
            this.threadExecutor = threadExecutor;
        }
    }

    public enum Side {
        ServerToClient, ClientToServer
    }
}

