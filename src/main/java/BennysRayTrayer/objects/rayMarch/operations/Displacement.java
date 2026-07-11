package BennysRayTrayer.objects.rayMarch.operations;

import BennysRayTrayer.core.Vec3;

public interface Displacement {
    double apply(Vec3 p, double baseSDF);
}
