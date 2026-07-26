package BennysRayTrayer.objects.Normal;

import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.HitRange;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.Object3D;

import java.util.ArrayList;
import java.util.List;

// wurde auch per ai fertiggestellt (Das intersectIntervals)

public class Quadric extends AnalyticObject {
    double a, b, c, d, e, f, g, h, i, j;

    public Quadric(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j) {
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
        this.setPosition(new Vec3(0f, 0f, 0f));
    }

    public static Quadric QuadricWithFormel(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j) {
        return new Quadric(
                a, b, c, d, e, f, g, h, i, j
        );
    }

    public static Quadric custom(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j) {
        return QuadricWithFormel(a,b,c,d,e,f,g,h,i,j);
    }

    public static Quadric sphere() {
        return new Quadric(
                1, 1, 1,
                0, 0, 0,
                0, 0, 0,
                -1
        );
    }

    public static Quadric cube() {
        return new Quadric(
                1, 1, 1,
                0, 0, 0,
                0, 0, 0,
                -1
        );
    }

    public static Quadric ellipsoide() {
        return sphere();
    }

    public static Quadric cylinderY() {
        return new Quadric(
                1, 0, 1,
                0, 0, 0,
                0, 0, 0,
                -1
        );
    }

    public static Quadric cylinderX() {
        return new Quadric(
                0, 1, 1,
                0, 0, 0,
                0, 0, 0,
                -1
        );
    }

    public static Quadric cylinderZ() {
        return new Quadric(
                1, 1, 0,
                0, 0, 0,
                0, 0, 0,
                -1
        );
    }

    public static Quadric coneY() {
        return new Quadric(
                1, -1, 1,
                0, 0, 0,
                0, 0, 0,
                0
        );
    }

    public static Quadric paraboloidY() {
        return new Quadric(
                1, 0, 1,
                0, 0, 0,
                0, 1, 0,
                0
        );
    }

    public static Quadric paraboloidX() {
        return new Quadric(
                0, 1, 1,
                0, 0, 0,
                1, 0, 0,
                0
        );
    }

    public static Quadric paraboloidZ() {
        return new Quadric(
                1, 1, 0,
                0, 0, 0,
                0, 0, 1,
                0
        );
    }

    public static Quadric hyperboloidY(Color color) {
        return new Quadric(
                1, -1, 1,
                0, 0, 0,
                0, 0, 0,
                -1
        );
    }

    public static Quadric hyperboloidX() {
        return new Quadric(
                0, 1, 1,
                0, 0, 0,
                0, 0, 0,
                -1
        );
    }

    public static Quadric hyperboloidZ() {
        return new Quadric(
                1, 1, 0,
                0, 0, 0,
                0, 0, 0,
                -1
        );
    }

    public static Quadric planeX() {
        return new Quadric(
                0, 0, 0,
                0, 0, 0,
                -1, 0, 0,
                0
        );
    }

    public static Quadric planeY(){
        return new Quadric(
                0, 0, 0,
                0, 0, 0,
                0, -1, 0,
                0
        );
    }

    public static Quadric planeZ() {
        return new Quadric(
                0, 0, 0,
                0, 0, 0,
                0, 0, -1,
                0
        );
    }


    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        final double EPS = 1e-6;

        Vec3 s = this.getTransform().getScale();

        Vec3 localOrigin = toLocalPoint(ray.origin);
        Vec3 localDir = toLocalDirection(ray.direction);

        if (Math.abs(s.x) < EPS || Math.abs(s.y) < EPS || Math.abs(s.z) < EPS) {
            return new ArrayList<>();
        }

        double ox = localOrigin.x;
        double oy = localOrigin.y;
        double oz = localOrigin.z;

        double dx = localDir.x;
        double dy = localDir.y;
        double dz = localDir.z;

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

            Vec3 hitPoint = toWorldPoint(new Vec3(
                    (float) (lx * s.x),
                    (float) (ly * s.y),
                    (float) (lz * s.z)
            ));

            double gradX = 2.0 * a * lx + d * ly + e * lz + g;
            double gradY = 2.0 * b * ly + d * lx + f * lz + h;
            double gradZ = 2.0 * c * lz + e * lx + f * ly + i;

            Vec3 normal = toWorldDirection(new Vec3(
                    (float) (gradX / s.x),
                    (float) (gradY / s.y),
                    (float) (gradZ / s.z)
            )).normalize();

