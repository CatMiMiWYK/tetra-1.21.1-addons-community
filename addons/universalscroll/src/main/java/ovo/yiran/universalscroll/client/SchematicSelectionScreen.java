package ovo.yiran.universalscroll.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import ovo.yiran.universalscroll.net.SelectSchematicPayload;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.gui.GuiTextures;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.data.GlyphData;
import se.mickelus.tetra.module.schematic.SchematicRarity;
import se.mickelus.tetra.module.schematic.SchematicType;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 图纸选择界面：枚举当前已加载的全部 Tetra schematic（本体 + 附属），支持搜索、图标、来源/类型分类。
 * 选中后由服务端把玩家带到对应工作台图纸，不在这里伪造任何合成逻辑。
 */
public class SchematicSelectionScreen extends Screen {
    private static final int PANEL_WIDTH = 380;
    private static final int ROW_HEIGHT = 18;
    private static final int ICON_SIZE = 16;

    private final BlockPos workbenchPos;

    private final List<Entry> allEntries = new ArrayList<>();
    private List<Entry> visibleEntries = new ArrayList<>();

    private EditBox search;
    private int scrollOffset;
    private int panelX;
    private int panelY;
    private int panelHeight;
    private int listTop;
    private int listBottom;
    private int rowsVisible;

    public SchematicSelectionScreen(@Nullable BlockPos workbenchPos) {
        super(Component.translatable("gui.universalscroll.title"));
        this.workbenchPos = workbenchPos;
    }

    @Override
    protected void init() {
        super.init();

        panelHeight = Math.min(250, this.height - 40);
        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = (this.height - panelHeight) / 2;
        listTop = panelY + 46;
        listBottom = panelY + panelHeight - 36;
        rowsVisible = Math.max(1, (listBottom - listTop) / ROW_HEIGHT);

        search = new EditBox(this.font, panelX + 10, panelY + 22, PANEL_WIDTH - 20, 18,
                Component.translatable("gui.universalscroll.search"));
        search.setMaxLength(64);
        search.setHint(Component.translatable("gui.universalscroll.search_hint"));
        search.setResponder(value -> {
            scrollOffset = 0;
            refresh(value);
        });
        addRenderableWidget(search);

        allEntries.clear();
        buildEntries();
        refresh(search.getValue());
        setInitialFocus(search);
        search.setFocused(true);
    }

    private void buildEntries() {
        List<UpgradeSchematic> schematics = new ArrayList<>(SchematicRegistry.getAllSchematics());
        schematics.sort(Comparator
                .comparing((UpgradeSchematic schematic) -> String.join(",", safeSources(schematic)))
                .thenComparing(schematic -> safeType(schematic).name())
                .thenComparing(UpgradeSchematic::getName, String.CASE_INSENSITIVE_ORDER));

        for (UpgradeSchematic schematic : schematics) {
            GlyphData glyph = schematic.getGlyph();
            ResourceLocation texture = glyph != null && glyph.textureLocation != null ? glyph.textureLocation : GuiTextures.glyphs;
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(TetraMod.MOD_ID, schematic.getKey());
            SchematicRarity rarity = schematic.getRarity();
            int tint = (rarity != null ? rarity.tint : 0xFFFFFF) | 0xFF000000;

            allEntries.add(new Entry(schematic, id, schematic.getName(), safeSources(schematic), safeType(schematic), tint,
                    texture, glyph != null ? glyph.textureX : 0, glyph != null ? glyph.textureY : 0));
        }
    }

