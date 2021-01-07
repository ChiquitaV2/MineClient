package chiquita.mineclient.module.modules;

import chiquita.mineclient.event.EventTick;
import chiquita.mineclient.module.Module;
import chiquita.mineclient.settings.Value;
import chiquita.mineclient.utils.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HotbarCache extends Module
{
    public final Value<Integer> Mode = new Value<Integer>("Mode", new String[] {"M"}, "The mode of refilling to use, Refill may cause desync", 1, 1, 2, 1, 0);
    public final Value<Float> Delay = new Value<Float>("Delay", new String[] {"D"}, "Delay to use", 1.0f, 0.0f, 10.0f, 1.0f, 1);

    public List<Value> values = Arrays.asList(
            Mode, Delay
    );

    public HotbarCache()
    {
        super("HotbarCache", Category.player, true, GLFW.GLFW_KEY_Y);
        super.settings = values;
    }

    private ArrayList<Item> Hotbar = new ArrayList<Item>();
    private Timer timer = new Timer();

    static MinecraftClient staticMC = MinecraftClient.getInstance();

    @Override
    public void onEnable()
    {
        super.onEnable();
        Hotbar.clear();
        for (int i = 0; i < 9; ++i) {
            ItemStack l_Stack = mc.player.inventory.getStack(i);
            if (!l_Stack.isEmpty() && !Hotbar.contains(l_Stack.getItem()))
                Hotbar.add(l_Stack.getItem());
            else
                Hotbar.add(Items.AIR);
        }
    }

    /// Don't activate on startup
    @Override
    public void toggleNoSave() {
    }

    @EventHandler
    private Listener<EventTick> OnPlayerUpdate = new Listener<>(p_Event -> {
        if (!timer.passed(Delay.getValue() * 1000))
            return;

        switch (Mode.getValue()) {
            case 1:
                for (int i = 0; i < 9; ++i) {
                    if (switchSlotIfNeed(i)) {
                        timer.reset();
                        return;
                    }
                }
                break;
            case 2:
                for (int i = 0; i < 9; ++i) {
                    if (refillSlotIfNeed(i)) {
                        timer.reset();
                        return;
                    }
                }
                break;
            default:
                break;
        }
    });

    private boolean switchSlotIfNeed(int targetSlot) {
        Item targetItem = Hotbar.get(targetSlot);

        if (targetItem == Items.AIR)
            return false;

        if (!mc.player.inventory.getStack(targetSlot).isEmpty() && mc.player.inventory.getStack(targetSlot).getItem() == targetItem)
            return false;

        int slotFromCache = getItemSlot(targetItem);

        if (slotFromCache != -1 && slotFromCache != 45) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotFromCache, 0,
                    SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, targetSlot+36, 0, SlotActionType.PICKUP,
                    mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotFromCache, 0,
                    SlotActionType.PICKUP, mc.player);
            mc.interactionManager.tick();

            return true;
        }

        return false;
    }

    private boolean refillSlotIfNeed(int targetSlot) {
        ItemStack targetStack = mc.player.inventory.getStack(targetSlot);

        if (targetStack.isEmpty() || targetStack.getItem() == Items.AIR)
            return false;

        if (!targetStack.isStackable())
            return false;

        if (targetStack.getCount() >= targetStack.getMaxCount())
            return false;

        /// We're going to search the entire inventory for the same stack, WITH THE SAME NAME, and use quick move.
        for (int i = 9; i < 36; ++i) {
            final ItemStack currentItem = mc.player.inventory.getStack(i);

            if (currentItem.isEmpty())
                continue;

            if (canItemBeMergedWith(targetStack, currentItem)) {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0,
                        SlotActionType.QUICK_MOVE, mc.player);
                mc.interactionManager.tick();

                /// Check again for more next available tick
                return true;
            }
        }

        return false;
    }

    private boolean canItemBeMergedWith(ItemStack source, ItemStack target) {
        return source.getItem() == target.getItem() && source.getName().equals(target.getName());
    }

    public static int getItemSlot(Item input) {
        if (staticMC.player == null)
            return 0;

        for (int i = 0; i < staticMC.player.inventory.size(); ++i) {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
                continue;

            ItemStack s = staticMC.player.inventory.getStack(i);

            if (s.isEmpty())
                continue;

            if (s.getItem() == input) {
                return i;
            }
        }
        return -1;
    }
}