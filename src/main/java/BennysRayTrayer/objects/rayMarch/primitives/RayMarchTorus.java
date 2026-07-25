package BennysRayTrayer.objects.rayMarch.primitives;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class RayMarchTorus extends RayMarchObject {

    private double outerR;
    private double innerR;

    public RayMarchTorus(double outerR, double innerR) {
        this.outerR = outerR;
        this.innerR = innerR;

        setBoundingSphere(
                new Vec3(0, 0, 0),
                outerR + innerR
        );
    }
    public RayMarchTorus(double outerR, double innerR, Color color) {
        super(color);
        this.outerR = outerR;
        this.innerR = innerR;

        setBoundingSphere(
                new Vec3(0, 0, 0),
                outerR + innerR
        );
    }

    public RayMarchTorus(double outerR, double innerR, Material material) {
        super(material);
        this.outerR = outerR;
        this.innerR = innerR;

        setBoundingSphere(
                new Vec3(0, 0, 0),
                outerR + innerR
        );
    }

    @Override
    public double getLocalSDF(Vec3 p) {
        Vec3 q = new Vec3(
                (float) (Math.sqrt(p.x * p.x + p.z * p.z) - outerR),
                p.y,
                0
        );

        return Math.sqrt(q.x * q.x + q.y * q.y) - innerR;
    }
}
