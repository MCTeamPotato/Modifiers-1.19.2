package com.teampotato.modifiers;

import com.teampotato.modifiers.common.config.*;
import com.teampotato.modifiers.common.curios.ICurioProxy;
import com.teampotato.modifiers.common.events.ClientEvents;
import com.teampotato.modifiers.common.events.CommonEvents;
import com.teampotato.modifiers.common.item.ItemModifierBook;
import com.teampotato.modifiers.common.modifier.Modifiers;
import com.teampotato.modifiers.common.network.NetworkHandler;
import com.teampotato.modifiers.common.network.NetworkHandlerForge;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ModifiersMod.MOD_ID)
public class ModifiersMod {
    public static final String MOD_ID = "modifiers";
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> MODIFIER_BOOK;
    public static final Logger LOGGER = LogManager.getLogger();
    public static ICurioProxy CURIO_PROXY;
    public static ItemGroup GROUP_BOOKS;
    public ModifiersMod() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final ModLoadingContext ctx = ModLoadingContext.get();
        final ModConfig.Type COMMON = ModConfig.Type.COMMON;
        NetworkHandler.register();
        ITEM_DEFERRED_REGISTER.register(eventBus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(CommonEvents.class);
        if (FMLLoader.getDist().isClient()) {
            MinecraftForge.EVENT_BUS.register(ClientEvents.class);
        }
        ctx.registerConfig(COMMON, ReforgeConfig.CONFIG, "remodifier/reforge.toml");
        ctx.registerConfig(COMMON, CurioNArmorConfig.CONFIG, "remodifier/armor-n-curio-modifiers.toml");
        ctx.registerConfig(COMMON, ToolConfig.CONFIG, "remodifier/tool-modifiers.toml");
        ctx.registerConfig(COMMON, BowConfig.CONFIG, "remodifier/bow-modifiers.toml");
        ctx.registerConfig(COMMON, ShieldConfig.CONFIG, "remodifier/shield-modifiers.toml");
    }

    static {
        NetworkHandler.setProxy(new NetworkHandlerForge());

        GROUP_BOOKS = new ItemGroup(-1, MOD_ID +"_books") {
            @Override
            public ItemStack createIcon() {
                return MODIFIER_BOOK.get().getDefaultStack();
            }
        };
    }

    private void setup(final FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("curios")) {
            try {
                CURIO_PROXY = (ICurioProxy) Class.forName("com.teampotato.modifiers.common.curios.CurioCompat").getDeclaredConstructor().newInstance();
                MinecraftForge.EVENT_BUS.register(CURIO_PROXY);
            } catch (Exception e) {
                LOGGER.error("Remodified failed to load Curios integration.");
                LOGGER.error(e.getMessage());
            }
        }
        if (CURIO_PROXY == null) {
            CURIO_PROXY = new ICurioProxy() {};
        }
        Modifiers.init();
    }

    static {
        MODIFIER_BOOK = ITEM_DEFERRED_REGISTER.register("modifier_book", ItemModifierBook::new);
    }
}
