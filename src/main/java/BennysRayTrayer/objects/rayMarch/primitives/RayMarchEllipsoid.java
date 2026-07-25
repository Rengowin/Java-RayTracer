package BennysRayTrayer.objects.rayMarch.primitives;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;

public class RayMarchEllipsoid extends RayMarchObject {

    private final double radiusX;
    private final double radiusY;
    private final double radiusZ;

    public RayMarchEllipsoid(double radiusX, double radiusY, double radiusZ, BennysRayTrayer.rendering.Material material) {
        super(material);
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;

        double boundingRadius = Math.max(
                Math.abs(radiusX),
                Math.max(
                        Math.abs(radiusY),
                        Math.abs(radiusZ)
                )
        );

        setBoundingSphere(
                new Vec3(0, 0, 0),
                boundingRadius
        );
    }

    public RayMarchEllipsoid(Vec3 radius, BennysRayTrayer.rendering.Material material) {
        super(material);
        this.radiusX = radius.x;
        this.radiusY = radius.y;
        this.radiusZ = radius.z;

        double boundingRadius = Math.max(
                Math.abs(radiusX),
                Math.max(
                        Math.abs(radiusY),
                        Math.abs(radiusZ)
                )
        );

        setBoundingSphere(
                new Vec3(0, 0, 0),
                boundingRadius
        );
    }

    @Override
    protected double getLocalSDF(BennysRayTrayer.core.Vec3 p) {
        Vec3 q = new Vec3(
                (float) (p.x / radiusX),
                (float) (p.y / radiusY),
                (float) (p.z / radiusZ)
        );

        double k0 = q.length();

        Vec3 q2 = new Vec3(
                (float) (p.x / (radiusX * radiusX)),
                (float) (p.y / (radiusY * radiusY)),
                (float) (p.z / (radiusZ * radiusZ))
        );

        double k1 = q2.length();

        return k0 * (k0 - 1.0) / k1;
    }
}
