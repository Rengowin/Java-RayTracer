package BennysRayTrayer.objects.rayMarch.primitives;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;

public class RayMarchHalfSpace extends RayMarchObject {

    private final Vec3 normal;
    private final double distance;

    public RayMarchHalfSpace(Vec3 normal, double distance) {
        this.normal = normal.normalize();
        this.distance = distance;
    }

    @Override
    public double getSDF(Vec3 p) {
        return normal.dot(p) - distance;
    }
}
