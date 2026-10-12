package common.cn.kafei.simukraft.config.material;

import com.google.gson.*;
import common.cn.kafei.simukraft.SimuKraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class MaterialConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String ALL_MODE_BLOCK_BLACKLIST_KEY = "allModeBlockBlacklist";
    private static final String BASIC_MATERIALS_KEY = "basicMaterials";
    private static final String MATERIAL_CATEGORY_GROUPS_KEY = "materialCategoryGroups";
    private static final String EXPERT_MODE_SKIP_LIST_KEY = "expertModeSkipList";

    public static Set<ResourceKey<Block>> ALL_MODE_BLOCK_BLACKLIST;
    public static Set<ResourceKey<Block>> BASIC_MATERIALS;
    public static Set<ReplaceableMaterialHolder> MATERIAL_CATEGORY_GROUPS;
    public static Set<ResourceKey<Block>> EXPERT_MODE_SKIP_LIST;

    private static final String CONFIG_JSON_FILE_NAME = "simukraft-materials.json";

    /**
     * configFile: 材料配置写在游戏 config 目录，不使用 defaultconfigs。
     */
    private static Path configFile() {
        return FMLPaths.CONFIGDIR.get().resolve(CONFIG_JSON_FILE_NAME);
    }

    private static boolean loaded;
    private static int revision;

    public static void init() {
        if (loaded) {
            return;
        }
        loaded = true;
        loadConfigs();
        revision++;
    }

    public static int revision() {
        init();
        return revision;
    }

    public static void save() {
        init();
        saveConfigs();
        revision++;
    }

    public static void replaceAll(List<String> blacklist, List<String> basic, List<String> categories, List<String> expert) {
        init();
        ALL_MODE_BLOCK_BLACKLIST = parseIdSet(blacklist, ALL_MODE_BLOCK_BLACKLIST_KEY);
        BASIC_MATERIALS = parseIdSet(basic, BASIC_MATERIALS_KEY);
        MATERIAL_CATEGORY_GROUPS = parseCategoryLines(categories);
        EXPERT_MODE_SKIP_LIST = parseIdSet(expert, EXPERT_MODE_SKIP_LIST_KEY);
        revision++;
        saveConfigs();
    }

    public static List<String> blacklistIds() {
        init();
        return blockIds(ALL_MODE_BLOCK_BLACKLIST);
    }

    public static List<String> basicMaterialIds() {
        init();
        return blockIds(BASIC_MATERIALS);
    }

    public static List<String> expertSkipIds() {
        init();
        return blockIds(EXPERT_MODE_SKIP_LIST);
    }

    public static List<String> categoryLines() {
        init();
        return categoryLines(MATERIAL_CATEGORY_GROUPS);
    }

    public static List<String> defaultBlacklistIds() {
        return blockIds(MaterialConfigDefaults.ALL_MODE_BLOCK_BLACKLIST_BLOCKS.get());
    }

    public static List<String> defaultBasicMaterialIds() {
        return blockIds(MaterialConfigDefaults.BASIC_MATERIALS_BLOCKS.get());
    }

    public static List<String> defaultExpertSkipIds() {
        return blockIds(MaterialConfigDefaults.EXPERT_MODE_SKIP_LIST_BLOCKS.get());
    }

    public static List<String> defaultCategoryLines() {
        return categoryLines(MaterialConfigDefaults.MATERIAL_CATEGORY_GROUPS_BLOCKS.get());
    }

    public static void setDefault() {
        ALL_MODE_BLOCK_BLACKLIST = MaterialConfigDefaults.ALL_MODE_BLOCK_BLACKLIST_BLOCKS.get();
        BASIC_MATERIALS = MaterialConfigDefaults.BASIC_MATERIALS_BLOCKS.get();
        MATERIAL_CATEGORY_GROUPS = MaterialConfigDefaults.MATERIAL_CATEGORY_GROUPS_BLOCKS.get();
        EXPERT_MODE_SKIP_LIST = MaterialConfigDefaults.EXPERT_MODE_SKIP_LIST_BLOCKS.get();
    }

    private static void loadConfigs() {
        if (!Files.isRegularFile(configFile())) {
            setDefault();
            saveConfigs();
            return;
        }
        try (Reader reader = Files.newBufferedReader(configFile(), StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonObject()) {
                SimuKraft.LOGGER.warn("Simukraft: {} is not a JSON object, using material defaults in memory", configFile());
                setDefault();
                return;
            }
            applyRoot(root.getAsJsonObject());
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            SimuKraft.LOGGER.warn("Simukraft: Failed to read {}, using material defaults in memory", configFile(), exception);
            setDefault();
        }
    }

    private static void saveConfigs() {
        if (ALL_MODE_BLOCK_BLACKLIST == null || BASIC_MATERIALS == null
                || MATERIAL_CATEGORY_GROUPS == null || EXPERT_MODE_SKIP_LIST == null) {
            setDefault();
        }
        if (matchesDefaults()) {
            deleteConfigFile();
            return;
        }
        JsonObject root = new JsonObject();
        root.add(ALL_MODE_BLOCK_BLACKLIST_KEY, blockArray(ALL_MODE_BLOCK_BLACKLIST));
        root.add(BASIC_MATERIALS_KEY, blockArray(BASIC_MATERIALS));
        root.add(MATERIAL_CATEGORY_GROUPS_KEY, holderArray(MATERIAL_CATEGORY_GROUPS));
        root.add(EXPERT_MODE_SKIP_LIST_KEY, blockArray(EXPERT_MODE_SKIP_LIST));
        try {
            Path parent = configFile().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(configFile(), GSON.toJson(root) + "\n", StandardCharsets.UTF_8);
        } catch (IOException exception) {
            SimuKraft.LOGGER.warn("Simukraft: Failed to write {}", configFile(), exception);
        }
    }

    private static void applyRoot(JsonObject root) {
        ALL_MODE_BLOCK_BLACKLIST = readBlockSet(root.get(ALL_MODE_BLOCK_BLACKLIST_KEY), ALL_MODE_BLOCK_BLACKLIST_KEY,
                MaterialConfigDefaults.ALL_MODE_BLOCK_BLACKLIST_BLOCKS);
        BASIC_MATERIALS = readBlockSet(root.get(BASIC_MATERIALS_KEY), BASIC_MATERIALS_KEY,
                MaterialConfigDefaults.BASIC_MATERIALS_BLOCKS);
        MATERIAL_CATEGORY_GROUPS = readHolders(root.get(MATERIAL_CATEGORY_GROUPS_KEY));
        EXPERT_MODE_SKIP_LIST = readBlockSet(root.get(EXPERT_MODE_SKIP_LIST_KEY), EXPERT_MODE_SKIP_LIST_KEY,
                MaterialConfigDefaults.EXPERT_MODE_SKIP_LIST_BLOCKS);
    }

    private static Set<ResourceKey<Block>> readBlockSet(JsonElement element, String field) {
        return readBlockSet(element, field, null);
    }

    private static Set<ResourceKey<Block>> readBlockSet(JsonElement element, String field,
                                                        Supplier<Set<ResourceKey<Block>>> fallback) {
        if (element == null || element.isJsonNull()) {
            return fallback == null ? Set.of() : fallback.get();
        }
        if (!element.isJsonArray()) {
            SimuKraft.LOGGER.warn("Simukraft: material config field {} is not an array", field);
            return fallback == null ? Set.of() : fallback.get();
        }
        LinkedHashSet<ResourceKey<Block>> blocks = new LinkedHashSet<>();
        for (JsonElement entry : element.getAsJsonArray()) {
            ResourceKey<Block> key = parseBlockEntry(entry, field);
            if (key != null) {
                blocks.add(key);
            }
        }
        return Set.copyOf(blocks);
    }

    private static Set<ReplaceableMaterialHolder> readHolders(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return MaterialConfigDefaults.MATERIAL_CATEGORY_GROUPS_BLOCKS.get();
        }
        if (!element.isJsonArray()) {
            SimuKraft.LOGGER.warn("Simukraft: material config field {} is not an array", MATERIAL_CATEGORY_GROUPS_KEY);
            return MaterialConfigDefaults.MATERIAL_CATEGORY_GROUPS_BLOCKS.get();
        }
        Map<String, ReplaceableMaterialHolder> holders = new LinkedHashMap<>();
        List<JsonObject> objects = new ArrayList<>();
        for (JsonElement entry : element.getAsJsonArray()) {
            if (!entry.isJsonObject()) {
                SimuKraft.LOGGER.warn("Simukraft: skipped a material category group that is not an object");
                continue;
            }
            JsonObject object = entry.getAsJsonObject();
            String name = readName(object);
            if (name == null) {
                continue;
            }
            if (holders.containsKey(name)) {
                SimuKraft.LOGGER.warn("Simukraft: duplicate material category group {}, keeping the first", name);
                continue;
            }
            ReplaceableMaterialHolder holder = new ReplaceableMaterialHolder(name);
            readHolderBlocks(holder, object.get("blocks"));
            holder.syncSubReplaceableCount();
            holders.put(name, holder);
            objects.add(object);
        }
        for (JsonObject object : objects) {
            String name = readName(object);
            ReplaceableMaterialHolder holder = name == null ? null : holders.get(name);
            if (holder == null) {
                continue;
            }
            linkHolders(holder, object.get("holders"), holders);
        }
        return Set.copyOf(holders.values());
    }

    private static void readHolderBlocks(ReplaceableMaterialHolder holder, JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (!element.isJsonObject()) {
            SimuKraft.LOGGER.warn("Simukraft: material group {} blocks is not an object", holder.holderName);
            return;
        }
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            int subId;
            try {
                subId = Integer.parseInt(entry.getKey());
            } catch (NumberFormatException exception) {
                SimuKraft.LOGGER.warn("Simukraft: material group {} has a non-numeric block group {}", holder.holderName, entry.getKey());
                continue;
            }
            if (subId < 0 || !entry.getValue().isJsonArray()) {
                SimuKraft.LOGGER.warn("Simukraft: material group {} skipped block group {}", holder.holderName, entry.getKey());
                continue;
            }
            Set<ResourceKey<Block>> blocks = readBlockSet(entry.getValue(), holder.holderName + "." + subId);
            if (!blocks.isEmpty()) {
                holder.addReplaceableBlock(subId, blocks);
            }
        }
    }

    private static void linkHolders(ReplaceableMaterialHolder holder, JsonElement element, Map<String, ReplaceableMaterialHolder> holders) {
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (!element.isJsonArray()) {
            SimuKraft.LOGGER.warn("Simukraft: material group {} holders is not an array", holder.holderName);
            return;
        }
        for (JsonElement entry : element.getAsJsonArray()) {
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
                continue;
            }
            String targetName = entry.getAsString().trim();
            ReplaceableMaterialHolder target = holders.get(targetName);
            if (target == null) {
                SimuKraft.LOGGER.warn("Simukraft: material group {} references missing holder {}", holder.holderName, targetName);
                continue;
            }
            holder.addReplaceableHolder(target);
        }
    }

    private static String readName(JsonObject object) {
        JsonElement nameElement = object.get("name");
        if (nameElement == null || !nameElement.isJsonPrimitive() || !nameElement.getAsJsonPrimitive().isString()) {
            SimuKraft.LOGGER.warn("Simukraft: skipped a material category group without a name");
            return null;
        }
        String name = nameElement.getAsString().trim();
        if (name.isEmpty()) {
            SimuKraft.LOGGER.warn("Simukraft: skipped a material category group with a blank name");
            return null;
        }
        return name;
    }

    private static ResourceKey<Block> parseBlockEntry(JsonElement entry, String field) {
        if (entry == null || !entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString()) {
            SimuKraft.LOGGER.warn("Simukraft: skipped a non-string block id in {}", field);
            return null;
        }
        String raw = entry.getAsString().trim();
        if (raw.isEmpty()) {
            return null;
        }
        try {
            return ResourceKey.create(Registries.BLOCK, Identifier.parse(raw));
        } catch (RuntimeException exception) {
            SimuKraft.LOGGER.warn("Simukraft: skipped invalid block id {} in {}", raw, field);
            return null;
        }
    }

    private static JsonArray blockArray(Set<ResourceKey<Block>> blocks) {
        JsonArray array = new JsonArray();
        if (blocks == null) {
            return array;
        }
        blocks.stream()
                .map(key -> key.identifier().toString())
                .sorted()
                .forEach(array::add);
        return array;
    }

    private static JsonArray holderArray(Set<ReplaceableMaterialHolder> holders) {
        JsonArray array = new JsonArray();
        if (holders == null || holders.isEmpty()) {
            return array;
        }
        Map<String, ReplaceableMaterialHolder> byName = new TreeMap<>();
        for (ReplaceableMaterialHolder holder : collectHolders(holders)) {
            ReplaceableMaterialHolder previous = byName.putIfAbsent(holder.holderName, holder);
            if (previous != null && previous != holder) {
                SimuKraft.LOGGER.warn("Simukraft: duplicate material category group {} while saving, keeping the first", holder.holderName);
            }
        }
        for (ReplaceableMaterialHolder holder : byName.values()) {
            array.add(holderObject(holder, byName));
        }
        return array;
    }

    private static Set<ReplaceableMaterialHolder> collectHolders(Set<ReplaceableMaterialHolder> roots) {
        LinkedHashSet<ReplaceableMaterialHolder> collected = new LinkedHashSet<>();
        List<ReplaceableMaterialHolder> pending = new ArrayList<>(roots);
        while (!pending.isEmpty()) {
            ReplaceableMaterialHolder holder = pending.removeLast();
            if (holder == null || !collected.add(holder)) {
                continue;
            }
            pending.addAll(holder.replaceableHolders);
        }
        return collected;
    }

    private static JsonObject holderObject(ReplaceableMaterialHolder holder, Map<String, ReplaceableMaterialHolder> known) {
        JsonObject object = new JsonObject();
        object.addProperty("name", holder.holderName);
        JsonObject blocks = new JsonObject();
        holder.replaceableBlocks.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .forEach(entry -> blocks.add(Integer.toString(entry.getKey()), blockArray(entry.getValue())));
        object.add("blocks", blocks);
        JsonArray links = new JsonArray();
        holder.replaceableHolders.stream()
                .filter(known::containsValue)
                .map(link -> link.holderName)
                .distinct()
                .sorted()
                .forEach(links::add);
        object.add("holders", links);
        return object;
    }

    /**
     * 与默认材料配置一致时删掉已有文件。分组编号不参与比较。
     */
    private static void deleteConfigFile() {
        try {
            Files.deleteIfExists(configFile());
        } catch (IOException exception) {
            SimuKraft.LOGGER.warn("Simukraft: Failed to delete {}", configFile(), exception);
        }
    }

    private static boolean matchesDefaults() {
        return sameBlocks(ALL_MODE_BLOCK_BLACKLIST, MaterialConfigDefaults.ALL_MODE_BLOCK_BLACKLIST_BLOCKS.get())
                && sameBlocks(BASIC_MATERIALS, MaterialConfigDefaults.BASIC_MATERIALS_BLOCKS.get())
                && sameBlocks(EXPERT_MODE_SKIP_LIST, MaterialConfigDefaults.EXPERT_MODE_SKIP_LIST_BLOCKS.get())
                && sameHolders(MATERIAL_CATEGORY_GROUPS, MaterialConfigDefaults.MATERIAL_CATEGORY_GROUPS_BLOCKS.get());
    }

    private static boolean sameBlocks(Set<ResourceKey<Block>> current, Set<ResourceKey<Block>> defaults) {
        if (current == defaults) {
            return true;
        }
        if (current == null || defaults == null || current.size() != defaults.size()) {
            return false;
        }
        return current.equals(defaults);
    }

    private static boolean sameHolders(Set<ReplaceableMaterialHolder> current, Set<ReplaceableMaterialHolder> defaults) {
        Map<String, List<String>> currentGroups = holderGroups(current);
        Map<String, List<String>> defaultGroups = holderGroups(defaults);
        if (currentGroups == null || defaultGroups == null || !currentGroups.equals(defaultGroups)) {
            return false;
        }
        Map<String, List<String>> currentLinks = holderLinks(current);
        Map<String, List<String>> defaultLinks = holderLinks(defaults);
        return currentLinks != null && currentLinks.equals(defaultLinks);
    }

    /**
     * 返回每个分组名对应的方块组。同一名字出现两次时返回 null。不记录分组 id。
     */
    private static Map<String, List<String>> holderGroups(Set<ReplaceableMaterialHolder> holders) {
        Map<String, List<String>> groups = new HashMap<>();
        if (holders == null) {
            return groups;
        }
        for (ReplaceableMaterialHolder holder : collectHolders(holders)) {
            if (groups.containsKey(holder.holderName)) {
                return null;
            }
            List<String> blockGroups = new ArrayList<>();
            for (Set<ResourceKey<Block>> blocks : holder.replaceableBlocks.values()) {
                if (blocks == null || blocks.isEmpty()) {
                    continue;
                }
                blockGroups.add(blocks.stream()
                        .filter(Objects::nonNull)
                        .map(key -> key.identifier().toString())
                        .sorted()
                        .reduce((left, right) -> left + "," + right)
                        .orElse(""));
            }
            blockGroups.sort(Comparator.naturalOrder());
            groups.put(holder.holderName, blockGroups);
        }
        return groups;
    }

    private static Map<String, List<String>> holderLinks(Set<ReplaceableMaterialHolder> holders) {
        Map<String, ReplaceableMaterialHolder> byName = new HashMap<>();
        for (ReplaceableMaterialHolder holder : collectHolders(holders)) {
            if (byName.putIfAbsent(holder.holderName, holder) != null) {
                return null;
            }
        }
        Map<String, List<String>> links = new HashMap<>();
        for (ReplaceableMaterialHolder holder : byName.values()) {
            List<String> names = holder.replaceableHolders.stream()
                    .filter(Objects::nonNull)
                    .map(link -> link.holderName)
                    .distinct()
                    .sorted()
                    .toList();
            links.put(holder.holderName, names);
        }
        return links;
    }

    public static List<String> blockIds(Set<ResourceKey<Block>> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return List.of();
        }
        return blocks.stream().map(key -> key.identifier().toString()).sorted().toList();
    }

    public static List<String> categoryLines(Set<ReplaceableMaterialHolder> holders) {
        if (holders == null || holders.isEmpty()) {
            return List.of();
        }
        Map<String, ReplaceableMaterialHolder> byName = new TreeMap<>();
        for (ReplaceableMaterialHolder holder : collectHolders(holders)) {
            ReplaceableMaterialHolder previous = byName.putIfAbsent(holder.holderName, holder);
            if (previous != null && previous != holder) {
                SimuKraft.LOGGER.warn("Simukraft: duplicate material category group {} while exporting, keeping the first", holder.holderName);
            }
        }
        List<String> lines = new ArrayList<>();
        for (ReplaceableMaterialHolder holder : byName.values()) {
            String groups = holder.replaceableBlocks.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .map(entry -> entry.getKey() + "=" + joinIds(entry.getValue()))
                    .reduce((left, right) -> left + ";" + right)
                    .orElse("");
            String links = holder.replaceableHolders.stream()
                    .filter(Objects::nonNull)
                    .filter(byName::containsValue)
                    .map(link -> link.holderName)
                    .distinct()
                    .sorted()
                    .reduce((left, right) -> left + "," + right)
                    .orElse("");
            lines.add(holder.holderName + "|" + groups + "|" + links);
        }
        return List.copyOf(lines);
    }

    private static String joinIds(Set<ResourceKey<Block>> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return "";
        }
        return blocks.stream()
                .filter(Objects::nonNull)
                .map(key -> key.identifier().toString())
                .sorted()
                .reduce((left, right) -> left + "," + right)
                .orElse("");
    }

    private static Set<ResourceKey<Block>> parseIdSet(List<String> ids, String field) {
        LinkedHashSet<ResourceKey<Block>> blocks = new LinkedHashSet<>();
        if (ids == null) {
            return Set.of();
        }
        for (String raw : ids) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            try {
                blocks.add(ResourceKey.create(Registries.BLOCK, Identifier.parse(raw.trim())));
            } catch (RuntimeException exception) {
                SimuKraft.LOGGER.warn("Simukraft: skipped invalid block id {} in {}", raw, field);
            }
        }
        return Set.copyOf(blocks);
    }

    private static Set<ReplaceableMaterialHolder> parseCategoryLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return Set.of();
        }
        Map<String, ReplaceableMaterialHolder> holders = new LinkedHashMap<>();
        List<String[]> pendingLinks = new ArrayList<>();
        for (String rawLine : lines) {
            if (rawLine == null || rawLine.isBlank()) {
                continue;
            }
            String[] parts = rawLine.split("\\|", 3);
            String name = parts[0].trim();
            if (name.isEmpty()) {
                continue;
            }
            if (holders.containsKey(name)) {
                SimuKraft.LOGGER.warn("Simukraft: duplicate material category group {}, keeping the first", name);
                continue;
            }
            ReplaceableMaterialHolder holder = new ReplaceableMaterialHolder(name);
            if (parts.length > 1 && !parts[1].isBlank()) {
                for (String group : parts[1].split(";")) {
                    int separator = group.indexOf('=');
                    if (separator <= 0) {
                        continue;
                    }
                    int subId;
                    try {
                        subId = Integer.parseInt(group.substring(0, separator).trim());
                    } catch (NumberFormatException exception) {
                        continue;
                    }
                    if (subId < 0) {
                        continue;
                    }
                    List<String> rawIds = new ArrayList<>();
                    for (String piece : group.substring(separator + 1).split(",")) {
                        rawIds.add(piece);
                    }
                    Set<ResourceKey<Block>> blocks = parseIdSet(rawIds, name + "." + subId);
                    if (!blocks.isEmpty()) {
                        holder.addReplaceableBlock(subId, blocks);
                    }
                }
            }
            holder.syncSubReplaceableCount();
            holders.put(name, holder);
            pendingLinks.add(new String[]{name, parts.length > 2 ? parts[2] : ""});
        }
        for (String[] link : pendingLinks) {
            ReplaceableMaterialHolder holder = holders.get(link[0]);
            if (holder == null || link[1].isBlank()) {
                continue;
            }
            for (String targetName : link[1].split(",")) {
                ReplaceableMaterialHolder target = holders.get(targetName.trim());
                if (target != null) {
                    holder.addReplaceableHolder(target);
                }
            }
        }
        return Set.copyOf(holders.values());
    }
}
