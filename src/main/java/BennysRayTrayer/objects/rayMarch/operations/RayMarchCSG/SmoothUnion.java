package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class SmoothUnion extends SmoothRayMarchCSG
{
    public SmoothUnion(RayMarchObject a, RayMarchObject b, double k, CSGMaterialBlendMode blendMode) {
        super(a, b, k, blendMode);
    }

    public SmoothUnion(RayMarchObject a, RayMarchObject b, double k) {
        super(a, b, k);
    }

    public SmoothUnion(RayMarchObject a, RayMarchObject b) {
        super(a, b);
    }

    @Override
    protected double getLocalSDF(Vec3 point) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        double h = Math.max(
                k - Math.abs(d1 - d2),
                0.0
        ) / k;

        return Math.min(d1, d2)
                - h * h * k * 0.25;
    }
}
