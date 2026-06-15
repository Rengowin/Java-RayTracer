package BennysRayTrayer.objects;

import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;

import java.util.ArrayList;
import java.util.List;

// wurde auch per ai fertiggestellt (Das intersectIntervals)

public class Quadric extends Object3D {
    double a, b, c, d, e, f, g, h, i, j;

    public Quadric(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, Vec3 position, Vec3 color) {
        super(color);
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
        this.i = i;
        this.j = j;
        this.setPosition(position);
    }

    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        final double EPS = 1e-6;

        Vec3 p = this.getTransform().getPosition();
        Vec3 s = this.getTransform().getScale();

        if (Math.abs(s.x) < EPS || Math.abs(s.y) < EPS || Math.abs(s.z) < EPS) {
            return new ArrayList<>();
        }

        Vec3 localOriginRot = inverseRotate(ray.origin.sub(p));
        Vec3 localDirRot = inverseRotate(ray.direction);

        double ox = localOriginRot.x / s.x;
        double oy = localOriginRot.y / s.y;
        double oz = localOriginRot.z / s.z;

        double dx = localDirRot.x / s.x;
        double dy = localDirRot.y / s.y;
        double dz = localDirRot.z / s.z;

        double A = a * dx * dx + b * dy * dy + c * dz * dz
                + d * dx * dy + e * dx * dz + f * dy * dz;

        double B = 2.0 * a * ox * dx + 2.0 * b * oy * dy + 2.0 * c * oz * dz
                + d * (ox * dy + oy * dx)
                + e * (ox * dz + oz * dx)
                + f * (oy * dz + oz * dy)
                + g * dx + h * dy + i * dz;

        double C = a * ox * ox + b * oy * oy + c * oz * oz
                + d * ox * oy + e * ox * oz + f * oy * oz
                + g * ox + h * oy + i * oz + j;

        List<HitInterval> intervals = new ArrayList<>();

        if (Math.abs(A) < EPS) {
            // Lineare Gleichung: nur ein Hit
            if (Math.abs(B) < EPS) {
                return intervals;
            }
            double t = -C / B;
            if (t < EPS) {
                return intervals;
            }

            double lx = ox + t * dx;
            double ly = oy + t * dy;
            double lz = oz + t * dz;

            Vec3 hitPoint = rotate(new Vec3(
                    (float) (lx * s.x),
                    (float) (ly * s.y),
                    (float) (lz * s.z)
            )).add(p);

            double gradX = 2.0 * a * lx + d * ly + e * lz + g;
            double gradY = 2.0 * b * ly + d * lx + f * lz + h;
            double gradZ = 2.0 * c * lz + e * lx + f * ly + i;

            Vec3 normal = rotate(new Vec3(
                    (float) (gradX / s.x),
                    (float) (gradY / s.y),
                    (float) (gradZ / s.z)
            )).normalize();

            // Degeneriertes Intervall (nur ein punkt)
            intervals.add(new HitInterval(t, t, normal, normal, this));
            return intervals;
        } else {
            // Quadratische Gleichung: zwei Hits
            double discriminant = B * B - 4.0 * A * C;
            if (discriminant < 0.0) {
                return intervals;
            }

            double sqrtD = Math.sqrt(discriminant);
            double t1 = (-B - sqrtD) / (2.0 * A);
            double t2 = (-B + sqrtD) / (2.0 * A);

            // Beide Lösungen müssen positiv sein
            if (t1 < EPS && t2 < EPS) {
                return intervals;
            }

            // Falls nur t1 positiv ist
            if (t1 < EPS) {
                t1 = t2;
            }

            double tEnter = Math.min(t1, t2);
            double tExit = Math.max(t1, t2);

            // Normalen für beide Punkte berechnen
            double lx_enter = ox + tEnter * dx;
            double ly_enter = oy + tEnter * dy;
            double lz_enter = oz + tEnter * dz;

            double lx_exit = ox + tExit * dx;
            double ly_exit = oy + tExit * dy;
            double lz_exit = oz + tExit * dz;

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

            double gradX_enter = 2.0 * a * lx_enter + d * ly_enter + e * lz_enter + g;
            double gradY_enter = 2.0 * b * ly_enter + d * lx_enter + f * lz_enter + h;
            double gradZ_enter = 2.0 * c * lz_enter + e * lx_enter + f * ly_enter + i;

            double gradX_exit = 2.0 * a * lx_exit + d * ly_exit + e * lz_exit + g;
            double gradY_exit = 2.0 * b * ly_exit + d * lx_exit + f * lz_exit + h;
            double gradZ_exit = 2.0 * c * lz_exit + e * lx_exit + f * ly_exit + i;

            Vec3 normalEnter = rotate(new Vec3(
                    (float) (gradX_enter / s.x),
                    (float) (gradY_enter / s.y),
                    (float) (gradZ_enter / s.z)
            )).normalize();

            Vec3 normalExit = rotate(new Vec3(
                    (float) (gradX_exit / s.x),
                    (float) (gradY_exit / s.y),
                    (float) (gradZ_exit / s.z)
            )).normalize();

            intervals.add(new HitInterval(tEnter, tExit, normalEnter, normalExit, this));
            return intervals;
        }
    }

}
