package xienaoban.minecraft.bole.client.highlight;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.mixin.IMixinFallingBlockEntity;

@Environment(EnvType.CLIENT)
public class ClientHighlightBlockEntity extends FallingBlockEntity {
    public ClientHighlightBlockEntity(World world, double x, double y, double z, BlockState block) {
        super(EntityType.FALLING_BLOCK, world);
        ((IMixinFallingBlockEntity) this).setBlock(block);
        setPosition(x, y, z);
        setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        setBoundingBox(new Box(x + 0.48, y + 0.02, z + 0.48, x + 0.52, y + 0.06, z + 0.52));
    }
}
