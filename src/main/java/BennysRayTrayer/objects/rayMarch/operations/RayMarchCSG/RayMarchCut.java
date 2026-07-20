package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.objects.rayMarch.RayMarchObject;

public class RayMarchCut extends RayMarchCSG{

    public RayMarchCut(
            BennysRayTrayer.objects.rayMarch.RayMarchObject a,
            BennysRayTrayer.objects.rayMarch.RayMarchObject b,
            BennysRayTrayer.CSGMaterialBlendMode blendMode
    ) {
        super(a, b, blendMode);
    }

    public RayMarchCut(
            BennysRayTrayer.objects.rayMarch.RayMarchObject a,
            BennysRayTrayer.objects.rayMarch.RayMarchObject b
    ) {
        super(a, b, BennysRayTrayer.CSGMaterialBlendMode.USE_A);
    }

    public RayMarchCut(
            BennysRayTrayer.objects.rayMarch.RayMarchObject a,
            BennysRayTrayer.objects.rayMarch.RayMarchObject b,
            BennysRayTrayer.rendering.Material material
    ) {
        super(a, b, BennysRayTrayer.CSGMaterialBlendMode.USE_A);

        if (material != null) {
            setMaterial(material);
        }
    }

    @Override
    protected RayMarchObject getSurfaceObject(BennysRayTrayer.core.Vec3 point) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        return d1 >= d2 ? a : b;
    }

    @Override
    protected double getLocalSDF(BennysRayTrayer.core.Vec3 point) {
        return Math.max(
                distanceA(point),
                -distanceB(point)
        );
    }
}
