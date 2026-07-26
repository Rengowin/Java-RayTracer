package BennysRayTrayer.objects.Normal;

import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.HitRange;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.Object3D;

import java.util.ArrayList;
import java.util.List;

public class HalfSpace extends AnalyticObject {
    Vec3 normal;
    float distance;

    public HalfSpace(Vec3 normal, float distance) {
        this.normal = normal;
        this.distance = distance;
    }

    public static HalfSpace xLess(float x) {
        return new HalfSpace(new Vec3(1, 0, 0), x);
    }

    public static HalfSpace xGreater(float x) {
        return new HalfSpace(new Vec3(-1, 0, 0), -x);
    }

    public static HalfSpace yLess(float y) {
        return new HalfSpace(new Vec3(0, 1, 0), y);
    }

    public static HalfSpace yGreater(float y) {
        return new HalfSpace(new Vec3(0, -1, 0), -y);
    }

    public static HalfSpace zLess(float z) {
        return new HalfSpace(new Vec3(0, 0, 1), z);
    }

    public static HalfSpace zGreater(float z) {
        return new HalfSpace(new Vec3(0, 0, -1), -z);
    }

    public static HalfSpace custom(Vec3 normal, float distance) {
        return new HalfSpace(normal.normalize(), distance);
    }

    public static HalfSpace withNormal(Vec3 normal, float distance) {
        return new HalfSpace(normal.normalize(), distance);
    }


    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        final double EPS = 1e-6;
        List<HitInterval> result = new ArrayList<>();

        Vec3 localOrigin = toLocalPoint(ray.origin);
        Vec3 localDir = toLocalDirection(ray.direction).normalize();

        Vec3 localNormal = normal;
        float localDistance = distance;

        float denom = localNormal.dot(localDir);
        float numer = localDistance - localNormal.dot(localOrigin);

        if (Math.abs(denom) < EPS) {
            if (localNormal.dot(localOrigin) <= localDistance) {
                result.add(new HitInterval(
                        EPS,
                        Double.MAX_VALUE,
                        toWorldDirection(localNormal.mul(-1)).normalize(),
                        toWorldDirection(localNormal).normalize(),
                        this
                ));
            }
            return result;
        }

        double t = numer / denom;

        if (denom < 0) {
            result.add(new HitInterval(
                    Math.max(t, EPS),
                    Double.MAX_VALUE,
                    toWorldDirection(localNormal.mul(-1)).normalize(),
                    toWorldDirection(localNormal).normalize(),
                    this
            ));
        } else {
            if (t > EPS) {
                result.add(new HitInterval(
                        EPS,
                        t,
                        toWorldDirection(localNormal.mul(-1)).normalize(),
                        toWorldDirection(localNormal).normalize(),
                        this
                ));
            }
        }
        return result;
    }

    @Override
    public HitRange intersectRange(
            Ray ray,
            double maxDistance
    ) {
        Vec3 localOrigin =
                toLocalPoint(ray.origin);

        Vec3 localDirection =
                toLocalDirection(ray.direction)
                        .normalize();

        Vec3 localNormal = normal;
        double localDistance = distance;

        double originDistance =
                localNormal.dot(localOrigin);

        double denominator =
                localNormal.dot(localDirection);

        // Strahl parallel zur Ebene
        if (Math.abs(denominator) < EPS) {
            // Ursprung liegt innerhalb des Half-Space
            if (originDistance <= localDistance) {
                return new HitRange(
                        -Double.MAX_VALUE,
                        maxDistance
                );
            }

            return null;
        }

        double t =
                (localDistance - originDistance)
                        / denominator;

        if (denominator < 0.0) {
            // Strahl tritt in den Half-Space ein
            if (t >= maxDistance) {
                return null;
            }

            return new HitRange(
                    t,
                    maxDistance
            );
        }

        // Strahl verlässt den Half-Space
        if (t <= EPS) {
            return null;
        }

        return new HitRange(
                -Double.MAX_VALUE,
                Math.min(t, maxDistance)
        );
    }
}
