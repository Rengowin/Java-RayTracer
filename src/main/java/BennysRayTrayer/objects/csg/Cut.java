package BennysRayTrayer.objects.csg;

import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Object3D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cut extends Object3D {

    Object3D a;
    Object3D b;

    public Cut(Object3D a, Object3D b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        final double EPS = 1e-6;

        Vec3 s = this.getTransform().getScale();

        Vec3 localOrigin = toLocalPoint(ray.origin);
        Vec3 localDir = toLocalDirection(ray.direction);

        Ray localRay = new Ray(localOrigin, localDir);

        List<HitInterval> intervalsA = a.intersectIntervals(localRay);
        List<HitInterval> intervalsB = b.intersectIntervals(localRay);

        List<HitInterval> result = new ArrayList<>();

        // Intersection: nur Teile von A, die INNERHALB von B liegen
        if (intervalsA == null || intervalsA.isEmpty() || intervalsB == null || intervalsB.isEmpty()) {
            return result;
        }

        for (HitInterval ia : intervalsA) {
            for (HitInterval ib : intervalsB) {
                // Überlappung finden
                double overlapStart = Math.max(ia.tEnter, ib.tEnter);
                double overlapEnd = Math.min(ia.tExit, ib.tExit);

                if (overlapStart < overlapEnd - EPS) {
                    boolean enterFromA = ia.tEnter >= ib.tEnter;
                    boolean exitFromA  = ia.tExit <= ib.tExit;

                    Vec3 normalEnter = enterFromA ? ia.normalEnter : ib.normalEnter;
                    Vec3 normalExit  = exitFromA  ? ia.normalExit  : ib.normalExit;

                    Object3D objectEnter = enterFromA ? ia.objectEnter : ib.objectEnter;
                    Object3D objectExit  = exitFromA  ? ia.objectExit  : ib.objectExit;

                    HitInterval hi = new HitInterval(
                            overlapStart,
                            overlapEnd,
                            toWorldDirection(normalEnter).normalize(),
                            toWorldDirection(normalExit).normalize(),
                            objectEnter
                    );
                    hi.objectExit = objectExit;

                    result.add(hi);
                }
            }
        }

        Collections.sort(result, (i1, i2) -> Double.compare(i1.tEnter, i2.tEnter));
        return result;
    }
}
