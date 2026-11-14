package net.yiran.tetrajs.items;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.yiran.tetrajs.TetraJS;
import net.yiran.tetrajs.api.ShowModelHelper;
import net.yiran.tetrajs.util.NbtSlotData;
import se.mickelus.mutil.network.PacketHandler;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.gui.GuiModuleOffsets;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.data.SynergyData;
import se.mickelus.tetra.module.model.IModuleModel;
import se.mickelus.tetra.module.schematic.RepairSchematic;
import se.mickelus.tetra.util.ItemStackTagHelper;

public class DynamicModularItem extends ItemModularHandheld {
    public Cache<String, List<NbtSlotData>> slotCache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterWrite(5L, TimeUnit.MINUTES).build();
    public Cache<String, List<String>> showModuleCache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterWrite(5L, TimeUnit.MINUTES).build();
    public String[] synergiesPath;

    public DynamicModularItem(ResourceLocation itemID, boolean canHone, int honeBase, int honeIntegrityMultiplier, String[] synergiesPath) {
        super(new Item.Properties().stacksTo(1));
        SchematicRegistry.instance.registerSchematic(new RepairSchematic(this, itemID.getPath()));
        this.canHone = canHone;
        this.honeBase = honeBase;
        this.honeIntegrityMultiplier = honeIntegrityMultiplier;
        this.synergiesPath = synergiesPath;
        TetraJS.items.add(this);
    }

    @Override
    public void commonInit(PacketHandler packetHandler) {
        DataManager.instance.synergyData.onReload(() -> this.synergies = DataManager.instance.synergyData.getData().entrySet().stream()
                .filter(entry -> entry.getKey().getNamespace().equals("tetra"))
                .filter(entry -> Arrays.stream(this.synergiesPath).anyMatch(s -> entry.getKey().getPath().startsWith(s)))
                .map(Map.Entry::getValue)
                .flatMap(Arrays::stream)
                .toArray(SynergyData[]::new));
    }

    public List<NbtSlotData> getSlotData(ItemStack itemStack) {
        String id = this.getIdentifier(itemStack);
        if (id == null || id.isEmpty()) {
            return List.of();
        }
        try {
            return this.slotCache.get(id, () -> {
                if (ItemStackTagHelper.getTag(itemStack) == null) {
                    return List.of();
                }
                ListTag tag = ItemStackTagHelper.getTag(itemStack).getList("extraslots", 10);
                return NbtSlotData.LCODEC.parse(NbtOps.INSTANCE, tag).result().orElse(List.of());
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public GuiModuleOffsets getMinorGuiOffsets(ItemStack itemStack) {
        int[] pos = this.getSlotData(itemStack).stream().filter(NbtSlotData::isMinor).flatMapToInt(NbtSlotData::getPos).toArray();
        return new GuiModuleOffsets(pos);
    }

    @Override
    public GuiModuleOffsets getMajorGuiOffsets(ItemStack itemStack) {
        int[] pos = this.getSlotData(itemStack).stream().filter(NbtSlotData::isMajor).flatMapToInt(NbtSlotData::getPos).toArray();
        return new GuiModuleOffsets(pos);
    }

    @Override
    public String[] getMinorModuleKeys(ItemStack itemStack) {
        return this.getSlotData(itemStack).stream().filter(NbtSlotData::isMinor).map(NbtSlotData::slot).toArray(String[]::new);
    }

    @Override
    public String[] getMajorModuleKeys(ItemStack itemStack) {
        return this.getSlotData(itemStack).stream().filter(NbtSlotData::isMajor).map(NbtSlotData::slot).toArray(String[]::new);
    }

    @Override
    public String[] getRequiredModules(ItemStack itemStack) {
        return this.getSlotData(itemStack).stream().filter(NbtSlotData::required).map(NbtSlotData::slot).toArray(String[]::new);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ImmutableList<IModuleModel> getModels(ItemStack itemStack, @Nullable LivingEntity entity) {
        return ShowModelHelper.getModels(itemStack, entity, this::getSynergyData, this::getAllModules);
    }
}
