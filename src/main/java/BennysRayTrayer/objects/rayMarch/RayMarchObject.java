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

    private Vec3 boundingCenter = null;
    private double boundingRadius = -1.0;

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

    public void setBoundingSphere(
            Vec3 center,
            double radius
    ) {
        this.boundingCenter = center;
        this.boundingRadius = radius;
    }

    public boolean hasBoundingSphere() {
        return boundingCenter != null
                && boundingRadius > 0.0;
    }

    protected abstract double getLocalSDF(Vec3 localPoint);

    public final double getSDF(Vec3 pointInParentSpace) {
        double distanceScale =
                transform.getMinAbsScale();

        if (distanceScale < 1e-6) {
            return Double.MAX_VALUE;
        }

        Vec3 localPoint =
                toLocalPoint(pointInParentSpace);

        double localDistance =
                getLocalSDF(localPoint);

        return localDistance * distanceScale;
    }

    private double raymarchDistance(
            Ray ray,
            double maxDistance
    ) {
        Vec3 currentPos = ray.origin;
        double totalDistance = 0.0;

        double distanceLimit = Math.min(
                maxDistance,
                MAX_DISTANCE
        );

        for (
                int step = 0;
                step < MAX_STEPS
                        && totalDistance < distanceLimit;
                step++
        ) {
            double sdfValue = getSDF(currentPos);

            if (sdfValue < EPSILON) {
                return totalDistance;
            }

            double stepSize = Math.max(
                    sdfValue * 0.8,
                    MIN_DISTANCE
            );

            totalDistance += stepSize;

            if (totalDistance >= distanceLimit) {
                return Double.POSITIVE_INFINITY;
            }

            currentPos = ray.origin.add(
                    ray.direction.mul((float) totalDistance)
            );
        }

        return Double.POSITIVE_INFINITY;
    }

    /**
     * Die SDF wird im Parent-/World-Space abgetastet.
     * Deshalb ist die Normale anschließend ebenfalls bereits
     * im richtigen Koordinatensystem.
     */
    private Vec3 calculateNormal(Vec3 p) {
        float delta = (float) SDF_DELTA;

        Vec3 e1 = new Vec3( 1.0f, -1.0f, -1.0f);
        Vec3 e2 = new Vec3(-1.0f, -1.0f,  1.0f);
        Vec3 e3 = new Vec3(-1.0f,  1.0f, -1.0f);
        Vec3 e4 = new Vec3( 1.0f,  1.0f,  1.0f);

        double d1 = getSDF(
                p.add(e1.mul(delta))
        );

        double d2 = getSDF(
                p.add(e2.mul(delta))
        );

        double d3 = getSDF(
                p.add(e3.mul(delta))
        );

        double d4 = getSDF(
                p.add(e4.mul(delta))
        );

        return e1.mul((float) d1)
                .add(e2.mul((float) d2))
                .add(e3.mul((float) d3))
                .add(e4.mul((float) d4))
                .normalize();
    }

    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        double distance = raymarchDistance(
                ray,
                MAX_DISTANCE
        );

        List<HitInterval> intervals = new ArrayList<>();

        if (!Double.isFinite(distance)
                || distance <= 0.0) {
            return intervals;
        }

        Hit hit = createHit(
                ray,
                distance
        );

        intervals.add(new HitInterval(
                hit.t,
                hit.t + 0.001,
                hit.normal,
                hit.normal.mul(-1.0f),
                this
        ));

        return intervals;
    }

    @Override
    public Hit createHit(
            Ray ray,
            double distance
    ) {
        if (!Double.isFinite(distance)
                || distance <= 0.0) {
            return null;
        }

        Vec3 position = ray.origin.add(
                ray.direction.mul((float) distance)
        );

        Vec3 normal = calculateNormal(position);

        return new Hit(
                distance,
                position,
                normal,
                this
        );
    }

    @Override
    public double intersectDistance(
            Ray ray,
            double maxDistance
    ) {
        if (hasBoundingSphere()
                && !intersectsBoundingSphere(
                ray,
                maxDistance
        )) {
            return Double.POSITIVE_INFINITY;
        }

        return raymarchDistance(
                ray,
                maxDistance
        );
    }

    private boolean intersectsBoundingSphere(
            Ray ray,
            double maxDistance
    ) {
        Vec3 centerWorld = toWorldPoint(
                boundingCenter
        );

        Vec3 scale = transform.getScale();

        double maxScale = Math.max(
                Math.abs(scale.x),
                Math.max(
                        Math.abs(scale.y),
                        Math.abs(scale.z)
                )
        );

        double radiusWorld =
                boundingRadius * maxScale;

        Vec3 originToCenter =
                ray.origin.sub(centerWorld);

        double b =
                originToCenter.dot(ray.direction);

        double c =
                originToCenter.dot(originToCenter)
                        - radiusWorld * radiusWorld;

        double discriminant =
                b * b - c;

        if (discriminant < 0.0) {
            return false;
        }

        double sqrtDiscriminant =
                Math.sqrt(discriminant);

        double tNear =
                -b - sqrtDiscriminant;

        double tFar =
                -b + sqrtDiscriminant;

        if (tFar <= 0.0) {
            return false;
        }

        return tNear < maxDistance;
    }

    public Vec3 getBoundingCenter() {
        return boundingCenter;
    }

    public double getBoundingRadius() {
        return boundingRadius;
    }

    public Vec3 getBoundingCenterInParentSpace() {
        if (!hasBoundingSphere()) {
            return null;
        }

        return toWorldPoint(
                boundingCenter
        );
    }

    public double getBoundingRadiusInParentSpace() {
        if (!hasBoundingSphere()) {
            return -1.0;
        }

        Vec3 scale = transform.getScale();

        double maxScale = Math.max(
                Math.abs(scale.x),
                Math.max(
                        Math.abs(scale.y),
                        Math.abs(scale.z)
                )
        );

        return boundingRadius * maxScale;
    }
}

