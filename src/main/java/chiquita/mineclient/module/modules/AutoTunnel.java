package chiquita.mineclient.module.modules;

import chiquita.mineclient.event.EventMove;
import chiquita.mineclient.module.Module;
import chiquita.mineclient.module.ModuleManager;
import chiquita.mineclient.settings.Value;
import chiquita.mineclient.utils.EntityUtils;
import chiquita.mineclient.utils.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.minecraft.util.math.Direction.UP;

public class AutoTunnel extends Module
{

    public final Value<Boolean> pauseAutoWalk = new Value<>("PauseAutoWalk", new String[]{"Blocks"}, "Pause AutoWalk?", true);

    public AutoTunnel()
    {
        super("AutoTunnel", Category.world, false, GLFW.GLFW_KEY_Y);
    }

    private List<BlockPos> blocksToDestroy = new CopyOnWriteArrayList<>();
    private boolean needPause = false;
    private Timer pauseTimer = new Timer();

    @EventHandler
    private Listener<EventMove> eventMoveListener = new Listener<>(event -> {
        AutoEat autoEat = (AutoEat) ModuleManager.getModule(AutoEat.class);
        if (autoEat.isEating()) return;

        blocksToDestroy.clear();

        BlockPos playerPos = new BlockPos(Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ()));

        switch (EntityUtils.GetFacing())
        {
            case East:
                switch (1)
                {
                    case 1:
                        for (int i = 0; i < 3; ++i)
                        {
                            blocksToDestroy.add(playerPos.east());
                            blocksToDestroy.add(playerPos.east().up());
                            blocksToDestroy.add(playerPos.east().up().up());
                            blocksToDestroy.add(playerPos.east().north());
                            blocksToDestroy.add(playerPos.east().north().up());
                            blocksToDestroy.add(playerPos.east().north().up().up());

                            playerPos = new BlockPos(playerPos).east();
                        }
                        break;
                }
                break;
            case North:
                switch (1)
                {
                    case 1:
                        for (int i = 0; i < 3; ++i)
                        {
                            blocksToDestroy.add(playerPos.north());
                            blocksToDestroy.add(playerPos.north().up());
                            blocksToDestroy.add(playerPos.north().up().up());
                            blocksToDestroy.add(playerPos.north().east());
                            blocksToDestroy.add(playerPos.north().east().up());
                            blocksToDestroy.add(playerPos.north().east().up().up());

                            playerPos = new BlockPos(playerPos).north();
                        }
                        break;
                }
                break;
            case South:
                switch (1)
                {
                    case 1:
                        for (int i = 0; i < 3; ++i)
                        {
                            blocksToDestroy.add(playerPos.south());
                            blocksToDestroy.add(playerPos.south().up());
                            blocksToDestroy.add(playerPos.south().up().up());
                            blocksToDestroy.add(playerPos.south().west());
                            blocksToDestroy.add(playerPos.south().west().up());
                            blocksToDestroy.add(playerPos.south().west().up().up());

                            playerPos = new BlockPos(playerPos).south();
                        }
                        break;
                }
                break;
            case West:
                switch (1)
                {
                    case 1:
                        for (int i = 0; i < 3; ++i)
                        {
                            blocksToDestroy.add(playerPos.west());
                            blocksToDestroy.add(playerPos.west().up());
                            blocksToDestroy.add(playerPos.west().up().up());
                            blocksToDestroy.add(playerPos.west().south());
                            blocksToDestroy.add(playerPos.west().south().up());
                            blocksToDestroy.add(playerPos.west().south().up().up());

                            playerPos = new BlockPos(playerPos).west();
                        }
                        break;
                }
                break;
            default:
                break;
        }

        BlockPos toDestroy = null;

        for (BlockPos pos : blocksToDestroy)
        {
            BlockState state = mc.world.getBlockState(pos);

            if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.NETHERRACK || state.getBlock() == Blocks.CAVE_AIR || state.getBlock() == Blocks.VOID_AIR)
                continue;

            toDestroy = pos;
            break;
        }

        if (toDestroy != null) {
            Block td = mc.world.getBlockState(toDestroy).getBlock();
            if (td != Blocks.AIR && td != Blocks.NETHERRACK && td != Blocks.NETHER_PORTAL) {
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, toDestroy, UP));
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                        toDestroy, UP));
                needPause = true;
            }
        }
        else needPause = false;
    });

    public boolean PauseAutoWalk()
    {
        return needPause && pauseAutoWalk.getValue();
    }
}