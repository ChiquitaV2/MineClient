package chiquita.mineclient.module.modules;

import chiquita.mineclient.event.EventTick;
import chiquita.mineclient.mixin.IKeyBinding;
import chiquita.mineclient.module.Module;
import chiquita.mineclient.settings.Value;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AutoEat extends Module {

    public final Value<Integer> mode = new Value<>("Mode", new String[]{"Mode"}, "What mode to use, 1 = Hunger, 2 = Health, 3 = Both.", 1, 1, 3, 1, 0);
    public final Value<Integer> healthToEat = new Value<>("Health", new String[]{"Health"}, "How much health to heal at.", 10, 1, 36, 1, 0);
    public final Value<Integer> hungerToEat = new Value<>("Hunger", new String[]{"Hunger"}, "How much hunger to eat at.", 13, 1, 20, 1, 0);

    public List<Value> values = Arrays.asList(
            mode,
            healthToEat,
            hungerToEat
    );

    public AutoEat() {
        super("AutoEat", Category.player, true, GLFW.GLFW_KEY_Y);
        super.settings = values;
    }
    private int lastSlot = -1;
    private boolean eating = false;

    private boolean isValid(ItemStack stack, int food) {
        return stack.getItem().getGroup() == ItemGroup.FOOD && (20 - food) >= Objects.requireNonNull(stack.getItem().getFoodComponent()).getHunger();
    }

    @EventHandler
    private Listener<EventTick> eventTickListener = new Listener<>(event -> {
        assert mc.player != null;
        if (mode.getValue() == 1) {
            if (eating && (mc.player.getHungerManager().getFoodLevel() == 20)) {
                if (lastSlot != -1) {
                    mc.player.inventory.selectedSlot = lastSlot;
                    lastSlot = -1;
                }
                eating = false;
                KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), false);
                return;
            }
        }
        if (mode.getValue() == 2) {
            if (eating && (mc.player.getHealth() + mc.player.getAbsorptionAmount() > healthToEat.getValue())) {
                if (lastSlot != -1) {
                    mc.player.inventory.selectedSlot = lastSlot;
                    lastSlot = -1;
                }
                eating = false;
                KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), false);
                return;
            }
        }
        if (mode.getValue() == 3) {
            if (eating && (mc.player.getHealth() + mc.player.getAbsorptionAmount() > healthToEat.getValue()) && (mc.player.getHungerManager().getFoodLevel() == 20)) {
                if (lastSlot != -1) {
                    mc.player.inventory.selectedSlot = lastSlot;
                    lastSlot = -1;
                }
                eating = false;
                KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), false);
                return;
            }
        }
        if (eating) return;
        if (mode.getValue() == 1) {
            if (mc.player.getHungerManager().getFoodLevel() < hungerToEat.getValue()) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.inventory.getStack(i).isFood()) {
                        lastSlot = mc.player.inventory.selectedSlot;
                        mc.player.inventory.selectedSlot = i;
                        eating = true;
                        KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), true);
                        return;
                    }
                }
            }
        }
        if (mode.getValue() == 2) {
            if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= healthToEat.getValue()) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.inventory.getStack(i).isFood()) {
                        lastSlot = mc.player.inventory.selectedSlot;
                        mc.player.inventory.selectedSlot = i;
                        eating = true;
                        KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), true);
                        return;
                    }
                }
            }
        }
        if (mode.getValue() == 3) {
            if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= healthToEat.getValue() || mc.player.getHungerManager().getFoodLevel() < hungerToEat.getValue()) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.inventory.getStack(i).isFood()) {
                        lastSlot = mc.player.inventory.selectedSlot;
                        mc.player.inventory.selectedSlot = i;
                        eating = true;
                        KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), true);
                        return;
                    }
                }
            }
        }
    });
    public boolean isEating() {
        return eating;
    }
}