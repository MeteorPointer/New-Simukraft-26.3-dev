package client.cn.nsimu.simukraft.client.config;

import com.lowdragmc.lowdraglib2.gui.texture.*;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.data.TextWrap;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.SearchComponent;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import com.lowdragmc.lowdraglib2.gui.ui.utils.UIElementProvider;
import com.lowdragmc.lowdraglib2.utils.search.IResultHandler;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;


final class SimuKraftMaterialCategoryGroupEditor {
    private static final int ROW_HEIGHT = 28;
    private static final int ICON_SIZE = 16;
    private static final int SUBGROUP_COLOR = 0xFFFFAA00;
    private static final int BLOCK_COLOR = 0xFF88AAFF;
    private static final int MAX_SEARCH_RESULTS = 16;
    private static final int HINT_HEIGHT = 22;

    private final CopyOnWriteArrayList<String> groupNames = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<String> availableItems = new CopyOnWriteArrayList<>(SimuKraftMaterialConfigItems.allItems());
    private final ConcurrentHashMap<String, TreeMap<Integer, CopyOnWriteArrayList<String>>> blockGroups = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> groupLinks = new ConcurrentHashMap<>();
    private final Consumer<List<String>> onChanged;
    private final UIElement groupList = SimuKraftConfigWidgets.scrollColumn(0, 4);
    private final UIElement subgroupList = SimuKraftConfigWidgets.scrollColumn(0, 4);
    private final UIElement linkList = SimuKraftConfigWidgets.scrollColumn(0, 4);
    private final UIElement blockList = SimuKraftConfigWidgets.scrollColumn(0, 4);
    private TextField groupField;
    private TextField linkField;
    private String selectedGroup;
    private int selectedSubId;

    private SimuKraftMaterialCategoryGroupEditor(List<String> values, Consumer<List<String>> onChanged) {
        this.onChanged = onChanged;
        parse(values);
        selectedGroup = groupNames.isEmpty() ? null : groupNames.getFirst();
        selectedSubId = 0;
    }

    /**
     * create: 创建材料分组编辑器，编辑分组编号、组内方块和上游分组。
     */
    static UIElement create(Component title, Component hint, List<String> values, Consumer<List<String>> onChanged) {
        SimuKraftMaterialCategoryGroupEditor editor = new SimuKraftMaterialCategoryGroupEditor(values, onChanged);
        return editor.build(title, hint);
    }

    private UIElement build(Component title, Component hint) {
        UIElement root = SimuKraftConfigWidgets.column(6, 4);
        root.addChild(SimuKraftConfigWidgets.compactSection(title));
        root.addChild(SimuKraftConfigWidgets.label(hint, Horizontal.LEFT, SimuKraftConfigWidgets.TEXT_MUTED, HINT_HEIGHT, TextWrap.WRAP));
        root.addChild(useStackedLayout() ? stackedContent() : splitContent());
        refreshAll();
        return root;
    }

    private UIElement splitContent() {
        UIElement right = SimuKraftConfigWidgets.split(
                "config.material.category.items.split",
                50F, 30F, 70F,
                structurePanel(),
                blockPanel());
        return SimuKraftConfigWidgets.split(
                "config.material.category.group.split",
                32F, 22F, 48F,
                groupPanel(),
                right);
    }

    private static boolean useStackedLayout() {
        var gui = Minecraft.getInstance().getWindow();
        return gui.getGuiScaledWidth() < 560;
    }

    private UIElement stackedContent() {
        UIElement column = SimuKraftConfigWidgets.scrollColumn(0, 6);
        column.addChild(stackedPanel(groupPanel(), 140));
        column.addChild(stackedPanel(structurePanel(), 260));
        column.addChild(stackedPanel(blockPanel(), 160));
        return SimuKraftConfigWidgets.scroller(column);
    }

    private UIElement stackedPanel(UIElement panel, int height) {
        return panel.layout(layout -> {
            layout.widthPercent(100);
            layout.height(height);
            layout.flexShrink(0);
        });
    }

