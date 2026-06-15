package BennysRayTrayer.objects;

import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Sphere extends Object3D {

    double radius;

    public Sphere(Vec3 center, double radius, Vec3 color) {
        super(color);
        this.setPosition(center);
        this.radius = radius;
    }

    public Vec3 getPostion() {
        return this.getTransform().getPosition();
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        final double EPS = 1e-6;

        Vec3 p = this.getTransform().getPosition();
        Vec3 s = this.getTransform().getScale();

        if (Math.abs(s.x) < EPS || Math.abs(s.y) < EPS || Math.abs(s.z) < EPS) {
            return new ArrayList<>();
        }

        Vec3 localOrigin = inverseRotate(ray.origin.sub(p));
        Vec3 localDir = inverseRotate(ray.direction);

        double ox = localOrigin.x / s.x;
        double oy = localOrigin.y / s.y;
        double oz = localOrigin.z / s.z;

        double dx = localDir.x / s.x;
        double dy = localDir.y / s.y;
        double dz = localDir.z / s.z;

        double A = dx * dx + dy * dy + dz * dz;
        double B = 2.0 * (ox * dx + oy * dy + oz * dz);
        double C = ox * ox + oy * oy + oz * oz - radius * radius;

        double discriminant = B * B - 4.0 * A * C;
        if (discriminant < 0.0) {
            return new ArrayList<>();
        }

        double sqrtD = Math.sqrt(discriminant);
        double t1 = (-B - sqrtD) / (2.0 * A);
        double t2 = (-B + sqrtD) / (2.0 * A);

        // Beide Lösungen müssen positiv sein für ein gültiges Intervall
        if (t1 < EPS && t2 < EPS) {
            return new ArrayList<>();
        }

        // Falls t1 negativ ist, nur t2 betrachten
        if (t1 < EPS) {
            t1 = t2;
        }

        double tEnter = Math.min(t1, t2);
        double tExit = Math.max(t1, t2);

        // Positionen berechnen
        double lx_enter = ox + tEnter * dx;
        double ly_enter = oy + tEnter * dy;
        double lz_enter = oz + tEnter * dz;

        double lx_exit = ox + tExit * dx;
        double ly_exit = oy + tExit * dy;
        double lz_exit = oz + tExit * dz;

        Vec3 localHitPointEnter = new Vec3((float) lx_enter, (float) ly_enter, (float) lz_enter);
        Vec3 localHitPointExit = new Vec3((float) lx_exit, (float) ly_exit, (float) lz_exit);

        Vec3 hitPointEnter = rotate(new Vec3(
                (float) (lx_enter * s.x),
                (float) (ly_enter * s.y),
                (float) (lz_enter * s.z)
        )).add(p);

        Vec3 hitPointExit = rotate(new Vec3(
                (float) (lx_exit * s.x),
                (float) (ly_exit * s.y),
                (float) (lz_exit * s.z)
        )).add(p);

        Vec3 normalEnter = rotate(new Vec3(
                (float) (localHitPointEnter.x / s.x),
                (float) (localHitPointEnter.y / s.y),
                (float) (localHitPointEnter.z / s.z)
        )).normalize();

        Vec3 normalExit = rotate(new Vec3(
                (float) (localHitPointExit.x / s.x),
                (float) (localHitPointExit.y / s.y),
                (float) (localHitPointExit.z / s.z)
        )).normalize();

        List<HitInterval> intervals = new ArrayList<>();
        intervals.add(new HitInterval(tEnter, tExit, normalEnter, normalExit, this));
        return intervals;
    }

}
