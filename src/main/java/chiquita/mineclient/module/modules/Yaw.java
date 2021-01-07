package chiquita.mineclient.module.modules;

import chiquita.mineclient.event.EventTick;
import chiquita.mineclient.module.Module;
import chiquita.mineclient.settings.Value;
import chiquita.mineclient.utils.EntityUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class Yaw extends Module {

    private boolean lDown = false;
    private boolean rDown = false;

    public final Value<Boolean> autoAlign = new Value<>("AutoAlign", new String[]{"Yaw"}, "Automatically determines highway and snaps yaw", true);
    public final Value<Boolean> useYaw = new Value<>("UseYaw", new String[]{"UseYaw"}, "Snaps yaw based on stuff and stuff", false);
    public final Value<Integer> yaw = new Value<>("Yaw", new String[]{"Yaw"}, "What angle to snap to, 1 = 45, 2 = 30, 3 = 15, 4 = 90", 1, 4, 1, 1, 0);

    public List<Value> values = Arrays.asList(
            autoAlign,
            useYaw,
            yaw
    );

    public Yaw() {
        super("Yaw", Category.player, true, GLFW.GLFW_KEY_Y);
        super.settings = values;
    }

    @EventHandler
    private Listener<EventTick> eventTickListener = new Listener<>(event -> {
        /* yes looks like a good way to do it to me */
        if (useYaw.getValue() && mc.currentScreen == null) {
            int ymode = yaw.getValue() - 1;

            if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT) && !lDown) {
                mc.player.yaw -= ymode == 0 ? 45 : ymode == 1 ? 30 : ymode == 2 ? 15 : 90;
                lDown = true;
            } else if (!InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT)) {
                lDown = false;
            }

            if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT) && !rDown) {
                mc.player.yaw += ymode == 0 ? 45 : ymode == 1 ? 30 : ymode == 2 ? 15 : 90;
                rDown = true;
            } else if (!InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT)) {
                rDown = false;
            }
            snap();
        }
        else if (autoAlign.getValue()) {
            switch (EntityUtils.determineHighway()) {
                case XP: mc.player.yaw = -90; break;
                case XPZP: mc.player.yaw = -45; break;
                case XPZN: mc.player.yaw = -135; break;
                case XN: mc.player.yaw = 90; break;
                case XNZP: mc.player.yaw = 45; break;
                case XNZN: mc.player.yaw = 135; break;
                case ZP: mc.player.yaw = 0; break;
                case ZN: mc.player.yaw = 180; break;
            }
        }

    });

    public void snap() {
        // quic maff
        if (useYaw.getValue()) {
            int mode = yaw.getValue() - 1;
            int interval = mode == 0 ? 45 : mode == 1 ? 30 : mode == 2 ? 15 : 90;
            int rot = (int) mc.player.yaw + (Math.floorMod((int) mc.player.yaw, interval) < interval / 2 ?
                    -Math.floorMod((int) mc.player.yaw, interval) : interval - Math.floorMod((int) mc.player.yaw, interval));

            mc.player.yaw = rot;
        }
    }
}