    private UIElement groupPanel() {
        UIElement panel = panel();
        panel.addChild(title(Component.translatable("gui.simukraft.config.material.groups"), SimuKraftConfigWidgets.TEXT_TITLE));
        panel.addChild(inputRow(Component.translatable("gui.simukraft.config.material.group_name"), field -> groupField = field, this::addGroup));
        panel.addChild(SimuKraftConfigWidgets.scroller(groupList, false));
        panel.addChild(SimuKraftConfigWidgets.button(Component.translatable("gui.simukraft.config.material.delete_group"), this::deleteSelectedGroup, true).layout(layout -> {
            layout.widthPercent(100);
            layout.height(22);
            layout.flexShrink(0);
        }));
        return panel;
    }

    private UIElement structurePanel() {
        UIElement panel = panel();
        panel.addChild(title(Component.translatable("gui.simukraft.config.material.subgroups"), SUBGROUP_COLOR));
        panel.addChild(SimuKraftConfigWidgets.scroller(subgroupList, false));
        panel.addChild(SimuKraftConfigWidgets.button(Component.translatable("gui.simukraft.config.material.add_subgroup"), this::addSubgroup, true).layout(layout -> {
            layout.widthPercent(100);
            layout.height(22);
            layout.flexShrink(0);
        }));
        panel.addChild(SimuKraftConfigWidgets.button(Component.translatable("gui.simukraft.config.material.delete_subgroup"), this::deleteSelectedSubgroup, true).layout(layout -> {
            layout.widthPercent(100);
            layout.height(22);
            layout.flexShrink(0);
        }));
        panel.addChild(title(Component.translatable("gui.simukraft.config.material.links"), SimuKraftConfigWidgets.TEXT_TITLE));
        panel.addChild(inputRow(Component.translatable("gui.simukraft.config.material.link_name"), field -> linkField = field, this::addLink));
        panel.addChild(SimuKraftConfigWidgets.scroller(linkList, false));
        return panel;
    }

    private UIElement blockPanel() {
        UIElement panel = panel();
        panel.addChild(title(Component.translatable("gui.simukraft.config.material.blocks"), BLOCK_COLOR));
        panel.addChild(itemSearchRow(this::addBlock));
        panel.addChild(SimuKraftConfigWidgets.scroller(blockList, false));
        return panel;
    }

    private UIElement panel() {
        return new UIElement()
                .style(style -> style.backgroundTexture(new GuiTextureGroup(
                        new ColorRectTexture(SimuKraftConfigWidgets.PANEL_BG),
                        new ColorBorderTexture(1, 0xFF5A5A5A))))
                .setOverflowVisible(false)
                .layout(layout -> {
                    layout.widthPercent(100);
                    layout.heightPercent(100);
                    layout.flexDirection(FlexDirection.COLUMN);
                    layout.alignItems(AlignItems.STRETCH);
                    layout.paddingAll(6);
                    layout.gapAll(6);
                });
    }

    private UIElement title(Component text, int color) {
        return SimuKraftConfigWidgets.label(text, Horizontal.LEFT, color, 16, TextWrap.HIDE);
    }

    private UIElement inputRow(Component placeholder, FieldSetter setter, Runnable addAction) {
        UIElement row = new UIElement().layout(layout -> {
            layout.widthPercent(100);
            layout.height(24);
            layout.flexDirection(FlexDirection.ROW);
            layout.alignItems(AlignItems.CENTER);
            layout.gapAll(4);
            layout.flexShrink(0);
        });
        TextField field = SimuKraftConfigWidgets.textField("", value -> {
        });
        field.textFieldStyle(style -> style.placeholder(placeholder));
        field.layout(layout -> {
            layout.flex(1);
            layout.height(22);
        });
        setter.set(field);
        row.addChild(field);
        row.addChild(SimuKraftConfigWidgets.button(Component.literal("+"), addAction, true).layout(layout -> {
            layout.width(28);
            layout.height(22);
            layout.flexShrink(0);
        }));
        return row;
    }

