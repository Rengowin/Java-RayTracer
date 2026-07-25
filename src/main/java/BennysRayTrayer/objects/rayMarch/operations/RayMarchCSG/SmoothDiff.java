package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class SmoothDiff extends SmoothRayMarchCSG {


    public SmoothDiff(RayMarchObject a, RayMarchObject b, double k, CSGMaterialBlendMode blendMode) {
        super(a, b, k, blendMode, BoundingMode.USE_A);
    }

    public SmoothDiff(RayMarchObject a, RayMarchObject b, double k) {
        super(a, b, k, CSGMaterialBlendMode.USE_A, BoundingMode.USE_A);
    }

    public SmoothDiff(RayMarchObject a, RayMarchObject b) {
        super(a, b, BoundingMode.USE_A);
    }

    @Override
    public double getLocalSDF(Vec3 point) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        double h = Math.max(
                k - Math.abs(d1 + d2),
                0.0
        ) / k;

        return Math.max(d1, -d2)
                + h * h * k * 0.25;
    }
}