    private void refresh(String query) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) {
            visibleEntries = new ArrayList<>(allEntries);
        } else {
            visibleEntries = allEntries.stream()
                    .filter(entry -> entry.searchText().contains(needle))
                    .collect(Collectors.toList());
        }

        int maxOffset = Math.max(0, visibleEntries.size() - rowsVisible);
        scrollOffset = Math.min(scrollOffset, maxOffset);
    }

    private List<String> safeSources(UpgradeSchematic schematic) {
        String[] sources = schematic.getSources();
        return sources == null ? List.of("unknown") : Arrays.asList(sources);
    }

    private SchematicType safeType(UpgradeSchematic schematic) {
        SchematicType type = schematic.getType();
        return type != null ? type : SchematicType.other;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xC0000000);

        graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + panelHeight, 0xF0101016);
        graphics.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + 1, 0xFF44445A);
        graphics.fill(panelX, panelY + panelHeight - 1, panelX + PANEL_WIDTH, panelY + panelHeight, 0xFF44445A);
        graphics.fill(panelX, panelY, panelX + 1, panelY + panelHeight, 0xFF44445A);
        graphics.fill(panelX + PANEL_WIDTH - 1, panelY, panelX + PANEL_WIDTH, panelY + panelHeight, 0xFF44445A);

        graphics.drawCenteredString(this.font, Component.translatable("gui.universalscroll.title_count", visibleEntries.size()),
                panelX + PANEL_WIDTH / 2, panelY + 7, 0xFFFFFFFF);

        this.search.render(graphics, mouseX, mouseY, partialTick);

        graphics.enableScissor(panelX + 1, listTop, panelX + PANEL_WIDTH - 1, listBottom);
        for (int row = 0; row < rowsVisible; row++) {
            int index = scrollOffset + row;
            if (index >= visibleEntries.size()) {
                break;
            }

            Entry entry = visibleEntries.get(index);
            int rowY = listTop + row * ROW_HEIGHT;
            int rowColor = index % 2 == 0 ? 0x14FFFFFF : 0x08FFFFFF;
            graphics.fill(panelX + 1, rowY, panelX + PANEL_WIDTH - 1, rowY + ROW_HEIGHT - 1, rowColor);

            if (isInside(mouseX, mouseY, panelX + 1, rowY, PANEL_WIDTH - 2, ROW_HEIGHT - 1)) {
                graphics.fill(panelX + 1, rowY, panelX + PANEL_WIDTH - 1, rowY + ROW_HEIGHT - 1, 0x30FFFFFF);
            }

            graphics.blit(entry.glyphTexture(), panelX + 5, rowY + 1, entry.glyphX(), entry.glyphY(), ICON_SIZE, ICON_SIZE);
            graphics.drawString(this.font, trim(entry.name(), PANEL_WIDTH - 150), panelX + 25, rowY + 5, entry.tint(), false);

            String meta = trim(entry.type().name().toLowerCase(Locale.ROOT) + " · " + String.join(", ", entry.sources()),
                    PANEL_WIDTH - 160);
            graphics.drawString(this.font, meta, panelX + PANEL_WIDTH - 8 - this.font.width(meta), rowY + 5, 0xFF8A8A99, false);
        }
        graphics.disableScissor();

        Entry hovered = entryAt(mouseX, mouseY);
        if (hovered != null) {
            graphics.fill(panelX + 1, listBottom, panelX + PANEL_WIDTH - 1, listBottom + 1, 0x30FFFFFF);
            graphics.drawString(this.font, trim(hovered.description(), PANEL_WIDTH - 20), panelX + 10, listBottom + 4, 0xFFB8B8C8, false);
            String detail = hovered.requirementText();
            graphics.drawString(this.font, trim(detail == null ? hovered.id().toString() : detail, PANEL_WIDTH - 20),
                    panelX + 10, listBottom + 15, 0xFF6F6F80, false);
        } else {
            Component hint = workbenchPos == null
                    ? Component.translatable("gui.universalscroll.need_workbench")
                    : Component.translatable("gui.universalscroll.hint");
            graphics.drawString(this.font, hint, panelX + 10, listBottom + 8, workbenchPos == null ? 0xFFCC8844 : 0xFF6F6F80, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.search.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        this.search.setFocused(false);

        Entry entry = entryAt(mouseX, mouseY);
        if (button == 0 && entry != null) {
            select(entry);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= panelX && mouseX <= panelX + PANEL_WIDTH && mouseY >= listTop && mouseY <= listBottom) {
            int maxOffset = Math.max(0, visibleEntries.size() - rowsVisible);
            scrollOffset = Math.max(0, Math.min(maxOffset, scrollOffset - (int) Math.signum(scrollY) * 2));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.search.isFocused() && this.search.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.search.isFocused() && this.search.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Nullable
    private Entry entryAt(double mouseX, double mouseY) {
        if (mouseX < panelX + 1 || mouseX > panelX + PANEL_WIDTH - 1 || mouseY < listTop || mouseY >= listBottom) {
            return null;
        }

        int row = (int) ((mouseY - listTop) / ROW_HEIGHT);
        int index = scrollOffset + row;
        if (row < 0 || row >= rowsVisible || index < 0 || index >= visibleEntries.size()) {
            return null;
        }
        return visibleEntries.get(index);
    }

    private void select(Entry entry) {
        Minecraft minecraft = this.minecraft;
        if (workbenchPos == null) {
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.displayClientMessage(Component.translatable("message.universalscroll.need_workbench"), true);
            }
            return;
        }

        PacketDistributor.sendToServer(new SelectSchematicPayload(workbenchPos, entry.id()));
        this.onClose();
    }

    private String trim(String text, int maxWidth) {
        if (text == null) {
            return "";
        }
        if (this.font.width(text) <= maxWidth) {
            return text;
        }
        return this.font.plainSubstrByWidth(text, Math.max(0, maxWidth - 6)) + "...";
    }

    private static boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private record Entry(UpgradeSchematic schematic, ResourceLocation id, String name, List<String> sources,
                         SchematicType type, int tint, ResourceLocation glyphTexture, int glyphX, int glyphY) {
        String searchText() {
            return (name + " " + id + " " + String.join(",", sources)).toLowerCase(Locale.ROOT);
        }

        String description() {
            String description = schematic.getDescription(ItemStack.EMPTY);
            return description == null || description.isBlank() ? id.toString() : description;
        }

        @Nullable
        String requirementText() {
            List<Component> requirement = schematic.getRequirementDescription();
            if (requirement == null || requirement.isEmpty()) {
                return null;
            }
            return requirement.stream().map(Component::getString).collect(Collectors.joining(" / "));
        }
    }
}
