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

public class Diff extends NormalCSG {

    public Diff(AnalyticObject a, AnalyticObject b, CSGMaterialBlendMode blendMode) {
        super(a, b, blendMode);
    }

    public Diff(AnalyticObject a, AnalyticObject b) {
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

        if (rangeA == null) {
            return Double.POSITIVE_INFINITY;
        }

        HitRange rangeB =
                b.intersectRange(
                        localRay,
                        maxDistance
                );

        // B wird gar nicht getroffen:
        // A bleibt vollständig sichtbar
        if (rangeB == null) {
            return firstPositiveBoundary(
                    rangeA,
                    maxDistance
            );
        }

        /*
         * Fall 1:
         * A beginnt vor B.
         *
         * A:  [----]
         * B:     [--]
         *
         * Der erste sichtbare Punkt ist A-Eintritt.
         */
        if (rangeA.tEnter < rangeB.tEnter - EPS) {
            if (rangeA.tEnter > EPS
                    && rangeA.tEnter < maxDistance) {
                return rangeA.tEnter;
            }

            /*
             * Strahl startet schon in A, aber noch vor B.
             * Dann ist die erste relevante Oberfläche der Eintritt in B.
             */
            if (rangeB.tEnter > EPS
                    && rangeB.tEnter < rangeA.tExit
                    && rangeB.tEnter < maxDistance) {
                return rangeB.tEnter;
            }
        }

        /*
         * Fall 2:
         * A beginnt innerhalb von B oder gleichzeitig.
         *
         * Sichtbar wird A erst nach dem Austritt aus B.
         */
        double visibleStart = Math.max(
                rangeA.tEnter,
                rangeB.tExit
        );

        if (visibleStart > EPS
                && visibleStart < rangeA.tExit
                && visibleStart < maxDistance) {
            return visibleStart;
        }

        return Double.POSITIVE_INFINITY;
    }

    private double firstPositiveBoundary(
            HitRange range,
            double maxDistance
    ) {
        if (range.tEnter > EPS
                && range.tEnter < maxDistance) {
            return range.tEnter;
        }

        if (range.tExit > EPS
                && range.tExit < maxDistance) {
            return range.tExit;
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

        if (intervalsA == null || intervalsA.isEmpty()) {
            return result;
        }

        if (intervalsB == null || intervalsB.isEmpty()) {
            for (HitInterval ia : intervalsA) {
                Vec3 worldNormalEnter = toWorldDirection(ia.normalEnter).normalize();
                Vec3 worldNormalExit = toWorldDirection(ia.normalExit).normalize();

                HitInterval hi = new HitInterval(
                        ia.tEnter,
                        ia.tExit,
                        worldNormalEnter,
                        worldNormalExit,
                        this
                );
                hi.objectExit = this;
                result.add(hi);
            }
        } else {
            for (HitInterval ia : intervalsA) {
                double start = ia.tEnter;
                double end = ia.tExit;

                Vec3 startNormal = ia.normalEnter;

                for (HitInterval ib : intervalsB) {
                    if (start >= end - EPS) break;

                    if (ib.tExit <= start + EPS || ib.tEnter >= end - EPS) {
                        continue;
                    }

                    double overlapStart = Math.max(start, ib.tEnter);
                    double overlapEnd = Math.min(end, ib.tExit);

                    if (start < overlapStart - EPS) {
                        Vec3 normalEnter = toWorldDirection(startNormal).normalize();
                        Vec3 normalExit = toWorldDirection(ib.normalEnter.mul(-1)).normalize();

                        HitInterval hi = new HitInterval(
                                start,
                                overlapStart,
                                normalEnter,
                                normalExit,
                                this
                        );
                        hi.objectExit = this;
                        result.add(hi);
                    }

                    start = overlapEnd;
                    startNormal = ib.normalExit.mul(-1);
                }

                if (start < end - EPS) {
                    Vec3 normalEnter = toWorldDirection(startNormal).normalize();
                    Vec3 normalExit = toWorldDirection(ia.normalExit).normalize();

                    HitInterval hi = new HitInterval(
                            start,
                            end,
                            normalEnter,
                            normalExit,
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
