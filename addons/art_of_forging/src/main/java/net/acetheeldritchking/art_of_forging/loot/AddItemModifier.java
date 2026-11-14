package net.acetheeldritchking.art_of_forging.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class AddItemModifier
        extends LootModifier {
    public static final MapCodec<AddItemModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> AddItemModifier.codecStart(inst).and(ResourceLocation.CODEC.fieldOf("loot_table").forGetter(m -> m.loot_table)).apply(inst, AddItemModifier::new));
    private final ResourceLocation loot_table;

    public AddItemModifier(LootItemCondition[] conditionsIn, ResourceLocation item) {
        super(conditionsIn);
        this.loot_table = item;
    }

    @NotNull
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        LootTable addedLootTable = context.getLevel().getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, this.loot_table));
        addedLootTable.getRandomItems(context, generatedLoot::add);
        return generatedLoot;
    }

    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
