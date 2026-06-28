package BennysRayTrayer.objects.rayMarch;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.rendering.Material;

public class RayMarchCube extends RayMarchObject{

    private Vec3 halfSize;

    public RayMarchCube(Vec3 halfSize) {
        this.halfSize = halfSize;
    }

    public RayMarchCube(Vec3 halfSize, Color color) {
        super(color);
        this.halfSize = halfSize;
    }

    public RayMarchCube(Vec3 halfSize, Material material) {
        super(material);
        this.halfSize = halfSize;
    }

    public void setHalfSize(Vec3 halfSize) {
        this.halfSize = halfSize;
    }

    public void setHalfSize(double halfSize) {
        this.halfSize = new Vec3((float)halfSize, (float)halfSize, (float)halfSize);
    }

    @Override
    public double getSDF(Vec3 p) {
        Vec3 q = new Vec3(
                Math.abs(p.x) - halfSize.x,
                Math.abs(p.y) - halfSize.y,
                Math.abs(p.z) - halfSize.z
        );

        double outDist = Math.max(0, Math.max(q.x, Math.max(q.y, q.z)));

        double inDist = Math.min(0, Math.max(q.x, Math.max(q.y, q.z)));

        return outDist + inDist;
    }

}
