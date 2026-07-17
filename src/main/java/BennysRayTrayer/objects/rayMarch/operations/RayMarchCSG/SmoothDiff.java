package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class SmoothDiff extends SmoothRayMarchCSG {


    public SmoothDiff(RayMarchObject a, RayMarchObject b, double k, CSGMaterialBlendMode blendMode) {
        super(a, b, k, blendMode);
    }

    public SmoothDiff(RayMarchObject a, RayMarchObject b, double k) {
        super(a, b, k, CSGMaterialBlendMode.USE_A);
    }

    public SmoothDiff(RayMarchObject a, RayMarchObject b) {
        super(a, b);
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
