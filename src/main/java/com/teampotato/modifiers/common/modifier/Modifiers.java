package com.teampotato.modifiers.common.modifier;

import com.teampotato.modifiers.ModifiersMod;
import com.teampotato.modifiers.common.config.toml.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.teampotato.modifiers.common.config.json.JsonConfigInitialier.*;


@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class Modifiers {
    public static final Map<Identifier, Modifier> MODIFIERS = new Object2ObjectOpenHashMap<>();

    public static final Modifier NONE = new Modifier.ModifierBuilder(new Identifier(ModifiersMod.MOD_ID, "none"), "modifier_none", ModifierType.BOTH).setWeight(0).build();

    static {
        MODIFIERS.put(NONE.name, NONE);
    }

    public static final ModifierPool curioPool = new ModifierPool(stack -> ModifiersMod.CURIO_PROXY.isModifiableCurio(stack));

    public static final ModifierPool armorPool = new ModifierPool(stack -> stack.getItem() instanceof ArmorItem || CuriosConfig.WHETHER_OR_NOT_CURIOS_USE_ARMOR_MODIFIERS.get() && ModifiersMod.CURIO_PROXY.isModifiableCurio(stack));

    public static final ModifierPool toolPool = new ModifierPool(stack -> {
        Item item = stack.getItem();
        if (item instanceof SwordItem) return true;
        return item instanceof MiningToolItem;
    });

    public static final ModifierPool bowPool = new ModifierPool(stack -> stack.getItem() instanceof RangedWeaponItem);

    public static final ModifierPool shieldPool = new ModifierPool(stack -> stack.getItem() instanceof ShieldItem);

    @Contract("_ -> new")
    private static Modifier.@NotNull ModifierBuilder equipped(String name) {
        return new Modifier.ModifierBuilder(new Identifier(ModifiersMod.MOD_ID, name), "modifier_" + name, ModifierType.EQUIPPED);
    }

    @Contract("_ -> new")
    private static Modifier.@NotNull ModifierBuilder held(String name) {
        return new Modifier.ModifierBuilder(new Identifier(ModifiersMod.MOD_ID, name), "modifier_" + name, ModifierType.HELD);
    }

    private static void addCurio(Modifier modifier) {
        MODIFIERS.put(modifier.name, modifier);
        curioPool.add(modifier);
    }

    private static void addArmor(Modifier modifier) {
        MODIFIERS.put(modifier.name, modifier);
        armorPool.add(modifier);
    }

    private static void addTool(Modifier modifier) {
        MODIFIERS.put(modifier.name, modifier);
        toolPool.add(modifier);
    }

    private static void addBow(Modifier modifier) {
        MODIFIERS.put(modifier.name, modifier);
        bowPool.add(modifier);
    }

    private static void addShield(Modifier modifier) {
        MODIFIERS.put(modifier.name, modifier);
        shieldPool.add(modifier);
    }

    @Contract("_, _ -> new")
    private static Modifier.@NotNull AttributeModifierSupplier mod(double amount, Operation op) {
        return new Modifier.AttributeModifierSupplier(amount, op);
    }

    private static Modifier.AttributeModifierSupplier @NotNull [] mods(String @NotNull [] amounts, String[] ops) {
        Modifier.AttributeModifierSupplier[] suppliers = new Modifier.AttributeModifierSupplier[amounts.length];
        for (int index = 0; index < amounts.length; index++) {
            suppliers[index] = new Modifier.AttributeModifierSupplier(Double.parseDouble(amounts[index]), Operation.fromId(Integer.parseInt(ops[index])));
        }
        return suppliers;
    }

    @Contract("_, _ -> new")
    private static @NotNull List<? extends String> merge(@NotNull List<? extends String> iterable1, @NotNull List<? extends String> iterable2) {
        List<String> list = new ArrayList<>(iterable1);
        list.addAll(iterable2);
        return list;
    }

    private static void initBowModifiers() {
        List<? extends String> MODIFIERS_NAMES = merge(BowConfig.NAMES.get(), BOW_NAMES);
        List<? extends String> MODIFIERS_WEIGHTS = merge(BowConfig.WEIGHTS.get(), BOW_WEIGHTS);
        List<? extends String> MODIFIERS_ATTRIBUTES = merge(BowConfig.ATTRIBUTES.get(), BOW_ATTRIBUTES);
        List<? extends String> MODIFIERS_AMOUNTS = merge(BowConfig.AMOUNTS.get(), BOW_AMOUNTS);
        List<? extends String> MODIFIERS_OPERATIONS_IDS = merge(BowConfig.OPERATIONS_IDS.get(), BOW_OPERATIONS_IDS);
        for (int index = 0; index < MODIFIERS_NAMES.size(); index++) {
            String name = MODIFIERS_NAMES.get(index);
            String weight = MODIFIERS_WEIGHTS.get(index);
            String attribute = MODIFIERS_ATTRIBUTES.get(index);
            String amount = MODIFIERS_AMOUNTS.get(index);
            String operations_id = MODIFIERS_OPERATIONS_IDS.get(index);
            if (attribute.contains(";")) {
                String[] attributes = attribute.split(";");
                String[] amounts = amount.split(";");
                String[] operations_ids = operations_id.split(";");
                addBow(held(name).addModifiers(attributes, mods(amounts, operations_ids)).setWeight(Integer.parseInt(weight)).build());
            } else {
                EntityAttribute entityAttribute = ForgeRegistries.ATTRIBUTES.getValue(new Identifier(attribute));
                if (entityAttribute == null) {
                    ModifiersMod.LOGGER.fatal("Invalid value: " + attribute);
                    return;
                }
                addBow(held(name).setWeight(Integer.parseInt(weight)).addModifier(entityAttribute, mod(Double.parseDouble(amount), Operation.fromId(Integer.parseInt(operations_id)))).build());
            }
        }
    }

    private static void initShieldModifiers() {
        List<? extends String> MODIFIERS_NAMES = merge(ShieldConfig.NAMES.get(), SHIELD_NAMES);
        List<? extends String> MODIFIERS_WEIGHTS = merge(ShieldConfig.WEIGHTS.get(), SHIELD_WEIGHTS);
        List<? extends String> MODIFIERS_ATTRIBUTES = merge(ShieldConfig.ATTRIBUTES.get(), SHIELD_ATTRIBUTES);
        List<? extends String> MODIFIERS_AMOUNTS = merge(ShieldConfig.AMOUNTS.get(), SHIELD_AMOUNTS);
        List<? extends String> MODIFIERS_OPERATIONS_IDS = merge(ShieldConfig.OPERATIONS_IDS.get(), SHIELD_OPERATIONS_IDS);
        for (int index = 0; index < MODIFIERS_NAMES.size(); index++) {
            String name = MODIFIERS_NAMES.get(index);
            String weight = MODIFIERS_WEIGHTS.get(index);
            String attribute = MODIFIERS_ATTRIBUTES.get(index);
            String amount = MODIFIERS_AMOUNTS.get(index);
            String operations_id = MODIFIERS_OPERATIONS_IDS.get(index);
            if (attribute.contains(";")) {
                String[] attributes = attribute.split(";");
                String[] amounts = amount.split(";");
                String[] operations_ids = operations_id.split(";");
                addShield(held(name).addModifiers(attributes, mods(amounts, operations_ids)).setWeight(Integer.parseInt(weight)).build());
            } else {
                EntityAttribute entityAttribute = ForgeRegistries.ATTRIBUTES.getValue(new Identifier(attribute));
                if (entityAttribute == null) {
                    ModifiersMod.LOGGER.fatal("Invalid value: " + attribute);
                    return;
                }
                addShield(held(name).setWeight(Integer.parseInt(weight)).addModifier(entityAttribute, mod(Double.parseDouble(amount), Operation.fromId(Integer.parseInt(operations_id)))).build());
            }
        }
    }

    private static void initToolModifiers() {
        List<? extends String> MODIFIERS_NAMES = merge(ToolConfig.NAMES.get(), TOOL_NAMES);
        List<? extends String> MODIFIERS_WEIGHTS = merge(ToolConfig.WEIGHTS.get(), TOOL_WEIGHTS);
        List<? extends String> MODIFIERS_ATTRIBUTES = merge(ToolConfig.ATTRIBUTES.get(), TOOL_ATTRIBUTES);
        List<? extends String> MODIFIERS_AMOUNTS = merge(ToolConfig.AMOUNTS.get(), TOOL_AMOUNTS);
        List<? extends String> MODIFIERS_OPERATIONS_IDS = merge(ToolConfig.OPERATIONS_IDS.get(), TOOL_OPERATIONS_IDS);
        for (int index = 0; index < MODIFIERS_NAMES.size(); index++) {
            String name = MODIFIERS_NAMES.get(index);
            String weight = MODIFIERS_WEIGHTS.get(index);
            String attribute = MODIFIERS_ATTRIBUTES.get(index);
            String amount = MODIFIERS_AMOUNTS.get(index);
            String operations_id = MODIFIERS_OPERATIONS_IDS.get(index);
            if (attribute.contains(";")) {
                String[] attributes = attribute.split(";");
                String[] amounts = amount.split(";");
                String[] operations_ids = operations_id.split(";");
                addTool(held(name).addModifiers(attributes, mods(amounts, operations_ids)).setWeight(Integer.parseInt(weight)).build());
            } else {
                EntityAttribute entityAttribute = ForgeRegistries.ATTRIBUTES.getValue(new Identifier(attribute));
                if (entityAttribute == null) {
                    ModifiersMod.LOGGER.fatal("Invalid value: " + attribute);
                    return;
                }
                addTool(held(name).setWeight(Integer.parseInt(weight)).addModifier(entityAttribute, mod(Double.parseDouble(amount), Operation.fromId(Integer.parseInt(operations_id)))).build());
            }
        }
    }

    private static void initArmorsModifiers() {
        List<? extends String> MODIFIERS_NAMES = merge(ArmorConfig.NAMES.get(), ARMOR_NAMES);
        List<? extends String> MODIFIERS_WEIGHTS = merge(ArmorConfig.WEIGHTS.get(), ARMOR_WEIGHTS);
        List<? extends String> MODIFIERS_ATTRIBUTES = merge(ArmorConfig.ATTRIBUTES.get(), ARMOR_ATTRIBUTES);
        List<? extends String> MODIFIERS_AMOUNTS = merge(ArmorConfig.AMOUNTS.get(), ARMOR_AMOUNTS);
        List<? extends String> MODIFIERS_OPERATIONS_IDS = merge(ArmorConfig.OPERATIONS_IDS.get(), ARMOR_OPERATIONS_IDS);
        for (int index = 0; index < MODIFIERS_NAMES.size(); index++) {
            String name = MODIFIERS_NAMES.get(index);
            String weight = MODIFIERS_WEIGHTS.get(index);
            String attribute = MODIFIERS_ATTRIBUTES.get(index);
            String amount = MODIFIERS_AMOUNTS.get(index);
            String operations_id = MODIFIERS_OPERATIONS_IDS.get(index);
            if (attribute.contains(";")) {
                String[] attributes = attribute.split(";");
                String[] amounts = amount.split(";");
                String[] operations_ids = operations_id.split(";");
                addArmor(equipped(name).setWeight(Integer.parseInt(weight)).addModifiers(attributes, mods(amounts, operations_ids)).build());
            } else {
                addArmor(equipped(name).setWeight(Integer.parseInt(weight)).addModifier(ForgeRegistries.ATTRIBUTES.getValue(new Identifier(attribute.split(":")[0], attribute.split(":")[1])), mod(Double.parseDouble(amount), Operation.fromId(Integer.parseInt(operations_id)))).build());
            }
        }
    }

    private static void initCuriosModifiers() {
        List<? extends String> MODIFIERS_NAMES = merge(CuriosConfig.NAMES.get(), CURIOS_NAMES);
        List<? extends String> MODIFIERS_WEIGHTS = merge(CuriosConfig.WEIGHTS.get(), CURIOS_WEIGHTS);
        List<? extends String> MODIFIERS_ATTRIBUTES = merge(CuriosConfig.ATTRIBUTES.get(), CURIOS_ATTRIBUTES);
        List<? extends String> MODIFIERS_AMOUNTS = merge(CuriosConfig.AMOUNTS.get(), CURIOS_AMOUNTS);
        List<? extends String> MODIFIERS_OPERATIONS_IDS = merge(CuriosConfig.OPERATIONS_IDS.get(), CURIOS_OPERATIONS_IDS);
        for (int index = 0; index < MODIFIERS_NAMES.size(); index++) {
            String name = MODIFIERS_NAMES.get(index);
            String weight = MODIFIERS_WEIGHTS.get(index);
            String attribute = MODIFIERS_ATTRIBUTES.get(index);
            String amount = MODIFIERS_AMOUNTS.get(index);
            String operations_id = MODIFIERS_OPERATIONS_IDS.get(index);
            if (attribute.contains(";")) {
                String[] attributes = attribute.split(";");
                String[] amounts = amount.split(";");
                String[] operations_ids = operations_id.split(";");
                addCurio(equipped(name).setWeight(Integer.parseInt(weight)).addModifiers(attributes, mods(amounts, operations_ids)).build());
            } else {
                addCurio(equipped(name).setWeight(Integer.parseInt(weight)).addModifier(ForgeRegistries.ATTRIBUTES.getValue(new Identifier(attribute)), mod(Double.parseDouble(amount), Operation.fromId(Integer.parseInt(operations_id)))).build());
            }
        }
    }

    public static void initialize() {
        try {
            initToolModifiers();
            initArmorsModifiers();
            initBowModifiers();
            initShieldModifiers();
            if (ModifiersMod.isCuriosLoaded() && !CuriosConfig.WHETHER_OR_NOT_CURIOS_USE_ARMOR_MODIFIERS.get()) initCuriosModifiers();
        } catch (Exception e) {
            if (!crashTheGameIfConfigHasErrors.get()) {
                ModifiersMod.LOGGER.error("Exception occurs during modifiers initialization", e);
            } else {
                ModifiersMod.LOGGER.error("Exception occurs during modifiers initialization", e);
                Runtime.getRuntime().exit(-1);
            }
        }
    }
}