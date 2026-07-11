/*
package BennysRayTrayer.objects.rayMarch.primitives;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;

public class RayMarchRoundBox extends RayMarchObject {
    private double width;
    private double height;
    private double depth;
    private double radius;

    public RayMarchRoundBox(double width, double height, double depth, double radius) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.radius = radius;
    }

    @Override
    public double getSDF(Vec3 p) {
        Vec3 d = new Vec3(
                (float) (Math.abs(p.x) - width / 2),
                (float) (Math.abs(p.y) - height / 2),
                (float) (Math.abs(p.z) - depth / 2)
        );

        return Math.min(Math.max(d.x, Math.max(d.y, d.z)), 0.0) + Math.sqrt(Math.max(d.x, 0.0) * Math.max(d.x, 0.0) + Math.max(d.y, 0.0) * Math.max(d.y, 0.0) + Math.max(d.z, 0.0) * Math.max(d.z, 0.0)) - radius;
    }
}
*/
