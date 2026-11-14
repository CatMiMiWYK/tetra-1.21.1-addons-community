package net.acetheeldritchking.art_of_forging;

import net.acetheeldritchking.art_of_forging.item.custom.AncientItem;
import net.acetheeldritchking.art_of_forging.item.custom.EnigmaticConstructItem;
import net.acetheeldritchking.art_of_forging.item.custom.LifeFiberItem;
import net.acetheeldritchking.art_of_forging.item.custom.SigilOfEdenItem;
import net.acetheeldritchking.art_of_forging.item.modular.ModularArtifact;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AoFRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("art_of_forging");
    public static final DeferredItem<Item> MODULAR_ARTIFACT = ITEMS.register("modular_artifact", ModularArtifact::new);
    public static final DeferredItem<Item> CURIOUS_ARTIFACT = ITEMS.register("curious_artifact", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RESONANT_ALLOY = ITEMS.register("resonant_alloy", () -> new Item(new Item.Properties().fireResistant()));
    public static final DeferredItem<Item> FORGED_STEEL_INGOT = ITEMS.register("forged_steel_ingot", () -> new Item(new Item.Properties().fireResistant()));
    public static DeferredItem<Item> VOBRITE_CRYSTAL = ITEMS.register("vobrite_crystal", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static DeferredItem<Item> VOBRIVIUM_INGOT = ITEMS.register("vobrivium_ingot", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static DeferredItem<Item> ENDSTEEL_INGOT = ITEMS.register("endsteel_ingot", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static DeferredItem<Item> LIFE_FIBER = ITEMS.register("life_fiber", () -> new LifeFiberItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> FANG_CHARM = ITEMS.register("fang_charm", () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static DeferredItem<Item> SIGIL_OF_EDEN = ITEMS.register("sigil_of_eden", () -> new SigilOfEdenItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant()));
    public static DeferredItem<Item> ENIGMATIC_CONSTRUCT = ITEMS.register("enigmatic_construct", () -> new EnigmaticConstructItem(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static DeferredItem<Item> ANCIENT_AXE = ITEMS.register("ancient_axe", () -> new AncientItem(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static DeferredItem<Item> ANCIENT_BLADE = ITEMS.register("ancient_blade", () -> new AncientItem(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static DeferredItem<Item> ANCIENT_FLAIL = ITEMS.register("ancient_flail", () -> new AncientItem(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static DeferredItem<Item> DEMONIC_AXE = ITEMS.register("demonic_axe", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static DeferredItem<Item> DEMONIC_BLADE = ITEMS.register("demonic_blade", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static DeferredItem<Item> DEMONIC_FLAIL = ITEMS.register("demonic_flail", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1)));
    public static DeferredItem<Item> DEVILS_SOUL_GEM = ITEMS.register("devils_soul_gem", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant()));
    public static DeferredItem<Item> RENDING_SCISSOR_RED = ITEMS.register("rending_scissor_red", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant()));
    public static DeferredItem<Item> RENDING_SCISSOR_PURPLE = ITEMS.register("rending_scissor_purple", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant()));
    public static DeferredItem<Item> RENDING_SCISSOR_COMPLETE = ITEMS.register("rending_scissor_complete", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant()));
    public static DeferredItem<Item> NANO_INSECTOID = ITEMS.register("nano_insectoid", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));
    public static DeferredItem<Item> ENCODED_CANISTER = ITEMS.register("encoded_canister", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));
    public static DeferredItem<Item> ESOTERIC_CODEX = ITEMS.register("esoteric_codex", () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static DeferredItem<Item> MARK_OF_THE_ARCHITECT = ITEMS.register("mark_of_the_architect", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1)));
    public static DeferredItem<Item> SHOCKWAVE_CHAMBER = ITEMS.register("shockwave_chamber", () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16)));
    public static DeferredItem<Item> DRAGON_SOUL = ITEMS.register("dragon_soul", () -> new Item(new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static DeferredItem<Item> SHARDS_OF_MALICE = ITEMS.register("shards_of_malice", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant()));
    public static DeferredItem<Item> POTENT_MIXTURE = ITEMS.register("potent_mixture", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static DeferredItem<Item> HEART_OF_ENDER = ITEMS.register("heart_of_ender", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static DeferredItem<Item> EERIE_SHARD = ITEMS.register("eerie_shard", () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16)));
    public static DeferredItem<Item> SOUL_EMBER = ITEMS.register("soul_ember", () -> new Item(new Item.Properties()));
    public static DeferredItem<Item> FRAGMENT_OF_EDEN = ITEMS.register("fragment_of_eden", () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16)));
}
