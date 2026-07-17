package BennysRayTrayer.objects.rayMarch;

import BennysRayTrayer.core.Hit;
import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.rendering.Material;

import java.util.ArrayList;
import java.util.List;

public abstract class RayMarchObject extends Object3D {

    protected static final double MAX_DISTANCE = 100.0;
    protected static final double MIN_DISTANCE = 0.001;
    protected static final int MAX_STEPS = 256;
    protected static final double EPSILON = 1e-4;

    private static final double SDF_DELTA = 1e-4;

    public RayMarchObject(Color color, Material material) {
        super(color, material);
    }

    public RayMarchObject(Color color) {
        super(color);
    }

    public RayMarchObject(Material material) {
        super(material);
    }

    public RayMarchObject() {
        super();
    }

    public abstract double getSDF(Vec3 point);

    public double getTransformedSDF(Vec3 worldPoint) {
        Vec3 localPoint = toLocalPoint(worldPoint);
        double localDistance = getSDF(localPoint);

        Vec3 scale = transform.getScale();

        double distanceScale = Math.min(
                Math.abs(scale.x),
                Math.min(
                        Math.abs(scale.y),
                        Math.abs(scale.z)
                )
        );

        return localDistance * distanceScale;
    }

    private Hit raymarchRay(Ray ray) {
        Vec3 currentPos = ray.origin;
        double totalDistance = 0.0;
        int steps = 0;

        while (steps < MAX_STEPS && totalDistance < MAX_DISTANCE) {
            double sdfValue = getTransformedSDF(currentPos);

            if (sdfValue < EPSILON) {
                Vec3 normal = calculateWorldNormal(currentPos);

                return new Hit(
                        totalDistance,
                        currentPos,
                        normal,
                        this
                );
            }

            double stepSize = Math.max(sdfValue, MIN_DISTANCE);

            currentPos = currentPos.add(
                    ray.direction.mul((float) stepSize)
            );

            totalDistance += stepSize;
            steps++;
        }

        return null;
    }

    private Vec3 calculateWorldNormal(Vec3 worldPoint) {
        float delta = (float) SDF_DELTA;

        double fx =
                getTransformedSDF(worldPoint.add(new Vec3(delta, 0, 0)))
                        - getTransformedSDF(worldPoint.add(new Vec3(-delta, 0, 0)));

        double fy =
                getTransformedSDF(worldPoint.add(new Vec3(0, delta, 0)))
                        - getTransformedSDF(worldPoint.add(new Vec3(0, -delta, 0)));

        double fz =
                getTransformedSDF(worldPoint.add(new Vec3(0, 0, delta)))
                        - getTransformedSDF(worldPoint.add(new Vec3(0, 0, -delta)));

        return new Vec3(
                (float) fx,
                (float) fy,
                (float) fz
        ).normalize();
    }

    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        Hit hit = raymarchRay(ray);

        List<HitInterval> intervals = new ArrayList<>();

        if (hit != null && hit.t > 0) {
            intervals.add(new HitInterval(
                    hit.t,
                    hit.t + 0.001,
                    hit.normal,
                    hit.normal.mul(-1),
                    this
            ));
        }

        return intervals;
    }
}
