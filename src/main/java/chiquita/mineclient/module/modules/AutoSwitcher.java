package chiquita.mineclient.module.modules;

import chiquita.mineclient.event.EventTick;
import chiquita.mineclient.module.Module;
import chiquita.mineclient.module.ModuleManager;
import chiquita.mineclient.settings.Value;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class AutoSwitcher extends Module {


    public AutoSwitcher() {
        super("AutoSwitcher", Category.player, false, GLFW.GLFW_KEY_Y);
    }

    @EventHandler
    private Listener<EventTick> eventTickListener = new Listener<>(event -> {
        AutoEat autoEat = (AutoEat) ModuleManager.getModule(AutoEat.class);
        if (autoEat.isEating()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        int mode = 1;
        switch (mode) {
            case 0:
                if (player.inventory.getMainHandStack().isEmpty() || player.inventory.getMainHandStack().getItem() != Items.DIAMOND_PICKAXE || player.inventory.getMainHandStack().getItem() != Items.NETHERITE_PICKAXE) {
                    for (int i = 0; i < 9; i++) {
                        if (player.inventory.getStack(i).getItem() == Items.DIAMOND_PICKAXE || player.inventory.getStack(i).getItem() == Items.NETHERITE_PICKAXE) {
                            player.inventory.selectedSlot = i;
                            //                            player.inventory.swapSlotWithHotbar(i);
                            return;
                        }
                    }
                }
                break;
        }
    });
}