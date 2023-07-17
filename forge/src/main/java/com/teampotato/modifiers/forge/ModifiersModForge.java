package com.teampotato.modifiers.forge;

import com.teampotato.modifiers.ModifiersMod;
import com.teampotato.modifiers.common.curios.ICurioProxy;
import com.teampotato.modifiers.common.item.ItemModifierBook;
import com.teampotato.modifiers.common.modifier.Modifiers;
import com.teampotato.modifiers.common.network.NetworkHandler;
import com.teampotato.modifiers.config.CurioNArmorConfig;
import com.teampotato.modifiers.config.ReforgeConfig;
import com.teampotato.modifiers.config.ToolConfig;
import com.teampotato.modifiers.forge.network.NetworkHandlerForge;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(ModifiersMod.MODID)
public class ModifiersModForge extends ModifiersMod {
    public ModifiersModForge() {
        Modifiers.init();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        NetworkHandler.register();
        ITEM_DEFERRED_REGISTER.register(eventBus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, ReforgeConfig.CONFIG, "remodifier-reforge.toml");
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, CurioNArmorConfig.CONFIG, "remodifier-armor-n-curio-modifiers.toml");
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, ToolConfig.CONFIG, "remodifier-tool-modifiers.toml");
    }

    static {
        NetworkHandler.setProxy(new NetworkHandlerForge());

        GROUP_BOOKS = new ItemGroup(-1, ModifiersMod.MODID+"_books") {
            @Override
            public ItemStack createIcon() {
                return MODIFIER_BOOK.get().getDefaultStack();
            }
        };
    }

    private void setup(final FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("curios")) {
            try {
                CURIO_PROXY = (ICurioProxy) Class.forName("com.teampotato.modifiers.forge.curios.CurioCompat").newInstance();
                MinecraftForge.EVENT_BUS.register(CURIO_PROXY);
            } catch (Throwable e) {
                System.out.println("Remodified failed to load Curios compatibility.");
                e.printStackTrace();
            }
        }
        if (CURIO_PROXY == null) {
            CURIO_PROXY = new ICurioProxy() {};
        }
    }

    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> MODIFIER_BOOK;

    static {
        MODIFIER_BOOK = ITEM_DEFERRED_REGISTER.register("modifier_book", ItemModifierBook::new);
    }
}
