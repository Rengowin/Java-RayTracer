package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.objects.rayMarch.RayMarchObject;

public class RayMarchUnion extends RayMarchCSG{

    public RayMarchUnion(
            BennysRayTrayer.objects.rayMarch.RayMarchObject a,
            BennysRayTrayer.objects.rayMarch.RayMarchObject b,
            BennysRayTrayer.CSGMaterialBlendMode blendMode
    ) {
        super(a, b, blendMode,BoundingMode.UNION);
    }

    public RayMarchUnion(
            BennysRayTrayer.objects.rayMarch.RayMarchObject a,
            BennysRayTrayer.objects.rayMarch.RayMarchObject b
    ) {
        super(a, b, BennysRayTrayer.CSGMaterialBlendMode.USE_A, BoundingMode.UNION);
    }

    public RayMarchUnion(
            BennysRayTrayer.objects.rayMarch.RayMarchObject a,
            BennysRayTrayer.objects.rayMarch.RayMarchObject b,
            BennysRayTrayer.rendering.Material material
    ) {
        super(a, b, BennysRayTrayer.CSGMaterialBlendMode.USE_A, BoundingMode.UNION);

        if (material != null) {
            setMaterial(material);
        }
    }

    @Override
    protected RayMarchObject getSurfaceObject(BennysRayTrayer.core.Vec3 point) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        return d1 <= d2 ? a : b;
    }

    @Override
    protected double getLocalSDF(BennysRayTrayer.core.Vec3 point) {
        return Math.min(
                distanceA(point),
                distanceB(point)
        );
    }
}