    private SearchComponent<String> itemSearchRow(Consumer<String> addAction) {
        SearchComponent<String> search = new SearchComponent<>(new SearchComponent.ISearchUI<>() {
            @Override
            public void search(String word, IResultHandler<String> handler) {
                String query = word == null ? "" : word.toLowerCase(Locale.ROOT);
                int accepted = 0;
                for (String itemId : availableItems) {
                    if (matches(itemId, query)) {
                        handler.acceptResult(itemId);
                        if (++accepted >= MAX_SEARCH_RESULTS) {
                            return;
                        }
                    }
                }
            }

            @Override
            public String resultText(String result) {
                return result == null ? "" : result;
            }

            @Override
            public void onResultSelected(String result) {
                addAction.accept(result);
            }
        });
        search.setCandidateUIProvider((UIElementProvider<String>) value -> value == null ? new UIElement() : searchCandidate(value));
        search.searchStyle(style -> style.maxItemCount(8).closeAfterSelect(true));
        search.textField.getTextFieldStyle().placeholder(Component.translatable("gui.simukraft.config.material.item_id"));
        search.layout(layout -> {
            layout.widthPercent(100);
            layout.height(24);
            layout.flexShrink(0);
        });
        return search;
    }

    private boolean matches(String itemId, String query) {
        if (query.isBlank()) {
            return true;
        }
        return itemId.toLowerCase(Locale.ROOT).contains(query)
                || SimuKraftMaterialConfigItems.displayName(itemId).getString().toLowerCase(Locale.ROOT).contains(query);
    }

    private UIElement searchCandidate(String itemId) {
        UIElement row = new UIElement().layout(layout -> {
            layout.widthPercent(100);
            layout.height(24);
            layout.flexDirection(FlexDirection.ROW);
            layout.alignItems(AlignItems.CENTER);
            layout.gapAll(4);
            layout.paddingAll(3);
        });
        row.setOverflowVisible(false);
        row.addChild(new UIElement().layout(layout -> {
            layout.width(ICON_SIZE);
            layout.height(ICON_SIZE);
            layout.flexShrink(0);
        }).style(style -> style.backgroundTexture(new ItemStackTexture(SimuKraftMaterialConfigItems.stack(itemId)))));
        UIElement text = new UIElement().layout(layout -> {
            layout.flex(1);
            layout.height(20);
            layout.flexDirection(FlexDirection.COLUMN);
            layout.alignItems(AlignItems.STRETCH);
        });
        text.setOverflowVisible(false);
        text.addChild(SimuKraftConfigWidgets.label(SimuKraftMaterialConfigItems.displayName(itemId), Horizontal.LEFT, SimuKraftConfigWidgets.TEXT, 10, TextWrap.HOVER_ROLL));
        text.addChild(SimuKraftConfigWidgets.label(Component.literal(itemId), Horizontal.LEFT, SimuKraftConfigWidgets.TEXT_MUTED, 10, TextWrap.HOVER_ROLL));
        row.addChild(text);
        return row;
    }

    private void refreshAll() {
        refreshGroups();
        refreshSubgroups();
        refreshLinks();
        refreshBlocks();
    }

    private void refreshGroups() {
        groupList.clearAllChildren();
        if (groupNames.isEmpty()) {
            groupList.addChild(emptyRow());
            return;
        }
        for (String groupName : groupNames) {
            groupList.addChild(groupRow(groupName));
        }
    }

    private Button groupRow(String groupName) {
        boolean selected = groupName.equals(selectedGroup);
        Button row = rowButton(() -> {
            selectedGroup = groupName;
            selectedSubId = 0;
            ensureSubgroup(groupName, 0);
            refreshAll();
        }, selected ? 0xFF5555AA : SimuKraftConfigWidgets.CARD_BG);
        row.addChild(SimuKraftConfigWidgets.label(Component.literal(groupName), Horizontal.LEFT, selected ? SimuKraftConfigWidgets.TEXT_TITLE : SimuKraftConfigWidgets.TEXT, ROW_HEIGHT - 8, TextWrap.HOVER_ROLL).layout(layout -> {
            layout.flex(1);
            layout.height(ROW_HEIGHT - 8);
        }));
        return row;
    }

