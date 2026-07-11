package BennysRayTrayer.objects.rayMarch.operations;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;

public class SmoothDiff extends RayMarchObject {
    private final RayMarchObject a;
    private final RayMarchObject b;
    private final double k;

    public SmoothDiff(RayMarchObject a, RayMarchObject b, double k) {
        this.a = a;
        this.b = b;
        this.k = Math.max(1e-6, k);

        if (a.getMaterial() != null) {
            setMaterial(a.getMaterial());
        }

        if (a.getColor() != null) {
            setColor(a.getColor());
        }
    }

    @Override
    public double getSDF(Vec3 point) {
        Vec3 pointInA = a.getTransform().worldToLocalPoint(point);
        Vec3 pointInB = b.getTransform().worldToLocalPoint(point);

        double d1 = a.getSDF(pointInA);
        double d2 = b.getSDF(pointInB);
        double h = Math.max(k - Math.abs(d1 + d2), 0.0) / k;
        return Math.max(d1, -d2) + h * h * k * 0.25;
    }
}
