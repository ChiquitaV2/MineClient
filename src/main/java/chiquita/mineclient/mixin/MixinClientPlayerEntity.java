package chiquita.mineclient.mixin;

import chiquita.mineclient.Mineclient;
import chiquita.mineclient.event.EventMove;
import chiquita.mineclient.event.EventTick;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    public void move(MovementType movementType_1, Vec3d vec3d_1, CallbackInfo info) {
        EventMove event = new EventMove(movementType_1, vec3d_1);
        Mineclient.eventBus.post(event);
    }

    @Inject(at = @At("RETURN"), method = "tick()V", cancellable = true)
    public void tick(CallbackInfo info) {
        EventTick event = new EventTick();
        Mineclient.eventBus.post(event);
        if (event.isCancelled())
            info.cancel();
    }
}