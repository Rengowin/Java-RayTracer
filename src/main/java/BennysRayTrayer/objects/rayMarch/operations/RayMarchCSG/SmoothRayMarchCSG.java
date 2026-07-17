package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;

public abstract class SmoothRayMarchCSG extends RayMarchCSG{

    protected final double k;

    protected SmoothRayMarchCSG(RayMarchObject a, RayMarchObject b, double k, CSGMaterialBlendMode blendMode) {
        super(a, b, blendMode);
        this.k = Math.max(1e-6, k);
    }

    protected SmoothRayMarchCSG(RayMarchObject a, RayMarchObject b, double k) {
        this(a, b, k, CSGMaterialBlendMode.USE_A);
    }

    protected SmoothRayMarchCSG(RayMarchObject a, RayMarchObject b) {
        this(a, b, 1.0, CSGMaterialBlendMode.USE_A);
    }

    protected double smoothFactor(double d1, double d2) {
        return Math.max(k - Math.abs(d1 - d2), 0.0) / k;
    }

    @Override
    protected double calculateMaterialBlendFactor(Vec3 point) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        double factor = 0.5 + (d1 - d2) / (2.0 * k);

        return Math.max(0.0, Math.min(1.0, factor));
    }
}
