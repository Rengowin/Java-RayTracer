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


    // vlt nochmal selbst probieren
    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        final double EPS = 1e-6;
        List<HitInterval> result = new ArrayList<>();

        float denom = normal.dot(ray.direction);
        float numer = distance - normal.dot(ray.origin);

        // Ray parallel zur Ebene
        if (Math.abs(denom) < EPS) {
            if (normal.dot(ray.origin) <= distance) {
                // komplett im Halbraum
                result.add(new HitInterval(
                        EPS,
                        Double.MAX_VALUE,
                        normal.mul(-1),
                        normal,
                        this
                ));
            }
            return result;
        }

        double t = numer / denom;

        if (denom < 0) {
            // Ray geht in den Halbraum hinein
            result.add(new HitInterval(
                    Math.max(t, EPS),
                    Double.MAX_VALUE,
                    normal.mul(-1),
                    normal,
                    this
            ));
        } else {
            // Ray verlässt den Halbraum
            if (t > EPS) {
                result.add(new HitInterval(
                        EPS,
                        t,
                        normal.mul(-1),
                        normal,
                        this
                ));
            }
        }
        return result;
    }
}
