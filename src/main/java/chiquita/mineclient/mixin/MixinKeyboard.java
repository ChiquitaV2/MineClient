package chiquita.mineclient.mixin;

import chiquita.mineclient.Mineclient;
import chiquita.mineclient.event.KeyPressEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard
{
    @Inject(at = @At("HEAD"), method = "onKey(JIIII)V")
    private void onOnKey(long windowHandle, int keyCode, int scanCode,
                         int action, int modifiers, CallbackInfo ci)
    {
        KeyPressEvent event = new KeyPressEvent(keyCode, action);
        Mineclient.eventBus.post(event);
    }
}
