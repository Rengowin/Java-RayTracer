package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class RayMarchIntersect extends RayMarchCSG {

    public RayMarchIntersect(RayMarchObject a, RayMarchObject b, CSGMaterialBlendMode blendMode) {
        super(a, b, blendMode);
    }

    public RayMarchIntersect(RayMarchObject a, RayMarchObject b) {
        super(a, b, CSGMaterialBlendMode.USE_A);
    }

    public RayMarchIntersect(RayMarchObject a, RayMarchObject b, Material material) {
        super(a, b, CSGMaterialBlendMode.USE_A);

        if (material != null) {
            setMaterial(material);
        }
    }

    @Override
    protected RayMarchObject getSurfaceObject(Vec3 point) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        // Intersection benutzt max(...)
        return d1 >= d2 ? a : b;
    }

    @Override
    public double getSDF(Vec3 point) {
        return Math.max(
                distanceA(point),
                distanceB(point)
        );
    }
}

