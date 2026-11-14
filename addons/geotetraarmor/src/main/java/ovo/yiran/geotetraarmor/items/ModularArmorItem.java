package ovo.yiran.geotetraarmor.items;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import ovo.yiran.geotetraarmor.GeoTetraArmor;
import ovo.yiran.geotetraarmor.model.GeckoModelData;
import ovo.yiran.geotetraarmor.model.ModularGeoArmorRenderer;
import se.mickelus.tetra.items.InitializableItem;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.items.modular.ItemModularHandheld;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.model.IModuleModel;
import se.mickelus.tetra.module.schematic.RepairSchematic;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class ModularArmorItem extends ItemModularHandheld implements GeoItem, Equipable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public EquipmentSlot slot;
    public Cache<String, List<ModularGeoArmorRenderer>> rendererCache;

    public ModularArmorItem(EquipmentSlot slot, String repairName) {
        super(new Item.Properties().durability(1));
        GeoTetraArmor.ITEM.add(this);
        this.slot = slot;
        this.rendererCache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterWrite(5L, TimeUnit.MINUTES).build();
        SchematicRegistry.instance.registerSchematic(new RepairSchematic(this, repairName));
    }

    @Override
    public void clearCaches() {
        this.rendererCache.invalidateAll();
        super.clearCaches();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack itemStack) {
        if (this.isBroken(itemStack)) {
            return ItemAttributeModifiers.EMPTY;
        }

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        EquipmentSlotGroup group = switch (this.slot) {
            case HEAD -> EquipmentSlotGroup.HEAD;
            case CHEST -> EquipmentSlotGroup.CHEST;
            case LEGS -> EquipmentSlotGroup.LEGS;
            case FEET -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ARMOR;
        };
        this.getAttributeModifiersCached(itemStack).forEach((attribute, modifier) ->
                builder.add(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute), modifier, group));
        return builder.build();
    }

    /**
     * 与 1.20.1 实现等价：给每条修饰符的 id 追加槽位名，避免头/胸/腿/脚共享同一模块时 id 冲突导致只生效一条。
     */
    @Override
    public Multimap<Attribute, AttributeModifier> fixIdentifiers(Multimap<Attribute, AttributeModifier> modifiers) {
        if (modifiers == null) {
            return null;
        }

        return modifiers.entries().stream().collect(Multimaps.toMultimap(Map.Entry::getKey, entry -> {
            AttributeModifier modifier = entry.getValue();
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modifier.id().getNamespace(),
                    modifier.id().getPath() + "/" + this.slot.getName());
            return new AttributeModifier(id, modifier.amount(), modifier.operation());
        }, ArrayListMultimap::create));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly/Idle", 0, state -> {
            for (DataTicket<?> dataTicket : state.getExtraData().keySet()) {
                if ("entity".equals(dataTicket.id())) {
                    Object data = state.getData((DataTicket) dataTicket);
                    if (data instanceof Player player && player.getAbilities().flying) {
                        return state.setAndContinue(DefaultAnimations.FLY);
                    }
                }
            }
            return state.setAndContinue(DefaultAnimations.IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public ImmutableList<IModuleModel> getModels(ItemStack itemStack, @Nullable LivingEntity entity) {
        return this.getAllModules(itemStack).stream()
                .sorted(Comparator.comparing(ItemModule::getRenderLayer))
                .flatMap(itemModule -> Arrays.stream(itemModule.getModels(itemStack)))
                .filter(Objects::nonNull)
                .filter(ModularArmorItem::modelNotGecko)
                .sorted(Comparator.comparing(IModuleModel::getRenderLayer))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    public static boolean modelIsGecko(IModuleModel moduleModel) {
        return moduleModel instanceof GeckoModelData;
    }

    public static boolean modelNotGecko(IModuleModel moduleModel) {
        return !(moduleModel instanceof GeckoModelData);
    }

    public ImmutableList<GeckoModelData> getGecModels(ItemStack itemStack, @Nullable LivingEntity entity) {
        return this.getAllModules(itemStack).stream()
                .sorted(Comparator.comparing(ItemModule::getRenderLayer))
                .flatMap(itemModule -> Arrays.stream(itemModule.getModels(itemStack)))
                .filter(Objects::nonNull)
                .filter(ModularArmorItem::modelIsGecko)
                .map(model -> (GeckoModelData) model)
                .sorted(Comparator.comparing(IModuleModel::getRenderLayer))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    public List<ModularGeoArmorRenderer> geoArmorRenderers(ItemStack itemStack, @Nullable LivingEntity entity) {
        try {
            return this.rendererCache.get(this.getModelCacheKey(itemStack, entity),
                    () -> this.getGecModels(itemStack, entity).stream().map(ModularGeoArmorRenderer::new).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return ImmutableList.of();
        }
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return this.slot;
    }
}
