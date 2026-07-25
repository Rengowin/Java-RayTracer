package BennysRayTrayer.objects.rayMarch.primitives;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class RayMarchSpherePlate extends RayMarchObject {

    private final double sphereRadius;
    private final double thickness;
    private final Vec3 halfSize;
    private final Vec3 offset;

    public RayMarchSpherePlate(
            double sphereRadius,
            double thickness,
            Vec3 halfSize,
            Vec3 offset,
            Material material
    ) {
        super(material);

        this.sphereRadius = sphereRadius;
        this.thickness = thickness;
        this.halfSize = halfSize;
        this.offset = offset;

        setBoundingSphere(
                offset,
                halfSize.length()
        );
    }

    @Override
    protected double getLocalSDF(Vec3 p) {

        // Dünne Kugelschale
        double shell =
                Math.abs(p.length() - sphereRadius)
                        - thickness;

        // Begrenzung der Platte
        double bounds = boxSDF(
                p.sub(offset),
                halfSize
        );

        // Intersection:
        // Nur der Teil der Kugelschale innerhalb der Box bleibt übrig
        return Math.max(shell, bounds);
    }

    private double boxSDF(Vec3 p, Vec3 halfSize) {
        Vec3 q = new Vec3(
                Math.abs(p.x) - halfSize.x,
                Math.abs(p.y) - halfSize.y,
                Math.abs(p.z) - halfSize.z
        );

        Vec3 outside = new Vec3(
                Math.max(q.x, 0.0f),
                Math.max(q.y, 0.0f),
                Math.max(q.z, 0.0f)
        );

        double outsideDistance = outside.length();

        double insideDistance = Math.min(
                Math.max(q.x, Math.max(q.y, q.z)),
                0.0
        );

        return outsideDistance + insideDistance;
    }
}