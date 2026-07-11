package BennysRayTrayer.objects.rayMarch.primitives;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class RayMarchCapsule extends RayMarchObject {

    private double radius;
    private double height;

    public RayMarchCapsule(double radius, double height) {
        this.radius = radius;
        this.height = height;
    }

    public RayMarchCapsule(double radius, double height, Material material) {
        super(material);
        this.radius = radius;
        this.height = height;
    }

    public RayMarchCapsule(double radius, double height, Color color) {
        super(color);
        this.radius = radius;
        this.height = height;
    }

    @Override
    public double getSDF(Vec3 p) {
        Vec3 d = new Vec3(
                (float) (Math.sqrt(p.x * p.x + p.z * p.z) - radius),
                (float) (Math.abs(p.y) - height / 2),
                0
        );

        return Math.min(Math.max(d.x, d.y), 0.0) + Math.sqrt(Math.max(d.x, 0.0) * Math.max(d.x, 0.0) + Math.max(d.y, 0.0) * Math.max(d.y, 0.0));
    }

}