    private void refreshSubgroups() {
        subgroupList.clearAllChildren();
        TreeMap<Integer, CopyOnWriteArrayList<String>> groups = selectedBlocks();
        if (groups.isEmpty()) {
            subgroupList.addChild(emptyRow());
            return;
        }
        for (Integer subId : groups.keySet()) {
            subgroupList.addChild(subgroupRow(subId));
        }
    }

    private Button subgroupRow(int subId) {
        boolean selected = subId == selectedSubId;
        Button row = rowButton(() -> {
            selectedSubId = subId;
            refreshAll();
        }, selected ? 0xFF5555AA : SimuKraftConfigWidgets.CARD_BG);
        Component label = subId == 0
                ? Component.translatable("gui.simukraft.config.material.subgroup_fixed")
                : Component.translatable("gui.simukraft.config.material.subgroup_replaceable", subId);
        row.addChild(SimuKraftConfigWidgets.label(label, Horizontal.LEFT, SUBGROUP_COLOR, ROW_HEIGHT - 8, TextWrap.HOVER_ROLL).layout(layout -> {
            layout.flex(1);
            layout.height(ROW_HEIGHT - 8);
        }));
        return row;
    }

    private void refreshLinks() {
        linkList.clearAllChildren();
        List<String> links = selectedGroup == null ? List.of() : groupLinks.getOrDefault(selectedGroup, new CopyOnWriteArrayList<>());
        if (links.isEmpty()) {
            linkList.addChild(emptyRow());
            return;
        }
        for (String link : links) {
            linkList.addChild(textRow(link, () -> removeLink(link)));
        }
    }

    private void refreshBlocks() {
        blockList.clearAllChildren();
        List<String> blocks = selectedBlocks().getOrDefault(selectedSubId, new CopyOnWriteArrayList<>());
        if (blocks.isEmpty()) {
            blockList.addChild(emptyRow());
            return;
        }
        for (String itemId : blocks) {
            blockList.addChild(itemRow(itemId, () -> removeBlock(itemId)));
        }
    }

    private Button itemRow(String itemId, Runnable deleteAction) {
        Button row = rowButton(() -> {
        }, SimuKraftConfigWidgets.CARD_BG);
        row.addChild(new UIElement().layout(layout -> {
            layout.width(ICON_SIZE);
            layout.height(ICON_SIZE);
            layout.flexShrink(0);
        }).style(style -> style.backgroundTexture(new ItemStackTexture(SimuKraftMaterialConfigItems.stack(itemId)))));
        UIElement text = new UIElement().layout(layout -> {
            layout.flex(1);
            layout.height(ROW_HEIGHT - 8);
            layout.flexDirection(FlexDirection.COLUMN);
            layout.alignItems(AlignItems.STRETCH);
        });
        text.setOverflowVisible(false);
        text.addChild(SimuKraftConfigWidgets.label(SimuKraftMaterialConfigItems.displayName(itemId), Horizontal.LEFT, BLOCK_COLOR, 12, TextWrap.HOVER_ROLL));
        text.addChild(SimuKraftConfigWidgets.label(Component.literal(itemId), Horizontal.LEFT, SimuKraftConfigWidgets.TEXT_MUTED, 10, TextWrap.HOVER_ROLL));
        row.addChild(text);
        row.addChild(SimuKraftConfigWidgets.button(Component.literal("X"), deleteAction, true).layout(layout -> {
            layout.width(22);
            layout.height(20);
            layout.flexShrink(0);
        }));
        return row;
    }

    private Button textRow(String value, Runnable deleteAction) {
        Button row = rowButton(() -> {
        }, SimuKraftConfigWidgets.CARD_BG);
        row.addChild(SimuKraftConfigWidgets.label(Component.literal(value), Horizontal.LEFT, SimuKraftConfigWidgets.TEXT, ROW_HEIGHT - 8, TextWrap.HOVER_ROLL).layout(layout -> {
            layout.flex(1);
            layout.height(ROW_HEIGHT - 8);
        }));
        row.addChild(SimuKraftConfigWidgets.button(Component.literal("X"), deleteAction, true).layout(layout -> {
            layout.width(22);
            layout.height(20);
            layout.flexShrink(0);
        }));
        return row;
    }

