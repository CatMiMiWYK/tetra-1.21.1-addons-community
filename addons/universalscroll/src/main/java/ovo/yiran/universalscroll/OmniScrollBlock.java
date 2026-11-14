package ovo.yiran.universalscroll;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.ICraftingEffectProviderBlock;
import se.mickelus.tetra.blocks.ISchematicProviderBlock;
import se.mickelus.tetra.data.DataManager;
import se.mickelus.tetra.module.SchematicRegistry;
import se.mickelus.tetra.module.schematic.SchematicDefinition;
import se.mickelus.tetra.module.schematic.UpgradeSchematic;
import se.mickelus.tetra.module.schematic.requirement.AndRequirement;
import se.mickelus.tetra.module.schematic.requirement.CraftingRequirement;
import se.mickelus.tetra.module.schematic.requirement.LockedRequirement;
import se.mickelus.tetra.module.schematic.requirement.OrRequirement;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 百宝卷轴方块：汇总所有 Tetra 卷轴的功能。
 * <p>
 * 机制依据（tetra 7.0.0）：工作台通过 {@code AbstractWorkbenchBlock#getSchematics} 扫描周围
 * 5x5x5 范围内的 {@link ISchematicProviderBlock}，把其返回的键作为 {@code CraftingContext.unlocks}；
 * 图纸是否出现仍由 {@code SchematicRegistry.getSchematics(CraftingContext)} 按
 * 相关性/槽位/条件过滤，其中“卷轴解锁”条件是 {@link LockedRequirement}（判断 unlocks 是否包含其 key）。
 * <p>
 * 因此本方块返回“全部锁钥 + 全部图纸键”，等价于“玩家已获得所有卷轴”，不需要先逐个收集。
 * 其它条件（模块、材料、工具、经验等）保持原样校验，不做任何豁免。
 */
public class OmniScrollBlock extends Block implements ISchematicProviderBlock, ICraftingEffectProviderBlock {
    /** 与方块模型一致的碰撞/选取形状：卷轴躺在地面上。 */
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 5.0D, 14.0D, 3.0D, 11.0D);

    /** 解锁键缓存；数据包重载时清空。 */
    private static volatile ResourceLocation[] cachedUnlockKeys;
    private static boolean reloadHooked;

    public OmniScrollBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canUnlockSchematics(Level world, BlockPos pos, BlockPos targetPos) {
        return true;
    }

    /**
     * 返回全部卷轴解锁键。
     * <p>
     * 卷轴提供的键来自其 {@code schematics} 字段（例如 {@code tetra:warforge/axe}），这些键并不一定是
     * 图纸注册键，因此不能只返回图纸键。这里直接遍历已加载的图纸定义，收集所有
     * {@code tetra:locked} 条件引用的 key，再并入全部图纸键作为兜底。
     */
    @Override
    public ResourceLocation[] getSchematics(Level world, BlockPos pos, BlockState blockState) {
        return collectUnlockKeys();
    }

    @Override
    public boolean canUnlockCraftingEffects(Level world, BlockPos pos, BlockPos targetPos) {
        return true;
    }

    /** 返回全部合成效果键，等价于“所有卷轴的 craftingEffects 都已提供”。 */
    @Override
    public ResourceLocation[] getCraftingEffects(Level world, BlockPos pos, BlockState blockState) {
        if (DataManager.instance == null) {
            return new ResourceLocation[0];
        }
        return DataManager.instance.craftingEffectData.getData().keySet().toArray(new ResourceLocation[0]);
    }

    private static synchronized ResourceLocation[] collectUnlockKeys() {
        if (cachedUnlockKeys != null) {
            return cachedUnlockKeys;
        }

        Set<ResourceLocation> keys = new LinkedHashSet<>();
        if (DataManager.instance != null) {
            for (SchematicDefinition definition : DataManager.instance.schematicData.getData().values()) {
                collectLockedKeys(definition.requirement, keys);
            }
            if (!reloadHooked) {
                reloadHooked = true;
                // 数据包重载后重新收集，避免缓存过期
                DataManager.instance.schematicData.onReload(OmniScrollBlock::invalidateUnlockCache);
            }
        }

        // 兜底：全部图纸键也视为已解锁（原版卷轴有时直接以图纸键作为解锁标记）
        if (SchematicRegistry.instance != null) {
            for (UpgradeSchematic schematic : SchematicRegistry.getAllSchematics()) {
                keys.add(ResourceLocation.fromNamespaceAndPath(TetraMod.MOD_ID, schematic.getKey()));
            }
        }

        cachedUnlockKeys = keys.toArray(new ResourceLocation[0]);
        return cachedUnlockKeys;
    }

    private static void invalidateUnlockCache() {
        cachedUnlockKeys = null;
    }

    /**
     * 递归收集 {@code tetra:locked} 条件中的 key。
     * <p>
     * And/Or 组合条件的 children 字段是包私有，Tetra 没有公开读取接口，因此这里用反射遍历；
     * 反射失败时只跳过该分支，不影响其它解锁键。
     */
    private static void collectLockedKeys(CraftingRequirement requirement, Set<ResourceLocation> keys) {
        if (requirement == null) {
            return;
        }
        if (requirement instanceof LockedRequirement locked) {
            if (locked.key != null) {
                keys.add(locked.key);
            }
            return;
        }
        if (requirement instanceof AndRequirement || requirement instanceof OrRequirement) {
            try {
                Field field = requirement.getClass().getDeclaredField("requirements");
                field.setAccessible(true);
                Object value = field.get(requirement);
                if (value instanceof CraftingRequirement[] children) {
                    for (CraftingRequirement child : children) {
                        collectLockedKeys(child, keys);
                    }
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                // 兜底：结构变化时跳过，不影响其余解锁逻辑
            }
        }
    }
}
