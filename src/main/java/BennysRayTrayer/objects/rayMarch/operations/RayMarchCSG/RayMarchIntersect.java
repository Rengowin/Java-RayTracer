package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class RayMarchIntersect extends RayMarchObject {

    private final RayMarchObject a;
    private final RayMarchObject b;

    public RayMarchIntersect(
            RayMarchObject a,
            RayMarchObject b,
            Material material
    ) {
        super(material);
        this.a = a;
        this.b = b;
    }

    public RayMarchIntersect(
            RayMarchObject a,
            RayMarchObject b
    ) {
        this(a, b, null);
    }

    @Override
    public double getSDF(Vec3 p) {
        return Math.max(
                a.getSDF(p),
                b.getSDF(p)
        );
    }
}