    private Button rowButton(Runnable action, int color) {
        Button row = new Button().noText();
        row.buttonStyle(style -> style.baseTexture(rowTexture(color)).hoverTexture(rowTexture(0xFF454545)).pressedTexture(rowTexture(0xFF505050)));
        row.setOnClick(event -> action.run());
        row.layout(layout -> {
            layout.widthPercent(100);
            layout.height(ROW_HEIGHT);
            layout.flexDirection(FlexDirection.ROW);
            layout.alignItems(AlignItems.CENTER);
            layout.gapAll(4);
            layout.paddingAll(4);
            layout.flexShrink(0);
        });
        return row;
    }

    private UIElement emptyRow() {
        return SimuKraftConfigWidgets.label(Component.translatable("gui.simukraft.config.list.empty"), Horizontal.CENTER, SimuKraftConfigWidgets.TEXT_MUTED, 24, TextWrap.HIDE);
    }

    private IGuiTexture rowTexture(int color) {
        return new GuiTextureGroup(new ColorRectTexture(color), new ColorBorderTexture(1, 0xFF5A5A5A));
    }

    private void addGroup() {
        if (groupField == null) {
            return;
        }
        String groupName = groupField.getText().trim();
        if (!validName(groupName) || groupNames.contains(groupName)) {
            return;
        }
        groupNames.add(groupName);
        groupNames.sort(String::compareTo);
        TreeMap<Integer, CopyOnWriteArrayList<String>> groups = new TreeMap<>();
        groups.put(0, new CopyOnWriteArrayList<>());
        blockGroups.put(groupName, groups);
        groupLinks.put(groupName, new CopyOnWriteArrayList<>());
        selectedGroup = groupName;
        selectedSubId = 0;
        groupField.setText("", false);
        publishAndRefresh();
    }

    private void deleteSelectedGroup() {
        if (selectedGroup == null) {
            return;
        }
        String removed = selectedGroup;
        groupNames.remove(removed);
        blockGroups.remove(removed);
        groupLinks.remove(removed);
        for (CopyOnWriteArrayList<String> links : groupLinks.values()) {
            links.remove(removed);
        }
        selectedGroup = groupNames.isEmpty() ? null : groupNames.getFirst();
        selectedSubId = 0;
        publishAndRefresh();
    }

    private void addSubgroup() {
        if (selectedGroup == null) {
            return;
        }
        TreeMap<Integer, CopyOnWriteArrayList<String>> groups = selectedBlocks();
        int next = groups.isEmpty() ? 1 : groups.lastKey() + 1;
        if (next <= 0) {
            next = 1;
        }
        groups.put(next, new CopyOnWriteArrayList<>());
        selectedSubId = next;
        publishAndRefresh();
    }

    private void deleteSelectedSubgroup() {
        if (selectedGroup == null || selectedSubId == 0) {
            return;
        }
        selectedBlocks().remove(selectedSubId);
        selectedSubId = 0;
        ensureSubgroup(selectedGroup, 0);
        publishAndRefresh();
    }

    private void addLink() {
        if (selectedGroup == null || linkField == null) {
            return;
        }
        String linkName = linkField.getText().trim();
        if (!groupNames.contains(linkName) || linkName.equals(selectedGroup)) {
            return;
        }
        CopyOnWriteArrayList<String> links = groupLinks.computeIfAbsent(selectedGroup, key -> new CopyOnWriteArrayList<>());
        if (!links.contains(linkName)) {
            links.add(linkName);
            links.sort(String::compareTo);
            publish();
        }
        linkField.setText("", false);
        refreshAll();
    }

    private void removeLink(String linkName) {
        if (selectedGroup == null) {
            return;
        }
        CopyOnWriteArrayList<String> links = groupLinks.get(selectedGroup);
        if (links != null && links.remove(linkName)) {
            publishAndRefresh();
        }
    }

