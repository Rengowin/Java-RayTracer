package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;

public abstract class SmoothRayMarchCSG
        extends RayMarchCSG {

    protected final double k;

    protected SmoothRayMarchCSG(
            RayMarchObject a,
            RayMarchObject b,
            double k,
            CSGMaterialBlendMode blendMode,
            BoundingMode boundingMode
    ) {
        super(
                a,
                b,
                blendMode,
                boundingMode
        );

        this.k = Math.max(1e-6, k);

        expandBoundingSphereForSmoothness();
    }

    protected SmoothRayMarchCSG(
            RayMarchObject a,
            RayMarchObject b,
            double k,
            BoundingMode boundingMode
    ) {
        this(
                a,
                b,
                k,
                CSGMaterialBlendMode.USE_A,
                boundingMode
        );
    }

    protected SmoothRayMarchCSG(
            RayMarchObject a,
            RayMarchObject b,
            BoundingMode boundingMode
    ) {
        this(
                a,
                b,
                1.0,
                CSGMaterialBlendMode.USE_A,
                boundingMode
        );
    }

    private void expandBoundingSphereForSmoothness() {
        if (!hasBoundingSphere()) {
            return;
        }

        setBoundingSphere(
                getBoundingCenter(),
                getBoundingRadius() + k
        );
    }

    protected double smoothFactor(
            double d1,
            double d2
    ) {
        return Math.max(
                k - Math.abs(d1 - d2),
                0.0
        ) / k;
    }

    @Override
    protected double calculateMaterialBlendFactor(
            Vec3 point
    ) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        double factor =
                0.5 + (d2 - d1) / (2.0 * k);

        return Math.max(
                0.0,
                Math.min(1.0, factor)
        );
    }
}