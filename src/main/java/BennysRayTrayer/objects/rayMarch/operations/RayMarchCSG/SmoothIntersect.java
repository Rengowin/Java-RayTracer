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
        super(a, b, k, blendMode);
    }

    public SmoothIntersect(RayMarchObject a, RayMarchObject b, double k) {
        super(a, b, k);
    }

    public SmoothIntersect(RayMarchObject a, RayMarchObject b) {
        super(a, b);
    }

    @Override
    public double getLocalSDF(Vec3 point) {
        double d1 = getSDF(point);
        double d2 = getSDF(point);

        double h = smoothFactor(d1, d2);

        return Math.max(d1, d2) + h * h * k * 0.25;
    }
}
