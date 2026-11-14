package net.yiran.tetrajs.kubejs.events;

import dev.latvian.mods.kubejs.event.KubeEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.yiran.tetrajs.kubejs.builders.statbar.AttributeStatBarBuilder;
import net.yiran.tetrajs.kubejs.builders.statbar.EffectStatBarBuilder;
import net.yiran.tetrajs.kubejs.builders.statbar.IStatBarBuilder;
import se.mickelus.tetra.effect.ItemEffect;

public class StatBarRegisterEventJS implements KubeEvent {
    public List<IStatBarBuilder<?>> statBars = new ArrayList<>();

    public EffectStatBarBuilder registerEffectBar(ItemEffect effect) {
        EffectStatBarBuilder builder = new EffectStatBarBuilder(effect);
        this.statBars.add(builder);
        return builder;
    }

    public AttributeStatBarBuilder registerAttributeBar(Attribute attribute) {
        AttributeStatBarBuilder builder = new AttributeStatBarBuilder(attribute);
        this.statBars.add(builder);
        return builder;
    }

    public List<IStatBarBuilder<?>> getStatBars() {
        return this.statBars;
    }
}
