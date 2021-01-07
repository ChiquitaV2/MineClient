package chiquita.mineclient.event;

import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class EventMove {
    public MovementType Type;
    public Vec3d vec3d;

    public EventMove(MovementType p_Type, Vec3d vec3d_1)
    {
        this.Type = p_Type;
        this.vec3d = vec3d_1;
    }
}