            intervals.add(new HitInterval(t, t, normal, normal, this));
            return intervals;
        } else {
            double discriminant = B * B - 4.0 * A * C;
            if (discriminant < 0.0) {
                return intervals;
            }

            double sqrtD = Math.sqrt(discriminant);
            double t1 = (-B - sqrtD) / (2.0 * A);
            double t2 = (-B + sqrtD) / (2.0 * A);

            if (t1 < EPS && t2 < EPS) {
                return intervals;
            }

            double tEnter = Math.min(t1, t2);
            double tExit = Math.max(t1, t2);

            double lx_enter = ox + tEnter * dx;
            double ly_enter = oy + tEnter * dy;
            double lz_enter = oz + tEnter * dz;

            double lx_exit = ox + tExit * dx;
            double ly_exit = oy + tExit * dy;
            double lz_exit = oz + tExit * dz;

            Vec3 hitPointEnter = toWorldPoint(new Vec3(
                    (float) (lx_enter * s.x),
                    (float) (ly_enter * s.y),
                    (float) (lz_enter * s.z)
            ));

            Vec3 hitPointExit = toWorldPoint(new Vec3(
                    (float) (lx_exit * s.x),
                    (float) (ly_exit * s.y),
                    (float) (lz_exit * s.z)
            ));

            double gradX_enter = 2.0 * a * lx_enter + d * ly_enter + e * lz_enter + g;
            double gradY_enter = 2.0 * b * ly_enter + d * lx_enter + f * lz_enter + h;
            double gradZ_enter = 2.0 * c * lz_enter + e * lx_enter + f * ly_enter + i;

            double gradX_exit = 2.0 * a * lx_exit + d * ly_exit + e * lz_exit + g;
            double gradY_exit = 2.0 * b * ly_exit + d * lx_exit + f * lz_exit + h;
            double gradZ_exit = 2.0 * c * lz_exit + e * lx_exit + f * ly_exit + i;

            Vec3 normalEnter = toWorldDirection(new Vec3(
                    (float) (gradX_enter / s.x),
                    (float) (gradY_enter / s.y),
                    (float) (gradZ_enter / s.z)
            )).normalize();

            Vec3 normalExit = toWorldDirection(new Vec3(
                    (float) (gradX_exit / s.x),
                    (float) (gradY_exit / s.y),
                    (float) (gradZ_exit / s.z)
            )).normalize();

            intervals.add(new HitInterval(tEnter, tExit, normalEnter, normalExit, this));
            return intervals;
        }
    }

    @Override
    public HitRange intersectRange(
            Ray ray,
            double maxDistance
    ) {
        Vec3 scale = getTransform().getScale();

        if (Math.abs(scale.x) < EPS
                || Math.abs(scale.y) < EPS
                || Math.abs(scale.z) < EPS) {
            return null;
        }

        Vec3 localOrigin =
                toLocalPoint(ray.origin);

        Vec3 localDirection =
                toLocalDirection(ray.direction);

        double ox = localOrigin.x;
        double oy = localOrigin.y;
        double oz = localOrigin.z;

        double dx = localDirection.x;
        double dy = localDirection.y;
        double dz = localDirection.z;

        double equationA =
                a * dx * dx
                        + b * dy * dy
                        + c * dz * dz
                        + d * dx * dy
                        + e * dx * dz
                        + f * dy * dz;

        double equationB =
                2.0 * a * ox * dx
                        + 2.0 * b * oy * dy
                        + 2.0 * c * oz * dz
                        + d * (ox * dy + oy * dx)
                        + e * (ox * dz + oz * dx)
                        + f * (oy * dz + oz * dy)
                        + g * dx
                        + h * dy
                        + i * dz;

        double equationC =
                a * ox * ox
                        + b * oy * oy
                        + c * oz * oz
                        + d * ox * oy
                        + e * ox * oz
                        + f * oy * oz
                        + g * ox
                        + h * oy
                        + i * oz
                        + j;

        // Lineare Gleichung, z. B. Ebene
        if (Math.abs(equationA) < EPS) {
            if (Math.abs(equationB) < EPS) {
                return null;
            }

            double t =
                    -equationC / equationB;

            if (t <= EPS || t >= maxDistance) {
                return null;
            }

            return new HitRange(
                    t,
                    t
            );
        }

        double discriminant =
                equationB * equationB
                        - 4.0 * equationA * equationC;

        if (discriminant < 0.0) {
            return null;
        }

        double sqrtDiscriminant =
                Math.sqrt(discriminant);

        double t1 =
                (-equationB - sqrtDiscriminant)
                        / (2.0 * equationA);

        double t2 =
                (-equationB + sqrtDiscriminant)
                        / (2.0 * equationA);

        double tEnter = Math.min(t1, t2);
        double tExit = Math.max(t1, t2);

        if (tExit <= EPS) {
            return null;
        }

        if (tEnter >= maxDistance) {
            return null;
        }

        return new HitRange(
                tEnter,
                Math.min(tExit, maxDistance)
        );
    }

}
