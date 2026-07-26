package BennysRayTrayer.objects.Normal;

import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.HitRange;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.rendering.Material;


import java.util.List;

public abstract class AnalyticObject extends Object3D {

    protected static final double EPS = 1e-6;

    public AnalyticObject(Color color, Material material) {
        super(color, material);
    }

    public AnalyticObject(Color color) {
        super(color);
    }

    public AnalyticObject(Material material) {
        super(material);
    }

    public AnalyticObject() {
        super();
    }

    public HitRange intersectRange(
            Ray ray,
            double maxDistance
    ) {
        List<HitInterval> intervals =
                intersectIntervals(ray);

        if (intervals == null || intervals.isEmpty()) {
            return null;
        }

        for (HitInterval interval : intervals) {
            if (interval.tExit <= EPS) {
                continue;
            }

            if (interval.tEnter >= maxDistance) {
                continue;
            }

            return new HitRange(
                    interval.tEnter,
                    Math.min(
                            interval.tExit,
                            maxDistance
                    )
            );
        }

        return null;
    }
}
