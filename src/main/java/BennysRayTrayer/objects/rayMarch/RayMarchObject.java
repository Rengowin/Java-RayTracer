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
    protected static final double MIN_DISTANCE = 1e-5;
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

    /**
     * Die eigentliche Form im lokalen Koordinatensystem.
     */
    protected abstract double getLocalSDF(Vec3 localPoint);

    /**
     * Wertet dieses Objekt in seinem Parent-Koordinatensystem aus.
     *
     * Bei einem Root-Objekt ist der Parent-Space die Welt.
     * Bei einem Child in einer CSG-Operation ist der Parent-Space
     * der lokale Raum der CSG-Operation.
     */
    public final double getSDF(Vec3 pointInParentSpace) {
        Vec3 localPoint = toLocalPoint(pointInParentSpace);

        double localDistance = getLocalSDF(localPoint);

        Vec3 scale = transform.getScale();

        double minScale = Math.min(
                Math.abs(scale.x),
                Math.min(
                        Math.abs(scale.y),
                        Math.abs(scale.z)
                )
        );

        if (minScale < 1e-6) {
            return Double.MAX_VALUE;
        }

        return localDistance * minScale;
    }

    private Hit raymarchRay(Ray ray) {
        Vec3 currentPos = ray.origin;
        double totalDistance = 0.0;

        for (
                int step = 0;
                step < MAX_STEPS && totalDistance < MAX_DISTANCE;
                step++
        ) {
            double sdfValue = getSDF(currentPos);

            if (sdfValue < EPSILON) {
                Vec3 normal = calculateNormal(currentPos);

                return new Hit(
                        totalDistance,
                        currentPos,
                        normal,
                        this
                );
            }

            double stepSize = Math.max(
                    sdfValue * 0.8,
                    MIN_DISTANCE
            );

            currentPos = currentPos.add(
                    ray.direction.mul((float) stepSize)
            );

            totalDistance += stepSize;
        }

        return null;
    }

    /**
     * Die SDF wird im Parent-/World-Space abgetastet.
     * Deshalb ist die Normale anschließend ebenfalls bereits
     * im richtigen Koordinatensystem.
     */
    private Vec3 calculateNormal(Vec3 p) {
        float delta = (float) SDF_DELTA;

        double fx = getSDF(p.add(new Vec3(delta, 0, 0)))
                - getSDF(p.add(new Vec3(-delta, 0, 0)));

        double fy = getSDF(p.add(new Vec3(0, delta, 0)))
                - getSDF(p.add(new Vec3(0, -delta, 0)));

        double fz = getSDF(p.add(new Vec3(0, 0, delta)))
                - getSDF(p.add(new Vec3(0, 0, -delta)));

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

