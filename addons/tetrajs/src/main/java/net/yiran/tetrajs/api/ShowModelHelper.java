package net.yiran.tetrajs.api;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.data.SynergyData;
import se.mickelus.tetra.module.model.FilteredGridTextureModelData;
import se.mickelus.tetra.module.model.IModuleModel;
import se.mickelus.tetra.util.ItemStackTagHelper;

public class ShowModelHelper {
    public static ShowModelHelper INSTANCE = new ShowModelHelper();
    public static Codec<List<String>> CODEC = Codec.STRING.listOf();

    public void addShowModel(ItemStack stack, String... type) {
        Set<String> list = this.getShowModels(stack);
        list.addAll(List.of(type));
        this.saveShowModels(stack, list);
    }

    public void removeShowModel(ItemStack stack, String... type) {
        Set<String> list = this.getShowModels(stack);
        List.of(type).forEach(list::remove);
        this.saveShowModels(stack, list);
    }

    public boolean hasShowModel(ItemStack stack, String type) {
        Set<String> list = this.getShowModels(stack);
        return list.contains(type);
    }

    public void clearShowModel(ItemStack stack) {
        this.saveShowModels(stack, new HashSet<>());
    }

    public Set<String> getShowModels(ItemStack stack) {
        CompoundTag tag = ItemStackTagHelper.getTag(stack);
        if (tag == null) {
            return new HashSet<>();
        }
        ListTag list = tag.getList("showModels", 8);
        return new HashSet<>(CODEC.parse(NbtOps.INSTANCE, list).result().orElse(List.of()));
    }

    public void saveShowModels(ItemStack stack, Set<String> slots) {
        Tag encoded = CODEC.encodeStart(NbtOps.INSTANCE, slots.stream().toList()).result().orElseGet(ListTag::new);
        ItemStackTagHelper.mutate(stack, tag -> tag.put("showModels", encoded));
        IModularItem.updateIdentifier(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public static ImmutableList<IModuleModel> getModels(ItemStack itemStack, @Nullable LivingEntity entity, Function<ItemStack, SynergyData[]> getSynergyData, Function<ItemStack, Collection<ItemModule>> getAllModules, String... defaultShowModels) {
        ArrayList<String> showModelTypes = new ArrayList<>();
        CompoundTag tag = ItemStackTagHelper.getTag(itemStack);
        if (tag != null) {
            ListTag list = tag.getList("showModels", 8);
            showModelTypes.addAll(CODEC.parse(NbtOps.INSTANCE, list).result().orElse(List.of()));
        }
        showModelTypes.addAll(List.of(defaultShowModels));
        return Stream.concat(
                        Arrays.stream(getSynergyData.apply(itemStack)).flatMap(synergyData -> Arrays.stream(synergyData.models)),
                        getAllModules.apply(itemStack).stream().sorted(Comparator.comparing(ItemModule::getRenderLayer)).flatMap(itemModule -> Arrays.stream(itemModule.getModels(itemStack))))
                .filter(Objects::nonNull)
                .filter(moduleModel -> {
                    if (moduleModel instanceof FilteredGridTextureModelData filtered) {
                        return showModelTypes.isEmpty() || showModelTypes.contains(filtered.getFilter());
                    }
                    return true;
                })
                .sorted(Comparator.comparing(IModuleModel::getRenderLayer))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }
}
