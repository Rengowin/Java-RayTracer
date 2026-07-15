package BennysRayTrayer.objects.Normal;

import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.Object3D;

import java.util.ArrayList;
import java.util.List;

public class HalfSpace extends Object3D {
    Vec3 normal;
    float distance;

    public HalfSpace(Vec3 normal, float distance, Color color){
        super(color);
        this.normal = normal;
        this.distance = distance;
    }

    public static HalfSpace xLess(float x, Color color) {
        return new HalfSpace(new Vec3(1, 0, 0), x, color);
    }

    public static HalfSpace xGreater(float x, Color color) {
        return new HalfSpace(new Vec3(-1, 0, 0), -x, color);
    }

    public static HalfSpace yLess(float y, Color color) {
        return new HalfSpace(new Vec3(0, 1, 0), y, color);
    }

    public static HalfSpace yGreater(float y, Color color) {
        return new HalfSpace(new Vec3(0, -1, 0), -y, color);
    }

    public static HalfSpace zLess(float z, Color color) {
        return new HalfSpace(new Vec3(0, 0, 1), z, color);
    }

    public static HalfSpace zGreater(float z, Color color) {
        return new HalfSpace(new Vec3(0, 0, -1), -z, color);
    }

    public static HalfSpace custom(Vec3 normal, float distance, Color color) {
        return new HalfSpace(normal.normalize(), distance, color);
    }

    public static HalfSpace withNormal(Vec3 normal, float distance, Color color) {
        return new HalfSpace(normal.normalize(), distance, color);
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
}
