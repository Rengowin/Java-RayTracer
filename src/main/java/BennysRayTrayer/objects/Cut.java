package BennysRayTrayer.objects;

import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cut extends Object3D{

    Object3D a;
    Object3D b;

    public Cut(Object3D a, Object3D b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        final double EPS = 1e-6;

        Vec3 p = this.getTransform().getPosition();

        Vec3 localOrigin = inverseRotate(ray.origin.sub(p));
        Vec3 localDir = inverseRotate(ray.direction);
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
                    // Es gibt eine Überlappung
                    Vec3 normalEnter = (ia.tEnter >= ib.tEnter) ? ia.normalEnter : ib.normalEnter;
                    Vec3 normalExit = (ia.tExit <= ib.tExit) ? ia.normalExit : ib.normalExit;
                    
                    Vec3 worldNormalEnter = rotate(normalEnter).normalize();
                    Vec3 worldNormalExit = rotate(normalExit).normalize();

                    result.add(new HitInterval(overlapStart, overlapEnd, 
                            worldNormalEnter, worldNormalExit, ia.objectEnter));
                }
            }
        }

        Collections.sort(result, (i1, i2) -> Double.compare(i1.tEnter, i2.tEnter));
        return result;
    }
}
