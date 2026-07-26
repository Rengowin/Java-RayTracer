package BennysRayTrayer.objects.Normal.csg;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.HitRange;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Normal.AnalyticObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cut extends NormalCSG {

    public Cut(
            AnalyticObject a,
            AnalyticObject b,
            CSGMaterialBlendMode blendMode
    ) {
        super(a, b, blendMode);
    }

    public Cut(AnalyticObject a, AnalyticObject b) {
        this(a, b, CSGMaterialBlendMode.USE_A);
    }

    @Override
    protected double intersectLocalDistance(
            Ray localRay,
            double maxDistance
    ) {
        HitRange rangeA =
                a.intersectRange(
                        localRay,
                        maxDistance
                );

        HitRange rangeB =
                b.intersectRange(
                        localRay,
                        maxDistance
                );

        if (rangeA == null || rangeB == null) {
            return Double.POSITIVE_INFINITY;
        }

        double overlapStart = Math.max(
                rangeA.tEnter,
                rangeB.tEnter
        );

        double overlapEnd = Math.min(
                rangeA.tExit,
                rangeB.tExit
        );

        if (overlapStart >= overlapEnd - EPS) {
            return Double.POSITIVE_INFINITY;
        }

        if (overlapStart > EPS
                && overlapStart < maxDistance) {
            return overlapStart;
        }

        if (overlapEnd > EPS
                && overlapEnd < maxDistance) {
            return overlapEnd;
        }

        return Double.POSITIVE_INFINITY;
    }

    @Override
    public List<HitInterval> intersectLocalIntervals(
            Ray localRay
    ) {
        List<HitInterval> intervalsA =
                a.intersectIntervals(localRay);

        List<HitInterval> intervalsB =
                b.intersectIntervals(localRay);

        List<HitInterval> result = new ArrayList<>();

        if (intervalsA == null || intervalsA.isEmpty() || intervalsB == null || intervalsB.isEmpty()) {
            return result;
        }

        for (HitInterval ia : intervalsA) {
            for (HitInterval ib : intervalsB) {
                double overlapStart = Math.max(ia.tEnter, ib.tEnter);
                double overlapEnd = Math.min(ia.tExit, ib.tExit);

                if (overlapStart < overlapEnd - EPS) {
                    boolean enterFromA = ia.tEnter >= ib.tEnter;
                    boolean exitFromA  = ia.tExit <= ib.tExit;

                    Vec3 normalEnter = enterFromA ? ia.normalEnter : ib.normalEnter;
                    Vec3 normalExit  = exitFromA  ? ia.normalExit  : ib.normalExit;

                    HitInterval hi = new HitInterval(
                            overlapStart,
                            overlapEnd,
                            toWorldDirection(normalEnter).normalize(),
                            toWorldDirection(normalExit).normalize(),
                            this
                    );
                    hi.objectExit = this;
                    result.add(hi);
                }
            }
        }

        Collections.sort(result, (i1, i2) -> Double.compare(i1.tEnter, i2.tEnter));
        return result;
    }
}
