package BennysRayTrayer.objects;

import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DIff extends Object3D {
    Object3D a;
    Object3D b;

    public DIff(Object3D a, Object3D b) {
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

        if (intervalsA == null || intervalsA.isEmpty()) {
            return result;
        }

        // A minus B: Teile von A, die NICHT in B liegen
        if (intervalsB == null || intervalsB.isEmpty()) {
            // B ist leer, also A ungekürzt
            for (HitInterval ia : intervalsA) {
                Vec3 worldNormalEnter = rotate(ia.normalEnter).normalize();
                Vec3 worldNormalExit = rotate(ia.normalExit).normalize();
                result.add(new HitInterval(ia.tEnter, ia.tExit, 
                        worldNormalEnter, worldNormalExit, ia.objectEnter));
            }
        } else {
            // B ist nicht leer: schneide Teile von A weg, die in B sind
            for (HitInterval ia : intervalsA) {
                double currentStart = ia.tEnter;
                double currentExit = ia.tExit;

                for (HitInterval ib : intervalsB) {
                    if (currentStart >= currentExit - EPS) {
                        break; // Kein Interval mehr übrig
                    }

                    // Wenn ib vor currentStart endet oder nach currentExit beginnt: ignorieren
                    if (ib.tExit <= currentStart + EPS || ib.tEnter >= currentExit - EPS) {
                        continue;
                    }

                    // Überlappung mit B
                    double overlapStart = Math.max(currentStart, ib.tEnter);
                    double overlapEnd = Math.min(currentExit, ib.tExit);

                    // Teil VOR der Überlappung
                    if (currentStart < overlapStart - EPS) {
                        Vec3 worldNormalEnter = rotate(ia.normalEnter).normalize();
                        Vec3 worldNormalExit = rotate(ia.normalExit).normalize();
                        result.add(new HitInterval(currentStart, overlapStart, 
                                worldNormalEnter, worldNormalExit, ia.objectEnter));
                    }

                    currentStart = overlapEnd;
                }

                // Verbleibendes Teil NACH allen B-Intervallen
                if (currentStart < currentExit - EPS) {
                    Vec3 worldNormalEnter = rotate(ia.normalEnter).normalize();
                    Vec3 worldNormalExit = rotate(ia.normalExit).normalize();
                    result.add(new HitInterval(currentStart, currentExit, 
                            worldNormalEnter, worldNormalExit, ia.objectEnter));
                }
            }
        }

        Collections.sort(result, (i1, i2) -> Double.compare(i1.tEnter, i2.tEnter));
        return result;
    }
}
