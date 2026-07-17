package BennysRayTrayer.objects.rayMarch.primitives;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class RayMarchBox extends RayMarchObject {

    private Vec3 halfSize;

    public RayMarchBox(Vec3 halfSize) {
        this.halfSize = halfSize;
    }

    public RayMarchBox(Vec3 halfSize, Color color) {
        super(color);
        this.halfSize = halfSize;
    }

    public RayMarchBox(Vec3 halfSize, Material material) {
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
    public double getLocalSDF(Vec3 p) {
        Vec3 q = new Vec3(
                Math.abs(p.x) - halfSize.x,
                Math.abs(p.y) - halfSize.y,
                Math.abs(p.z) - halfSize.z
        );

        Vec3 outside = new Vec3(
                Math.max(q.x, 0),
                Math.max(q.y, 0),
                Math.max(q.z, 0)
        );

        double outsideDistance = outside.length();
        double insideDistance = Math.min(
                Math.max(q.x, Math.max(q.y, q.z)),
                0.0
        );

        return outsideDistance + insideDistance;
    }
}
