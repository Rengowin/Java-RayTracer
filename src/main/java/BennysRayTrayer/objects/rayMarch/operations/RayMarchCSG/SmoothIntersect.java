package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class SmoothIntersect extends SmoothRayMarchCSG {

    public SmoothIntersect(
            RayMarchObject a,
            RayMarchObject b,
            double k,
            CSGMaterialBlendMode blendMode
    ) {
        super(a, b, k, blendMode, BoundingMode.INTERSECTION);
    }

    public SmoothIntersect(RayMarchObject a, RayMarchObject b, double k) {
        super(a, b, k, BoundingMode.INTERSECTION);
    }

    public SmoothIntersect(RayMarchObject a, RayMarchObject b) {
        super(a, b, BoundingMode.INTERSECTION);
    }

    @Override
    public double getLocalSDF(Vec3 point) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        double h = Math.max(
                k - Math.abs(d1 - d2),
                0.0
        ) / k;

        return Math.max(d1, d2)
                + h * h * k * 0.25;
    }
}
