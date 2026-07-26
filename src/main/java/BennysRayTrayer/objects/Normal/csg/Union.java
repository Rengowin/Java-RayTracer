package BennysRayTrayer.objects.Normal.csg;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Normal.AnalyticObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Union extends NormalCSG {

    public Union(
            AnalyticObject a,
            AnalyticObject b,
            CSGMaterialBlendMode blendMode
    ) {
        super(a, b, blendMode);
    }

    public Union(
            AnalyticObject a,
            AnalyticObject b
    ) {
        super(a, b);
    }

    @Override
    protected double intersectLocalDistance(Ray localRay, double maxDistance) {
        return Math.min(
                a.intersectDistance(localRay, maxDistance),
                b.intersectDistance(localRay, maxDistance)
        );
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

        if (intervalsA != null) {
            for (HitInterval interval : intervalsA) {
                Vec3 worldNormalEnter = toWorldDirection(interval.normalEnter).normalize();
                Vec3 worldNormalExit = toWorldDirection(interval.normalExit).normalize();
                result.add(new HitInterval(interval.tEnter, interval.tExit, 
                        worldNormalEnter, worldNormalExit, interval.objectEnter));
            }
        }

        if (intervalsB != null) {
            for (HitInterval interval : intervalsB) {
                Vec3 worldNormalEnter = toWorldDirection(interval.normalEnter).normalize();
                Vec3 worldNormalExit = toWorldDirection(interval.normalExit).normalize();
                result.add(new HitInterval(interval.tEnter, interval.tExit, 
                        worldNormalEnter, worldNormalExit, interval.objectEnter));
            }
        }

        Collections.sort(result, (i1, i2) -> Double.compare(i1.tEnter, i2.tEnter));

        return result;
    }
}
