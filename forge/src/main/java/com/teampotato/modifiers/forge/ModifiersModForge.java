package com.teampotato.modifiers.forge;

import com.teampotato.modifiers.ModifiersMod;
import com.teampotato.modifiers.common.curios.ICurioProxy;
import com.teampotato.modifiers.common.item.ItemModifierBook;
import com.teampotato.modifiers.common.network.NetworkHandler;
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
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        NetworkHandler.register();
        eventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        ITEM_DEFERRED_REGISTER.register(eventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, forgeConfigSpec);
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
                curioProxy = (ICurioProxy) Class.forName("com.teampotato.modifiers.forge.curios.CurioCompat").newInstance();
                MinecraftForge.EVENT_BUS.register(curioProxy);
            } catch (Throwable e) {
                System.out.println("Remodified failed to load Curios compatibility.");
                e.printStackTrace();
            }
        }
        if (curioProxy == null) {
            curioProxy = new ICurioProxy() {};
        }
    }

    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> MODIFIER_BOOK;

    static {
        MODIFIER_BOOK = ITEM_DEFERRED_REGISTER.register("modifier_book", ItemModifierBook::new);
    }

    public static ForgeConfigSpec forgeConfigSpec;
    public static ForgeConfigSpec.ConfigValue<String> universalReforgeItem;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Remodified");
        universalReforgeItem = builder.define("Universal Reforge Item", "minecraft:diamond");
        builder.pop();
        forgeConfigSpec = builder.build();
    }
}
