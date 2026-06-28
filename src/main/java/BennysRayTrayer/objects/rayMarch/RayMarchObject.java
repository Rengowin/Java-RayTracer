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

    // Raymarching-Parameter
    protected static final double MAX_DISTANCE = 100.0;      // Maximale Marchierungsdistanz
    protected static final double MIN_DISTANCE = 0.001;      // Minimales Sicherheitsabstand
    protected static final int MAX_STEPS = 256;              // Maximale Marchierungsschritte
    protected static final double EPSILON = 1e-4;            // Toleranz für Oberflächenerkennung

    // Für Normale-Berechnung via Finite Differences
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

    /*

     */
    public abstract double getSDF(Vec3 point);

    private Hit raymarchRay(Ray ray) {

        Vec3 currentPos = ray.origin;
        double totalDistance = 0.0;
        int steps = 0;

        while (steps < MAX_STEPS && totalDistance < MAX_DISTANCE) {
            Vec3 localPos = toLocalPoint(currentPos);
            double sdfValue = getSDF(localPos);

            if(sdfValue < EPSILON){
                Vec3 normal = calculateNormal(localPos);
                normal = toWorldDirection(normal);

                return new Hit(
                        totalDistance,
                        currentPos,
                        normal,
                        this
                );
            }

            //double stepSize = Math.max(sdfValue * 0.8, MIN_DISTANCE);

            double stepSize = sdfValue;

            currentPos = currentPos.add(ray.direction.mul((float) stepSize));
            totalDistance += stepSize;
            steps++;
        }

        return null;
    }

    private Vec3 calculateNormal(Vec3 p) {
        double fx = getSDF(p.add(new Vec3((float) SDF_DELTA, 0, 0)))
                - getSDF(p.add(new Vec3((float) -SDF_DELTA, 0, 0)));
        double fy = getSDF(p.add(new Vec3(0, (float) SDF_DELTA, 0)))
                - getSDF(p.add(new Vec3(0, (float) -SDF_DELTA, 0)));
        double fz = getSDF(p.add(new Vec3(0, 0, (float) SDF_DELTA)))
                - getSDF(p.add(new Vec3(0, 0, (float) -SDF_DELTA)));
        Vec3 normal = new Vec3((float) fx, (float) fy, (float) fz).normalize();
        return normal;
    }

    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {

        Hit hit = raymarchRay(ray);

        List<HitInterval> intervals = new ArrayList<>();

        if(hit != null && hit.t > 0) {
            HitInterval interval = new HitInterval(
                    hit.t,
                    hit.t+0.001,
                    hit.normal,
                    hit.normal.mul(-1),
                    this
            );
            intervals.add(interval);
        }

        return intervals;
    }
}
