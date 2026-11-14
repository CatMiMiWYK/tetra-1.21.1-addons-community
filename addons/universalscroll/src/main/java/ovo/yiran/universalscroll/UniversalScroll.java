package ovo.yiran.universalscroll;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ovo.yiran.universalscroll.net.SelectSchematicPayload;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;

/**
 * 白白的百宝袋（万能的卷轴）附属。
 * <p>
 * 机制依据（tetra 7.0.0）：卷轴物品本身不会应用 schematic，只有被放置到工作台旁时，
 * 才会通过 {@code ScrollBlock -> ScrollTile -> AbstractWorkbenchBlock#getSchematics}
 * 进入工作台的图纸列表；真正的应用发生在 {@code WorkbenchTile#craft}。
 * 因此本附属在选择图纸后，直接把玩家带到工作台的图纸详情页，不伪造“凭空合成”。
 */
@Mod(UniversalScroll.MODID)
public class UniversalScroll {
    public static final String MODID = "universalscroll";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final DeferredItem<UniversalScrollItem> UNIVERSAL_SCROLL =
            ITEMS.register("universal_scroll", UniversalScrollItem::new);

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);

    /** 百宝卷轴方块：汇总全部图纸与合成效果，放在工作台附近即可直接使用。 */
    public static final DeferredHolder<Block, Block> OMNI_SCROLL_BLOCK = BLOCKS.register("omni_scroll",
            () -> new OmniScrollBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.GOLD)
                    .strength(0.3F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    /** 百宝卷轴物品；强制附魔光效以呈现鎏金质感。 */
    public static final DeferredItem<OmniScrollItem> OMNI_SCROLL = ITEMS.register("omni_scroll",
            () -> new OmniScrollItem(OMNI_SCROLL_BLOCK.get(), new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    /** 独立创造模式标签：白白的百宝袋。 */
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.universalscroll"))
                    .icon(() -> UNIVERSAL_SCROLL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(UNIVERSAL_SCROLL.get());
                        output.accept(OMNI_SCROLL.get());
                    })
                    .build());

    /**
     * 最近一次选中的图纸。1.21.1 下使用 DataComponent，不写物品旧 NBT。
     */
    public static final Supplier<DataComponentType<ResourceLocation>> SELECTED_SCHEMATIC =
            DATA_COMPONENTS.registerComponentType("selected_schematic", builder -> builder
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC));

    public UniversalScroll(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        TABS.register(modEventBus);
        modEventBus.addListener(this::onRegisterPayloads);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SelectSchematicPayload.TYPE, SelectSchematicPayload.STREAM_CODEC, SelectSchematicPayload::handle);
    }
}