    private void addBlock(String rawId) {
        if (selectedGroup == null) {
            return;
        }
        String itemId = SimuKraftMaterialConfigItems.cleanId(rawId, true);
        if (itemId.isBlank()) {
            return;
        }
        CopyOnWriteArrayList<String> blocks = ensureSubgroup(selectedGroup, selectedSubId);
        if (!blocks.contains(itemId)) {
            blocks.add(itemId);
            publish();
        }
        refreshAll();
    }

    private void removeBlock(String itemId) {
        CopyOnWriteArrayList<String> blocks = selectedBlocks().get(selectedSubId);
        if (blocks != null && blocks.remove(itemId)) {
            publishAndRefresh();
        }
    }

    private TreeMap<Integer, CopyOnWriteArrayList<String>> selectedBlocks() {
        if (selectedGroup == null) {
            return new TreeMap<>();
        }
        return blockGroups.computeIfAbsent(selectedGroup, key -> {
            TreeMap<Integer, CopyOnWriteArrayList<String>> groups = new TreeMap<>();
            groups.put(0, new CopyOnWriteArrayList<>());
            return groups;
        });
    }

    private CopyOnWriteArrayList<String> ensureSubgroup(String groupName, int subId) {
        TreeMap<Integer, CopyOnWriteArrayList<String>> groups = blockGroups.computeIfAbsent(groupName, key -> new TreeMap<>());
        return groups.computeIfAbsent(subId, key -> new CopyOnWriteArrayList<>());
    }

    private void publishAndRefresh() {
        publish();
        refreshAll();
    }

    private void publish() {
        onChanged.accept(serialize());
    }

    private List<String> serialize() {
        List<String> values = new ArrayList<>();
        for (String groupName : groupNames) {
            TreeMap<Integer, CopyOnWriteArrayList<String>> groups = blockGroups.getOrDefault(groupName, new TreeMap<>());
            List<String> groupParts = new ArrayList<>();
            for (Map.Entry<Integer, CopyOnWriteArrayList<String>> entry : groups.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    continue;
                }
                List<String> ids = new ArrayList<>(entry.getValue());
                ids.sort(Comparator.naturalOrder());
                groupParts.add(entry.getKey() + "=" + String.join(",", ids));
            }
            List<String> links = new ArrayList<>(groupLinks.getOrDefault(groupName, new CopyOnWriteArrayList<>()));
            links.sort(Comparator.naturalOrder());
            values.add(groupName + "|" + String.join(";", groupParts) + "|" + String.join(",", links));
        }
        values.sort(Comparator.naturalOrder());
        return values;
    }

    private void parse(List<String> values) {
        if (values == null) {
            return;
        }
        for (String entry : values) {
            if (entry == null || entry.isBlank() || !entry.contains("|")) {
                continue;
            }
            String[] parts = entry.split("\\|", 3);
            String name = parts[0].trim();
            if (!validName(name) || groupNames.contains(name)) {
                continue;
            }
            TreeMap<Integer, CopyOnWriteArrayList<String>> groups = new TreeMap<>();
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
                    CopyOnWriteArrayList<String> blocks = new CopyOnWriteArrayList<>();
                    for (String raw : group.substring(separator + 1).split(",")) {
                        String id = SimuKraftMaterialConfigItems.cleanId(raw, true);
                        if (!id.isBlank() && !blocks.contains(id)) {
                            blocks.add(id);
                        }
                    }
                    if (!blocks.isEmpty()) {
                        groups.put(subId, blocks);
                    }
                }
            }
            groups.putIfAbsent(0, new CopyOnWriteArrayList<>());
            CopyOnWriteArrayList<String> links = new CopyOnWriteArrayList<>();
            if (parts.length > 2 && !parts[2].isBlank()) {
                for (String rawLink : parts[2].split(",")) {
                    String link = rawLink.trim();
                    if (validName(link) && !links.contains(link)) {
                        links.add(link);
                    }
                }
            }
            groupNames.add(name);
            blockGroups.put(name, groups);
            groupLinks.put(name, links);
        }
        groupNames.sort(String::compareTo);
    }

    private static boolean validName(String name) {
        return name != null && !name.isBlank() && !name.contains("|") && !name.contains(";") && !name.contains("=") && !name.contains(",");
    }

    private interface FieldSetter {
        void set(TextField field);
    }
}
