package com.teampotato.modifiers.common.config.json;

import com.google.common.base.Suppliers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teampotato.modifiers.ModifiersMod;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JsonConfigInitialier {
    private static final Path ROOT = FMLLoader.getGamePath().resolve("config").resolve("remodifier");
    private static final String NAME = "name", WEIGHT = "weight", ATTRIBUTES = "attributes", AMOUNTS = "amounts", OPERATION_ID = "operationId";

    private static final Supplier<Boolean> shouldGenerateViolentJson = Suppliers.memoize(() -> {
        File configFile = ROOT.resolve("config.json").toFile();
        if (!configFile.exists()) return false;
        try {
            FileReader reader = new FileReader(configFile);
            JsonObject configObject = JsonParser.parseReader(reader).getAsJsonObject();
            return configObject.get("ShouldGenerateViolentJson").getAsBoolean();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    });

    public static final Supplier<Boolean> crashTheGameIfConfigHasErrors = Suppliers.memoize(() -> {
        File configFile = ROOT.resolve("config.json").toFile();
        if (!configFile.exists()) return false;
        try {
            FileReader reader = new FileReader(configFile);
            JsonObject configObject = JsonParser.parseReader(reader).getAsJsonObject();
            return configObject.get("CrashTheGameIfConfigHasErrors").getAsBoolean();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    });

    static {
        ROOT.toFile().mkdirs();
        mkdirs("armor", "curios", "shield", "tool", "bow");
        generateJsonConfig();
        if (shouldGenerateViolentJson.get()) generateExampleFile();
    }

    private static @NotNull FileWriter writeFile(File configFile) throws IOException {
        JsonObject config = new JsonObject();
        config.addProperty(NAME, "violent");
        config.addProperty(WEIGHT, 100);
        config.addProperty(ATTRIBUTES, "minecraft:generic.attack_speed");
        config.addProperty(AMOUNTS, "0.04");
        config.addProperty(OPERATION_ID, "2");
        FileWriter fileWriter = new FileWriter(configFile);
        fileWriter.write(config.toString());
        return fileWriter;
    }

    private static @NotNull FileWriter writeConfig(File configFile) throws IOException {
        JsonObject config = new JsonObject();
        config.addProperty("ShouldGenerateViolentJson", true);
        config.addProperty("CrashTheGameIfConfigHasErrors", true);
        FileWriter fileWriter = new FileWriter(configFile);
        fileWriter.write(config.toString());
        return fileWriter;
    }

    private static void mkdirs(String @NotNull ... strings) {
        for (String s : strings) ROOT.resolve(s).toFile().mkdirs();
    }

    private static void generateExampleFile() {
        File violent = new File(ROOT.resolve("armor").toFile(), "violent.json");
        if (!violent.exists()) {
            try {
                FileWriter writer = writeFile(violent);
                writer.close();
            } catch (Throwable throwable) {
                ModifiersMod.LOGGER.error("Error occurs during example json file generation", throwable);
            }
        }
    }

    private static void generateJsonConfig() {
        File config = new File(ROOT.toFile(), "config.json");
        if (!config.exists()) {
            try {
                FileWriter writer = writeConfig(config);
                writer.close();
            } catch (IOException e) {
                ModifiersMod.LOGGER.error("Error occurs during config json file generation", e);
            }
        }
    }

    private static final List<File> BOW_JSONS = readFiles(ROOT.resolve("bow"));
    private static final List<File> CURIOS_JSONS = readFiles(ROOT.resolve("curios"));
    private static final List<File> ARMOR_JSONS = readFiles(ROOT.resolve("armor"));
    private static final List<File> SHIELD_JSONS = readFiles(ROOT.resolve("shield"));
    private static final List<File> TOOL_JSONS = readFiles(ROOT.resolve("tool"));

    public static final List<? extends String> BOW_NAMES = getElements(BOW_JSONS, NAME);
    public static final List<? extends String> BOW_WEIGHTS = getElements(BOW_JSONS, WEIGHT);
    public static final List<? extends String> BOW_ATTRIBUTES = getElements(BOW_JSONS, ATTRIBUTES);
    public static final List<? extends String> BOW_AMOUNTS = getElements(BOW_JSONS, AMOUNTS);
    public static final List<? extends String> BOW_OPERATIONS_IDS = getElements(BOW_JSONS, OPERATION_ID);

    public static final List<String> CURIOS_NAMES = getElements(CURIOS_JSONS, NAME);
    public static final List<String> CURIOS_WEIGHTS = getElements(CURIOS_JSONS, WEIGHT);
    public static final List<String> CURIOS_ATTRIBUTES = getElements(CURIOS_JSONS, ATTRIBUTES);
    public static final List<String> CURIOS_AMOUNTS = getElements(CURIOS_JSONS, AMOUNTS);
    public static final List<String> CURIOS_OPERATIONS_IDS = getElements(CURIOS_JSONS, OPERATION_ID);

    public static final List<String> ARMOR_NAMES = getElements(ARMOR_JSONS, NAME);
    public static final List<String> ARMOR_WEIGHTS = getElements(ARMOR_JSONS, WEIGHT);
    public static final List<String> ARMOR_ATTRIBUTES = getElements(ARMOR_JSONS, ATTRIBUTES);
    public static final List<String> ARMOR_AMOUNTS = getElements(ARMOR_JSONS, AMOUNTS);
    public static final List<String> ARMOR_OPERATIONS_IDS = getElements(ARMOR_JSONS, OPERATION_ID);

    public static final List<String> SHIELD_NAMES = getElements(SHIELD_JSONS, NAME);
    public static final List<String> SHIELD_WEIGHTS = getElements(SHIELD_JSONS, WEIGHT);
    public static final List<String> SHIELD_ATTRIBUTES = getElements(SHIELD_JSONS, ATTRIBUTES);
    public static final List<String> SHIELD_AMOUNTS = getElements(SHIELD_JSONS, AMOUNTS);
    public static final List<String> SHIELD_OPERATIONS_IDS = getElements(SHIELD_JSONS, OPERATION_ID);

    public static final List<String> TOOL_NAMES = getElements(TOOL_JSONS, NAME);
    public static final List<String> TOOL_WEIGHTS = getElements(TOOL_JSONS, WEIGHT);
    public static final List<String> TOOL_ATTRIBUTES = getElements(TOOL_JSONS, ATTRIBUTES);
    public static final List<String> TOOL_AMOUNTS = getElements(TOOL_JSONS, AMOUNTS);
    public static final List<String> TOOL_OPERATIONS_IDS = getElements(TOOL_JSONS, OPERATION_ID);


    public void init() {
        Logger logger = LogManager.getLogger(JsonConfigInitialier.class);
        logger.info("Remodifier's early json config initialization starts");
        logger.info("Loaded bow json modifiers: " + Arrays.toString(BOW_NAMES.toArray()));
        logger.info("Loaded curios json modifiers: " + Arrays.toString(CURIOS_NAMES.toArray()));
        logger.info("Loaded armor json modifiers: " + Arrays.toString(ARMOR_NAMES.toArray()));
        logger.info("Loaded shield json modifiers: " + Arrays.toString(SHIELD_NAMES.toArray()));
        logger.info("Loaded tool json modifiers: " + Arrays.toString(TOOL_NAMES.toArray()));
    }

    private static @NotNull List<String> getElements(@NotNull Iterable<File> files, String element) {
        List<String> names = new ArrayList<>();
        for (File file : files) {
            try {
                FileReader fileReader = new FileReader(file);
                JsonObject configObject = JsonParser.parseReader(fileReader).getAsJsonObject();
                names.add(configObject.get(element).getAsString());
            } catch (Throwable throwable) {
                ModifiersMod.LOGGER.error("Error occurs during " + file.getName() + " reading", throwable);
            }
        }
        return names;
    }

    private static @NotNull List<File> readFiles(@NotNull Path path) {
        List<File> fileList = new ObjectArrayList<>();
        File folder = path.toFile();
        if (!folder.exists() || !folder.isDirectory()) return Collections.emptyList();
        File[] files = folder.listFiles();
        if (files == null) return Collections.emptyList();
        for (File file : files) {
            if (!file.isFile() || !file.getName().toLowerCase().endsWith(".json")) continue;
            fileList.add(file);
        }
        return fileList;
    }
}
