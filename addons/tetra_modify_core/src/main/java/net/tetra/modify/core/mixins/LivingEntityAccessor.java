package net.tetra.modify.core.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Stack;

/**
 * 1.21.1 的 damageContainers 声明在 LivingEntity，不在 Player。
 * PlayerMixin 目标为 Player，无法 @Shadow 继承字段，故用接口访问器读取。
 */
@Mixin(value = LivingEntity.class, remap = false)
public interface LivingEntityAccessor {
    @Accessor("damageContainers")
    Stack<DamageContainer> getDamageContainers();
}
