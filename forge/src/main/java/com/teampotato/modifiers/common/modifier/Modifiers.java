package com.teampotato.modifiers.common.modifier;

import com.teampotato.modifiers.config.CurioNArmorConfig;
import com.teampotato.modifiers.config.ToolConfig;
import com.teampotato.modifiers.forge.ModifiersModForge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Modifiers {
    public static Map<Identifier, Modifier> MODIFIERS = new HashMap<>();

    public static Modifier NONE = new Modifier.ModifierBuilder(new Identifier(ModifiersModForge.MODID, "none"), "modifier_none", Modifier.ModifierType.BOTH).setWeight(0).build();
    static {
        MODIFIERS.put(NONE.name, NONE);
    }

    public static ModifierPool curio_pool = new ModifierPool(stack -> {
        if (stack.getItem() instanceof ArmorItem) return true;
        return ModifiersModForge.CURIO_PROXY.isModifiableCurio(stack);
    });

    public static ModifierPool tool_pool = new ModifierPool(stack -> {
        Item item = stack.getItem();
        if (item instanceof SwordItem) return true;
        return item instanceof MiningToolItem || item instanceof RangedWeaponItem || item instanceof ShieldItem;
    });

    private static Modifier.ModifierBuilder curio(String name) {
        return new Modifier.ModifierBuilder(new Identifier(ModifiersModForge.MODID, name), "modifier_" + name, Modifier.ModifierType.EQUIPPED);
    }

    private static Modifier.ModifierBuilder both(String name) {
        return new Modifier.ModifierBuilder(new Identifier(ModifiersModForge.MODID, name), "modifier_" + name, Modifier.ModifierType.BOTH);
    }

    private static Modifier.ModifierBuilder tool(String name) {
        return new Modifier.ModifierBuilder(new Identifier(ModifiersModForge.MODID, name), "modifier_" + name, Modifier.ModifierType.HELD);
    }

    private static void addCurio(Modifier modifier) {
        MODIFIERS.put(modifier.name, modifier);
        curio_pool.add(modifier);
    }

    private static void addTool(Modifier modifier) {
        MODIFIERS.put(modifier.name, modifier);
        tool_pool.add(modifier);
    }

    private static Modifier.AttributeModifierSupplier mod(double amount, Operation op) {
        return new Modifier.AttributeModifierSupplier(amount, op);
    }

    private static Modifier.AttributeModifierSupplier[] mods(String[] amounts, String[] ops) {
        Modifier.AttributeModifierSupplier[] suppliers = new Modifier.AttributeModifierSupplier[amounts.length];
        for (String amount : amounts) {
            int index = List.of(amounts).indexOf(amount);
            suppliers[index] = new Modifier.AttributeModifierSupplier(Double.parseDouble(amount), Operation.fromId(Integer.parseInt(ops[index])));
        }
        return suppliers;
    }

    private static void initToolModifiers() {
        List<? extends String> MODIFIERS_NAMES = ToolConfig.NAMES.get();
        List<? extends String> MODIFIERS_WEIGHTS = ToolConfig.WEIGHTS.get();
        List<? extends String> MODIFIERS_ATTRIBUTES = ToolConfig.ATTRIBUTES.get();
        List<? extends String> MODIFIERS_AMOUNTS = ToolConfig.AMOUNTS.get();
        List<? extends String> MODIFIERS_OPERATIONS_IDS = ToolConfig.OPERATIONS_IDS.get();
        for (String name : MODIFIERS_NAMES) {
            int index = MODIFIERS_NAMES.indexOf(name);
            String weight = MODIFIERS_WEIGHTS.get(index);
            String attribute = MODIFIERS_ATTRIBUTES.get(index);
            String amount = MODIFIERS_AMOUNTS.get(index);
            String operations_id = MODIFIERS_OPERATIONS_IDS.get(index);
            if (attribute.contains(";")) {
                String[] attributes = attribute.split(";");
                String[] amounts = amount.split(";");
                String[] operations_ids = operations_id.split(";");
                addTool(tool(name).addModifiers(attributes, mods(amounts, operations_ids)).setWeight(Integer.parseInt(weight)).build());
            } else {
                EntityAttribute entityAttribute = ForgeRegistries.ATTRIBUTES.getValue(new Identifier(attribute));
                if (entityAttribute == null) {
                    ModifiersModForge.LOGGER.fatal("Invalid value: " + attribute);
                    if (FMLLoader.getDist().isDedicatedServer()){
                        ServerLifecycleHooks.getCurrentServer().shutdown();
                    } else if (FMLLoader.getDist().isClient()) {
                        MinecraftClient.getInstance().stop();
                    }
                }
                addTool(tool(name).setWeight(Integer.parseInt(weight)).addModifier(entityAttribute, mod(Double.parseDouble(amount), Operation.fromId(Integer.parseInt(operations_id)))).build());
            }
        }
    }

    private static void initCuriosNArmorsModifiers() {
        List<? extends String> MODIFIERS_NAMES = CurioNArmorConfig.NAMES.get();
        List<? extends String> MODIFIERS_WEIGHTS = CurioNArmorConfig.WEIGHTS.get();
        List<? extends String> MODIFIERS_ATTRIBUTES = CurioNArmorConfig.ATTRIBUTES.get();
        List<? extends String> MODIFIERS_AMOUNTS = CurioNArmorConfig.AMOUNTS.get();
        List<? extends String> MODIFIERS_OPERATIONS_IDS = CurioNArmorConfig.OPERATIONS_IDS.get();
        for (String name : MODIFIERS_NAMES) {
            int index = MODIFIERS_NAMES.indexOf(name);
            String weight = MODIFIERS_WEIGHTS.get(index);
            String attribute = MODIFIERS_ATTRIBUTES.get(index);
            String amount = MODIFIERS_AMOUNTS.get(index);
            String operations_id = MODIFIERS_OPERATIONS_IDS.get(index);
            if (attribute.contains(";")) {
                String[] attributes = attribute.split(";");
                String[] amounts = amount.split(";");
                String[] operations_ids = operations_id.split(";");
                addCurio(curio(name).setWeight(Integer.parseInt(weight)).addModifiers(attributes, mods(amounts, operations_ids)).build());
            } else {
                addCurio(curio(name).setWeight(Integer.parseInt(weight)).addModifier(ForgeRegistries.ATTRIBUTES.getValue(new Identifier(attribute.split(":")[0], attribute.split(":")[1])), mod(Double.parseDouble(amount), Operation.fromId(Integer.parseInt(operations_id)))).build());
            }
        }
    }


    public static void init() {
        initToolModifiers();
        initCuriosNArmorsModifiers();
    }
}