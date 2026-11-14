package net.acetheeldritchking.art_of_forging;

import com.mojang.logging.LogUtils;
import net.acetheeldritchking.art_of_forging.effects.BeheadingEffect;
import net.acetheeldritchking.art_of_forging.effects.CarnageEffect;
import net.acetheeldritchking.art_of_forging.effects.CavalryEffect;
import net.acetheeldritchking.art_of_forging.effects.ConqueringEffect;
import net.acetheeldritchking.art_of_forging.effects.DevouringEffect;
import net.acetheeldritchking.art_of_forging.effects.DismountingEffect;
import net.acetheeldritchking.art_of_forging.effects.DisorientingEffect;
import net.acetheeldritchking.art_of_forging.effects.DragonMistEffect;
import net.acetheeldritchking.art_of_forging.effects.EsotericEdgeEffect;
import net.acetheeldritchking.art_of_forging.effects.EvokingMawEffect;
import net.acetheeldritchking.art_of_forging.effects.FeastingEffect;
import net.acetheeldritchking.art_of_forging.effects.GoliathSlayerEffect;
import net.acetheeldritchking.art_of_forging.effects.HubrisEffect;
import net.acetheeldritchking.art_of_forging.effects.InfernalRebukeEffect;
import net.acetheeldritchking.art_of_forging.effects.JudgementEffect;
import net.acetheeldritchking.art_of_forging.effects.KnockbackEffect;
import net.acetheeldritchking.art_of_forging.effects.LifeFiberLossEffect;
import net.acetheeldritchking.art_of_forging.effects.LifeStealEffect;
import net.acetheeldritchking.art_of_forging.effects.NanoFusedEffect;
import net.acetheeldritchking.art_of_forging.effects.ResolveEffect;
import net.acetheeldritchking.art_of_forging.effects.RetributionEffect;
import net.acetheeldritchking.art_of_forging.effects.SlaughteringEffect;
import net.acetheeldritchking.art_of_forging.effects.SonicShockEffect;
import net.acetheeldritchking.art_of_forging.effects.SoulChargedEffect;
import net.acetheeldritchking.art_of_forging.effects.StormCallerEffect;
import net.acetheeldritchking.art_of_forging.effects.SubjugationEffect;
import net.acetheeldritchking.art_of_forging.effects.VengeanceEffect;
import net.acetheeldritchking.art_of_forging.effects.WitheringEffect;
import net.acetheeldritchking.art_of_forging.effects.curio.CurioFireResistanceEffect;
import net.acetheeldritchking.art_of_forging.effects.curio.CurioGlowingEffect;
import net.acetheeldritchking.art_of_forging.effects.curio.CurioHasteEffect;
import net.acetheeldritchking.art_of_forging.effects.curio.CurioKarmaEffect;
import net.acetheeldritchking.art_of_forging.effects.curio.CurioMagicResistanceEffect;
import net.acetheeldritchking.art_of_forging.effects.curio.CurioRegenerationEffect;
import net.acetheeldritchking.art_of_forging.effects.curio.CurioStrengthEffect;
import net.acetheeldritchking.art_of_forging.effects.potion.PotionEffects;
import net.acetheeldritchking.art_of_forging.events.AoFEvents;
import net.acetheeldritchking.art_of_forging.item.AoFCreativeModeTab;
import net.acetheeldritchking.art_of_forging.item.ScrollHelper;
import net.acetheeldritchking.art_of_forging.loot.ModLootModifiers;
import net.acetheeldritchking.art_of_forging.networking.AoFPackets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Mod(ArtOfForging.MOD_ID)
public class ArtOfForging {
    public static final String MOD_ID = "art_of_forging";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ArtOfForging(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::enqueueIMC);
        AoFRegistry.ITEMS.register(modBus);
        AoFCreativeModeTab.register(modBus);
        ModLootModifiers.register(modBus);
        PotionEffects.register(modBus);
        AoFEvents.register(modBus);
        NeoForge.EVENT_BUS.register(new WitheringEffect());
        NeoForge.EVENT_BUS.register(new StormCallerEffect());
        NeoForge.EVENT_BUS.register(new EvokingMawEffect());
        NeoForge.EVENT_BUS.register(new LifeStealEffect());
        NeoForge.EVENT_BUS.register(new KnockbackEffect());
        NeoForge.EVENT_BUS.register(new LifeFiberLossEffect());
        NeoForge.EVENT_BUS.register(new CavalryEffect());
        NeoForge.EVENT_BUS.register(new DismountingEffect());
        NeoForge.EVENT_BUS.register(new DragonMistEffect());
        NeoForge.EVENT_BUS.register(new DisorientingEffect());
        NeoForge.EVENT_BUS.register(new VengeanceEffect());
        NeoForge.EVENT_BUS.register(new ResolveEffect());
        NeoForge.EVENT_BUS.register(new DevouringEffect());
        NeoForge.EVENT_BUS.register(new InfernalRebukeEffect());
        NeoForge.EVENT_BUS.register(new HubrisEffect());
        NeoForge.EVENT_BUS.register(new SlaughteringEffect());
        NeoForge.EVENT_BUS.register(new CarnageEffect());
        NeoForge.EVENT_BUS.register(new JudgementEffect());
        NeoForge.EVENT_BUS.register(new RetributionEffect());
        NeoForge.EVENT_BUS.register(new EsotericEdgeEffect());
        NeoForge.EVENT_BUS.register(new FeastingEffect());
        NeoForge.EVENT_BUS.register(new NanoFusedEffect());
        NeoForge.EVENT_BUS.register(new BeheadingEffect());
        NeoForge.EVENT_BUS.register(new SoulChargedEffect());
        // SonicShockEffect 是 ChargedAbilityEffect（靠 item ability 注册），不是事件监听器，
        // 没有任何 @SubscribeEvent 方法，注册到 EVENT_BUS 会抛 IllegalArgumentException。
        NeoForge.EVENT_BUS.register(new ConqueringEffect());
        NeoForge.EVENT_BUS.register(new SubjugationEffect());
        NeoForge.EVENT_BUS.register(new GoliathSlayerEffect());
        NeoForge.EVENT_BUS.register(new CurioFireResistanceEffect());
        NeoForge.EVENT_BUS.register(new CurioMagicResistanceEffect());
        NeoForge.EVENT_BUS.register(new CurioStrengthEffect());
        NeoForge.EVENT_BUS.register(new CurioRegenerationEffect());
        NeoForge.EVENT_BUS.register(new CurioKarmaEffect());
        NeoForge.EVENT_BUS.register(new CurioHasteEffect());
        NeoForge.EVENT_BUS.register(new CurioGlowingEffect());
        modBus.addListener(this::buildCreativeTab);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AoFPackets.register();
            CuriosApi.registerCurio(AoFRegistry.MODULAR_ARTIFACT.get(), net.acetheeldritchking.art_of_forging.item.modular.ModularArtifact.instance);
        });
    }

    public void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", "register_type", () -> SlotTypePreset.CHARM.getMessageBuilder().size(1).build());
    }

    public void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("tetra", "default"))) {
            LOGGER.info("Registering AoF schematics with Tetra creative tab");
            event.accept(this.setupSchematic("tetra/flamberge_blade", MOD_ID, new String[]{"sword/flamberge_blade"}, false, 2, 16750098, 6, 15, 4, 7), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("single/head/halberd_head/halberd_head", MOD_ID, new String[]{"single/head/halberd_head/halberd_head"}, false, 1, 0x444AFF, 8, 1, 4, 5), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("sword/key_guard", MOD_ID, new String[]{"sword/key_guard"}, false, 1, 16442377, 0, 1, 9, 4), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("single/head/mace_head/mace_head", MOD_ID, new String[]{"single/head/mace_head/mace_head"}, false, 1, 0x560060, 9, 3, 6, 2), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("tetra/crucible_blade", MOD_ID, new String[]{"sword/crucible_blade"}, false, 2, 16719360, 8, 7, 9, 2), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("bow/stave/dreadnought_stave", MOD_ID, new String[]{"bow/stave/dreadnought_stave", "bow/stave/dreadnought_cross_stave"}, false, 1, 15971103, 8, 1, 9, 5), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("sword/katana/katana_blade", MOD_ID, new String[]{"sword/katana/katana_blade", "sword/tsuba_guard"}, false, 2, 14417680, 5, 10, 13, 2), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("bow/string/compound_string", MOD_ID, new String[]{"bow/string/compound_string", "crossbow/string/compound_cross_string"}, false, 1, 1697160, 15, 13, 12, 14), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("utilize/hammer", MOD_ID, new String[]{"utilize/hammer"}, false, 2, 16422889, 1, 15, 12, 8), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("sword/katana/murasama_blade", "otherworldly", new String[]{"sword/katana/murasama_blade"}, false, 2, 12919587, 6, 7, 13, 15), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("tetra/rending_scissor_complete", "otherworldly", new String[]{"sword/scissor_blade_left", "sword/scissor_blade_right", "sword/rending_scissor_complete"}, false, 2, 14885250, 1, 15, 2, 13), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("sword/crucible/architects_crucible_blade", "true_crucible", new String[]{"sword/crucible_blade", "sword/crucible/architects_crucible_blade"}, true, 2, 16711746, 8, 7, 9, 2), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("sword/tonal_blade", MOD_ID, new String[]{"sword/tonal_blade"}, false, 1, 14350246, 3, 5, 6, 9), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.accept(this.setupSchematic("sword/thousand_cold_nights", "otherworldly", new String[]{"sword/katana/murasama_blade", "sword/thousand_cold_nights"}, true, 2, 6061184, 7, 8, 14, 13), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    private ItemStack setupSchematic(String key, String details, String[] schematics, boolean isIntricate, int material, int tint, Integer... glyphs) {
        ScrollHelper data = new ScrollHelper(key, Optional.ofNullable(details), isIntricate, material, tint, Arrays.asList(glyphs), Arrays.stream(schematics).map(s -> ResourceLocation.fromNamespaceAndPath("tetra", s)).collect(Collectors.toList()), Collections.emptyList());
        Item tetraScroll = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("tetra", "scroll_rolled"));
        ItemStack itemStack = new ItemStack(tetraScroll);
        data.write(itemStack);
        return itemStack;
    }

    @EventBusSubscriber(modid = "art_of_forging", bus = EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
