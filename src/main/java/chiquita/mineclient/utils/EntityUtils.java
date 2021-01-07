package chiquita.mineclient.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class EntityUtils {

    static MinecraftClient mc = MinecraftClient.getInstance();

    public static final List<Block> NONSOLID_BLOCKS = Arrays.asList(
            Blocks.AIR, Blocks.LAVA, Blocks.WATER, Blocks.GRASS,
            Blocks.VINE, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS,
            Blocks.SNOW, Blocks.TALL_GRASS, Blocks.FIRE, Blocks.VOID_AIR);

    public static final List<Block> RIGHTCLICKABLE_BLOCKS = Arrays.asList(
            Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
            Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL,
            Blocks.OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.BIRCH_BUTTON, Blocks.DARK_OAK_BUTTON,
            Blocks.JUNGLE_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.STONE_BUTTON, Blocks.COMPARATOR,
            Blocks.REPEATER, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE,
            Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE,
            Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER,
            Blocks.LEVER, Blocks.NOTE_BLOCK, Blocks.JUKEBOX,
            Blocks.BEACON, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED,
            Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED,
            Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.RED_BED, Blocks.WHITE_BED,
            Blocks.YELLOW_BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR,
            Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR,
            Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE,
            Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK,
            Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE,
            Blocks.ACACIA_TRAPDOOR, Blocks.BIRCH_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR,
            Blocks.OAK_TRAPDOOR, Blocks.SPRUCE_TRAPDOOR, Blocks.CAKE, Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN,
            Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN,
            Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN,
            Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN);

    public static boolean isBlockEmpty(BlockPos pos) {
        if (!NONSOLID_BLOCKS.contains(mc.world.getBlockState(pos).getBlock()))
            return false;

        Box box = new Box(pos);
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof LivingEntity && box.intersects(e.getBoundingBox()))
                return false;
        }

        return true;
    }


    public static boolean placeBlock(BlockPos pos, int slot, boolean rotate, boolean rotateBack) {
        if (pos.getY() < 0 || pos.getY() > 255 || !isBlockEmpty(pos))
            return false;

        if (slot != mc.player.inventory.selectedSlot && slot >= 0 && slot <= 8)
            mc.player.inventory.selectedSlot = slot;

        for (Direction d : Direction.values()) {
            if ((d == Direction.DOWN && pos.getY() == 0) || (d == Direction.UP && pos.getY() == 255))
                continue;

            Block neighborBlock = mc.world.getBlockState(pos.offset(d)).getBlock();

            Vec3d vec = new Vec3d(pos.getX() + 0.5 + d.getOffsetX() * 0.5,
                    pos.getY() + 0.5 + d.getOffsetY() * 0.5,
                    pos.getZ() + 0.5 + d.getOffsetZ() * 0.5);

            if (NONSOLID_BLOCKS.contains(neighborBlock)
                    || mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0).distanceTo(vec) > 4.55)
                continue;

            float[] rot = new float[] { mc.player.yaw, mc.player.pitch };

            if (rotate)
                facePosPacket(vec.x, vec.y, vec.z);
            if (RIGHTCLICKABLE_BLOCKS.contains(neighborBlock))
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));

            mc.interactionManager.interactBlock(
                    mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(pos), d.getOpposite(), pos.offset(d), false));

            if (RIGHTCLICKABLE_BLOCKS.contains(neighborBlock))
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            if (rotateBack)
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(rot[0], rot[1], mc.player.isOnGround()));
            return true;
        }
        return false;
    }

    public enum FacingDirection
    {
        North,
        South,
        East,
        West,
        SouthEast,
        SouthWest,
        NorthWest,
        NorthEast,
    }

    public static FacingDirection GetFacing()
    {
        switch (MathHelper.floor((double) (mc.player.yaw * 8.0F / 360.0F) + 0.5D) & 7)
        {
            case 0:
            case 1:
                return FacingDirection.South;
            case 2:
            case 3:
                return FacingDirection.West;
            case 4:
            case 5:
                return FacingDirection.North;
            case 6:
            case 7:
                return FacingDirection.East;
            case 8:
        }
        return FacingDirection.North;
    }

    public enum highways {
        XP, XN, ZP, ZN, XPZP, XNZP, XPZN, XNZN
    }

    public static highways determineHighway() {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        highways highwayNum = highways.XP;
        if (player.getX() >= 100) {
            if (player.getZ() >= -5 && player.getZ() <= 5) {
                //+X highway
                highwayNum = highways.XP;
            }
            else if (player.getZ() - player.getX() >= -50 && player.getZ() - player.getX() <= 50) {
                //+X+Z highway
                highwayNum = highways.XPZP;
            }
            else if (player.getZ() + player.getX() >= -50 && player.getZ() + player.getX() <= 50) {
                //+X-Z highway
                highwayNum = highways.XPZN;
            }
        }
        else if (player.getX() <= -100) {
            if (player.getZ() >= -5 && player.getZ() <= 5) {
                //-X highway
                highwayNum = highways.XN;
            }
            else if (player.getX() + player.getZ() >= -50 && player.getX() + player.getZ() <= 50) {
                //-X+Z highway
                highwayNum = highways.XNZP;
            }
            else if (player.getZ() <= player.getX() + 100 && player.getZ() >= player.getX() - 100) {
                //-X-Z highway
                highwayNum = highways.XNZN;
            }
        }
        else if (player.getZ() >= 100) {
            if (player.getX() >= -5 && player.getX() <= 5) {
                //+Z highway
                highwayNum = highways.ZP;
            }
        }
        else if (player.getZ() <= -100) {
            if (player.getX() >= -5 && player.getX() <= 5) {
                //-Z highway
                highwayNum = highways.ZN;
            }
        }
        return highwayNum;
    }

    public static boolean isAnimal(Entity e) {
        return e instanceof PassiveEntity || e instanceof AmbientEntity || e instanceof WaterCreatureEntity || e instanceof GolemEntity;
    }

    public static void facePosAuto(double x, double y, double z, boolean sr) {
        if (sr) {
            facePosPacket(x, y, z);
        }
        else {
            facePos(x, y, z);
        }
    }

    public static void facePosPacket(double x, double y, double z) {
        double diffX = x - mc.player.getX();
        double diffY = y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = z - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.LookOnly(
                        mc.player.yaw + MathHelper.wrapDegrees(yaw - mc.player.yaw),
                        mc.player.pitch + MathHelper.wrapDegrees(pitch - mc.player.pitch), mc.player.isOnGround()));
    }

    public static int getPitchNeeded(double x, double y, double z) {
        double diffX = x - mc.player.getX();
        double diffY = y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = z - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        return (int) (mc.player.pitch + MathHelper.wrapDegrees(pitch - mc.player.pitch));
    }

    public static int getYawNeeded(double x, double z) {
        double diffX = x - mc.player.getX();
        double diffZ = z - mc.player.getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        return (int) (mc.player.yaw + MathHelper.wrapDegrees(yaw - mc.player.yaw));
    }

    public static void facePos(double x, double y, double z) {
        double diffX = x - mc.player.getX();
        double diffY = y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = z - mc.player.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        mc.player.yaw += MathHelper.wrapDegrees(yaw - mc.player.yaw);
        mc.player.pitch += MathHelper.wrapDegrees(pitch - mc.player.pitch);
    }

    public static Vec3d prevPos() {
        return new Vec3d(mc.player.prevX, mc.player.prevY, mc.player.prevZ);
    }

    public static Vec3d getInterpPos(float tickDelta) {
        Vec3d prev = prevPos();
        return addVec3d(prev, minusVec3d(mc.player.getPos(), prev).multiply(tickDelta));
    }

    public static Vec3d minusVec3d(Vec3d one, Vec3d two) {
        return addVec3d(one, two.negate());
    }

    public static Vec3d addVec3d(Vec3d one, Vec3d two) {
        return new Vec3d(one.x + two.x, one.y + two.y, one.z + two.z);
    }

    public static Vec3d addVec3d(Vec3d one, double blah) {
        return new Vec3d(one.x + blah, one.y + blah, one.z + blah);
    }

    public static Vec3d vec3dUp(Vec3d one, double blah) {
        return new Vec3d(one.x, one.y + blah, one.z);
    }

    public static Vec3d divVec3d(Vec3d one, double blah) {
        return new Vec3d(one.x / blah, one.y / blah, one.z / blah);
    }

    public static Pair<BlockPos, Direction> getIrreplaceableNeighbor(BlockPos blockPos, World world) {
        if (blockPos == null) return null;
        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            if (world.getBlockState(neighbor) != null) {
                if (world.getBlockState(neighbor).getMaterial() != null) {
                    if (world.getBlockState(neighbor).getMaterial().isReplaceable()) return new Pair<>(neighbor, side.getOpposite());
                }
            }
        }
        return null;
    }

    public static Vec3d asVec(BlockPos blockPos) {
        return new Vec3d((double) blockPos.getX(), (double) blockPos.getY(), (double) blockPos.getZ());
    }

    public static Vec3d asVec(Vec3i vec3i) {
        return new Vec3d((double) vec3i.getX(), (double) vec3i.getY(), (double) vec3i.getZ());
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return entity.getPos().subtract(entity.prevX, entity.prevY, entity.prevZ).multiply(x, y, z);
    }
    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }
    public static BlockPos toBlockPos(Vec3d vec3d) {
        return new BlockPos(vec3d.x, vec3d.y, vec3d.z);
    }
}