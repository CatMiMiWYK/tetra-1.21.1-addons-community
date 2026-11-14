package net.tetra.modify.core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.tetra.modify.api.event.LivingDamageEvent;
import net.tetra.modify.api.event.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Player.class})
public class PlayerMixin {
    @Inject(method = {"actuallyHurt"}, at = {@At(value = "HEAD")})
    public void hurtWrapper(DamageSource source, float amount, CallbackInfo ci) {
        java.util.Stack<DamageContainer> containers = ((LivingEntityAccessor) (Object) this).getDamageContainers();
        if (containers.isEmpty()) {
            return;
        }
        DamageContainer container = containers.peek();
        if (container == null) {
            return;
        }

        Player entity = (Player) (Object) this;

        LivingHurtEvent.Pre preEvent = new LivingHurtEvent.Pre(entity, source, container.getNewDamage());
        NeoForge.EVENT_BUS.post(preEvent);
        if (preEvent.isCanceled()) {
            container.setNewDamage(0.0f);
            return;
        }
        container.setNewDamage(preEvent.getResultAmount());

        LivingHurtEvent.Post postEvent = new LivingHurtEvent.Post(entity, source, container.getNewDamage());
        NeoForge.EVENT_BUS.post(postEvent);
        if (postEvent.isCanceled()) {
            container.setNewDamage(0.0f);
            return;
        }
        container.setNewDamage(postEvent.getResultAmount());
    }

    @WrapOperation(method = {"actuallyHurt"}, at = {@At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;onLivingDamagePre(Lnet/minecraft/world/entity/LivingEntity;Lnet/neoforged/neoforge/common/damagesource/DamageContainer;)F")})
    public float damageWrapper(LivingEntity entity, DamageContainer container, Operation<Float> original) {
        LivingDamageEvent.Pre preEvent = new LivingDamageEvent.Pre(entity, container.getSource(), container.getNewDamage());
        NeoForge.EVENT_BUS.post(preEvent);
        if (preEvent.isCanceled()) {
            return 0.0f;
        }
        container.setNewDamage(preEvent.getResultAmount());

        float result = original.call(entity, container);
        if (result == 0.0f) {
            return 0.0f;
        }

        LivingDamageEvent.Post postEvent = new LivingDamageEvent.Post(entity, container.getSource(), result);
        NeoForge.EVENT_BUS.post(postEvent);
        if (postEvent.isCanceled()) {
            return 0.0f;
        }
        container.setNewDamage(postEvent.getResultAmount());
        return postEvent.getResultAmount();
    }
}
