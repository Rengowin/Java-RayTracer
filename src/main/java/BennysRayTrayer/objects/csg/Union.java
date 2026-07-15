package BennysRayTrayer.objects.csg;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.rendering.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Union extends Object3D {
    Object3D a;
    Object3D b;
    CSGMaterialBlendMode blendMode;

    public Union(Object3D a, Object3D b, CSGMaterialBlendMode blendMode) {
        this.a = a;
        this.b = b;
        this.blendMode = blendMode;

        apply();
    }

    public Union(Object3D a, Object3D b) {
        this(a, b, CSGMaterialBlendMode.USE_A);
    }

    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        final double EPS = 1e-6;

        Vec3 s = this.getTransform().getScale();

        Vec3 localOrigin = toLocalPoint(ray.origin);
        Vec3 localDir = toLocalDirection(ray.direction);

        Ray  localRay    = new Ray(localOrigin, localDir);

        List<HitInterval> intervalsA = a.intersectIntervals(localRay);
        List<HitInterval> intervalsB = b.intersectIntervals(localRay);

        List<HitInterval> result = new ArrayList<>();

        // Alle Intervalle sammeln und transformieren
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

        // Sortiere Intervalle nach tEnter
        Collections.sort(result, (i1, i2) -> Double.compare(i1.tEnter, i2.tEnter));

        return result;
    }

    private void apply() {
        switch (blendMode) {
            case USE_A -> {
                if (a.getMaterial() != null) {
                    setMaterial(a.getMaterial());
                }
                if (a.getColor() != null) {
                    setColor(a.getColor());
                }
            }
            case USE_B -> {
                if (b.getMaterial() != null) {
                    setMaterial(b.getMaterial());
                }
                if (b.getColor() != null) {
                    setColor(b.getColor());
                }
            }
            case BLEND -> {
                if (a.getMaterial() != null && b.getMaterial() != null) {
                    setMaterial(Material.blend(a.getMaterial(), b.getMaterial(), 0.5));
                } else if (a.getMaterial() != null) {
                    setMaterial(a.getMaterial());
                } else if (b.getMaterial() != null) {
                    setMaterial(b.getMaterial());
                }

                if (a.getColor() != null && b.getColor() != null) {
                    setColor(Color.blendColors(a.getColor().toVec3(), b.getColor().toVec3(), 0.5));
                } else if (a.getColor() != null) {
                    setColor(a.getColor());
                } else if (b.getColor() != null) {
                    setColor(b.getColor());
                }
            }
            default -> {
                // Default to USE_A if blendMode is not recognized
                if (a.getMaterial() != null) {
                    setMaterial(a.getMaterial());
                }
                if (a.getColor() != null) {
                    setColor(a.getColor());
                }
            }
        }
    }
}
