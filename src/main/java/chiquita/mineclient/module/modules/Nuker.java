package chiquita.mineclient.module.modules;

import chiquita.mineclient.event.EventTick;
import chiquita.mineclient.module.Module;
import chiquita.mineclient.module.ModuleManager;
import chiquita.mineclient.settings.Value;
import chiquita.mineclient.utils.EntityUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nuker extends Module {

    private List<Block> blockList = new ArrayList<>();


    public Nuker() {
        super("Nuker", Category.world, false, GLFW.GLFW_KEY_Y);
    }

    private BlockPos lastPlayerPos = null;

    private List<BlockPos> getBlocks() {
        int mode = 1;
        List<BlockPos> blocks = new ArrayList<>();
        if (this.isToggled()) {
            switch (mode) {
                case 1:
                    blocks = get2x3();
                    break;
            }
        }
        return blocks;
    }
    public boolean canSeeBlock(BlockPos pos) {
        double diffX = pos.getX() + 0.5 - mc.player.getCameraPosVec(mc.getTickDelta()).x;
        double diffY = pos.getY() + 0.5 - mc.player.getCameraPosVec(mc.getTickDelta()).y;
        double diffZ = pos.getZ() + 0.5 - mc.player.getCameraPosVec(mc.getTickDelta()).z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = mc.player.yaw + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90 - mc.player.yaw);
        float pitch = mc.player.pitch + MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.pitch);

        Vec3d rotation = new Vec3d(
                (double) (MathHelper.sin(-yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F)),
                (double) (-MathHelper.sin(pitch * 0.017453292F)),
                (double) (MathHelper.cos(-yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F)));

        Vec3d rayVec = mc.player.getCameraPosVec(mc.getTickDelta()).add(rotation.x * 6, rotation.y * 6, rotation.z * 6);
        return mc.world.raycast(new RaycastContext(mc.player.getCameraPosVec(mc.getTickDelta()),
                rayVec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player))
                .getBlockPos().equals(pos);
    }

    @EventHandler
    private Listener<EventTick> eventTickListener = new Listener<>(event -> {
        List<BlockPos> blocks = getBlocks();
        double range = 6;

        AutoEat autoEat = (AutoEat) ModuleManager.getModule(AutoEat.class);

        if (autoEat.isEating()) return;

        if (blocks.isEmpty()) return;

        for (BlockPos pos : blocks) {
            if (!canSeeBlock(pos) || (mc.world.getBlockState(pos).getBlock() != Blocks.NETHERRACK && mc.world.getBlockState(pos).getBlock() != Blocks.SPONGE && mc.world.getBlockState(pos).getBlock() != Blocks.WET_SPONGE))
                continue;

            Vec3d vec = Vec3d.of(pos).add(0.5, 0.5, 0.5);

            if (mc.player.getPos().distanceTo(vec) > range + 0.5) continue;

            Direction dir = null;
            double dist = Double.MAX_VALUE;
            for (Direction d : Direction.values()) {
                double dist2 = mc.player.getPos().distanceTo(Vec3d.of(pos.offset(d)).add(0.5, 0.5, 0.5));
                if (dist2 > range || mc.world.getBlockState(pos.offset(d)).getBlock() != Blocks.AIR || dist2 > dist)
                    continue;
                dist = dist2;
                dir = d;
            }

            if (dir == null) continue;

            mc.interactionManager.attackBlock(pos, dir);
        }
    });

    public List<BlockPos> getCube() {
        List<BlockPos> cubeBlocks = new ArrayList<>();
        BlockPos playerPos = new BlockPos(Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ()));
        if (lastPlayerPos == null || !lastPlayerPos.equals(playerPos)) {
            switch (EntityUtils.GetFacing()) {
                case East:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.east());
                        cubeBlocks.add(playerPos.east().up());
                        cubeBlocks.add(playerPos.east().up().up());
                        cubeBlocks.add(playerPos.east().north());
                        cubeBlocks.add(playerPos.east().north().up());
                        cubeBlocks.add(playerPos.east().north().up().up());
                        cubeBlocks.add(playerPos.east().south());
                        cubeBlocks.add(playerPos.east().south().up());
                        cubeBlocks.add(playerPos.east().south().up().up());

                        playerPos = new BlockPos(playerPos).east();
                    }
                    break;
                case North:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.north());
                        cubeBlocks.add(playerPos.north().up());
                        cubeBlocks.add(playerPos.north().up().up());
                        cubeBlocks.add(playerPos.north().east());
                        cubeBlocks.add(playerPos.north().east().up());
                        cubeBlocks.add(playerPos.north().east().up().up());
                        cubeBlocks.add(playerPos.north().west());
                        cubeBlocks.add(playerPos.north().west().up());
                        cubeBlocks.add(playerPos.north().west().up().up());

                        playerPos = new BlockPos(playerPos).north();
                    }
                    break;
                case South:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.south());
                        cubeBlocks.add(playerPos.south().up());
                        cubeBlocks.add(playerPos.south().up().up());
                        cubeBlocks.add(playerPos.south().west());
                        cubeBlocks.add(playerPos.south().west().up());
                        cubeBlocks.add(playerPos.south().west().up().up());
                        cubeBlocks.add(playerPos.south().east());
                        cubeBlocks.add(playerPos.south().east().up());
                        cubeBlocks.add(playerPos.south().east().up().up());

                        playerPos = new BlockPos(playerPos).south();
                    }
                    break;
                case West:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.west());
                        cubeBlocks.add(playerPos.west().up());
                        cubeBlocks.add(playerPos.west().up().up());
                        cubeBlocks.add(playerPos.west().south());
                        cubeBlocks.add(playerPos.west().south().up());
                        cubeBlocks.add(playerPos.west().south().up().up());
                        cubeBlocks.add(playerPos.west().north());
                        cubeBlocks.add(playerPos.west().north().up());
                        cubeBlocks.add(playerPos.west().north().up().up());


                        playerPos = new BlockPos(playerPos).west();
                    }
                    break;
            }
        }
        return cubeBlocks;
    }


    public List<BlockPos> get2x3() {
        List<BlockPos> cubeBlocks = new ArrayList<>();
        BlockPos playerPos = new BlockPos(Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ()));
        if (lastPlayerPos == null || !lastPlayerPos.equals(playerPos)) {
            switch (EntityUtils.GetFacing()) {
                case East:
                    for (int i = 0; i < 4; ++i) {
                        cubeBlocks.add(playerPos.east());
                        cubeBlocks.add(playerPos.east().up());
                        cubeBlocks.add(playerPos.east().up().up());
                        cubeBlocks.add(playerPos.east().north());
                        cubeBlocks.add(playerPos.east().north().up());
                        cubeBlocks.add(playerPos.east().north().up().up());
                        playerPos = new BlockPos(playerPos).east();
                    }
                    break;
                case North:
                    for (int i = 0; i < 4; ++i) {
                        cubeBlocks.add(playerPos.north());
                        cubeBlocks.add(playerPos.north().up());
                        cubeBlocks.add(playerPos.north().up().up());
                        cubeBlocks.add(playerPos.north().east());
                        cubeBlocks.add(playerPos.north().east().up());
                        cubeBlocks.add(playerPos.north().east().up().up());
                        playerPos = new BlockPos(playerPos).north();
                    }
                    break;
                case South:
                    for (int i = 0; i < 4; ++i) {
                        cubeBlocks.add(playerPos.south());
                        cubeBlocks.add(playerPos.south().up());
                        cubeBlocks.add(playerPos.south().up().up());
                        cubeBlocks.add(playerPos.south().west());
                        cubeBlocks.add(playerPos.south().west().up());
                        cubeBlocks.add(playerPos.south().west().up().up());
                        playerPos = new BlockPos(playerPos).south();
                    }
                    break;
                case West:
                    for (int i = 0; i < 4; ++i) {
                        cubeBlocks.add(playerPos.west());
                        cubeBlocks.add(playerPos.west().up());
                        cubeBlocks.add(playerPos.west().up().up());
                        cubeBlocks.add(playerPos.west().south());
                        cubeBlocks.add(playerPos.west().south().up());
                        cubeBlocks.add(playerPos.west().south().up().up());
                        playerPos = new BlockPos(playerPos).west();
                    }
                    break;
            }
        }
        return cubeBlocks;
    }

}