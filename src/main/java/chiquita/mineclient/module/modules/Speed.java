/*
package chiquita.mineclient.module.modules;

import chiquita.mineclient.event.EventTick;
import chiquita.mineclient.module.Module;
import chiquita.mineclient.settings.Value;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class Speed extends Module {

    public Speed() {
        super("Speed", Category.movement, true, GLFW.GLFW_KEY_Y);
        super.settings = values;
    }

    public final Value<Float> Speed = new Value<Float>("Speed", new String[]{"Speed"}, "Speed to speed at", 1.16f, 0.3f, 3f, 0.01f, 2);
    public final Value<Boolean> Sneak = new Value<>("Sneak", new String[]{"Sneak"}, "Do we activate on sneak?", false);

    public List<Value> values = Arrays.asList(
            Speed,
            Sneak
    );

    @EventHandler
    private Listener<EventTick> onTick = new Listener<>(p_Event -> {
        if (mc.player.isFallFlying()) return;
        if (!Sneak.getValue() && mc.player.isSneaking()) return;
        double speedStrafe = Speed.getValue() / 3;
        double forward = mc.player.forwardSpeed;
        double strafe = mc.player.sidewaysSpeed;
        float yaw = mc.player.yaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        }
        else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) yaw += (forward > 0.0D ? 45 : -45);
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1.0D;
                } else if (forward < 0.0D) forward = -1.0D;
            }
            mc.player.setVelocity((forward * speedStrafe * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speedStrafe * Math.sin(Math.toRadians(yaw + 90.0F))), mc.player.getVelocity().y,
                    forward * speedStrafe * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speedStrafe * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    });
}

 */