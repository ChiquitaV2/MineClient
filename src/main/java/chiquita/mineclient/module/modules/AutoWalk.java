package chiquita.mineclient.module.modules;

import chiquita.mineclient.event.EventTick;
import chiquita.mineclient.module.Module;
import chiquita.mineclient.module.ModuleManager;
import chiquita.mineclient.settings.Value;
import chiquita.mineclient.utils.EntityUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class AutoWalk extends Module {

    public final Value<Boolean> autoAlign = new Value<>("AutoAlign", new String[]{"AutoAlign"}, "See what highway you're on and make a decision.", false);
    public final Value<Boolean> pauseNoPickaxe = new Value<>("PauseNoPick", new String[]{"PauseNoPick"}, "See if you have no pickaxe in your hand and pause", false);
    public final Value<Integer> direction = new Value<>("Direction", new String[]{"Direction"}, "Which key to hold down.", 1, 0, 4, 1,0);

    public List<Value> values = Arrays.asList(
            autoAlign,
            direction,
            pauseNoPickaxe
    );

    public AutoWalk() {
        super("AutoWalk", Module.Category.movement, true, GLFW.GLFW_KEY_Y);
        super.settings = values;
    }

    @EventHandler
    private Listener<EventTick> eventTickListener = new Listener<>(event -> {
        AutoEat autoEat = (AutoEat) ModuleManager.getModule(AutoEat.class);
        AutoTunnel at = ((AutoTunnel) ModuleManager.getModule(AutoTunnel.class));
        boolean pause = at.PauseAutoWalk() || autoEat.isEating() || (pauseNoPickaxe.getValue() && (mc.player.inventory.getMainHandStack().getItem() != Items.DIAMOND_PICKAXE && mc.player.inventory.getMainHandStack().getItem() != Items.NETHERITE_PICKAXE));
        if (!pause) {
            if (autoAlign.getValue()) {
                ModuleManager.getModule(Yaw.class).getSettings().get(0).setValue(false);
                ModuleManager.getModule(Yaw.class).getSettings().get(1).setValue(false);
                switch (EntityUtils.determineHighway()) {
                    case XPZP:
                        mc.player.yaw = 0;
                        mc.player.headYaw = -45;
                        mc.options.keyLeft.setPressed(true);
                        mc.options.keyForward.setPressed(true);
                        break;
                    case XNZP:
                        mc.player.yaw = 90;
                        mc.player.headYaw = -135;
                        mc.options.keyForward.setPressed(true);
                        mc.options.keyLeft.setPressed(true);
                        break;
                    case XPZN:
                        mc.player.yaw = 180;
                        mc.player.yaw = 135;
                        break;
                }
            }
            else {
                switch (direction.getValue()) {
                    case 1:
                        mc.options.keyForward.setPressed(true);
                        break;
                    case 2:
                        mc.options.keyRight.setPressed(true);
                        break;
                    case 3:
                        mc.options.keyBack.setPressed(true);
                        break;
                    case 4:
                        mc.options.keyLeft.setPressed(true);
                        break;
                }
            }
        }
        if (pause) {
            mc.options.keyForward.setPressed(false);
            mc.options.keyLeft.setPressed(false);
            mc.options.keyRight.setPressed(false);
            mc.options.keyBack.setPressed(false);
        }
    });

    public void onDisable() {
        mc.options.keyForward.setPressed(false);
        mc.options.keyLeft.setPressed(false);
        mc.options.keyRight.setPressed(false);
        mc.options.keyBack.setPressed(false);
        super.onDisable();
    }
}
