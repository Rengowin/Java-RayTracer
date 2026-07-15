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

public class Diff extends Object3D {
    Object3D a;
    Object3D b;
    CSGMaterialBlendMode blendMode;

    public Diff(Object3D a, Object3D b, CSGMaterialBlendMode blendMode) {
        this.a = a;
        this.b = b;
        this.blendMode = blendMode;
        apply();
    }

    public Diff(Object3D a, Object3D b) {
        this(a, b, CSGMaterialBlendMode.USE_A);
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

        if (intervalsA == null || intervalsA.isEmpty()) {
            return result;
        }

        // A minus B: Teile von A, die NICHT in B liegen
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
                Object3D startObject = ia.objectEnter;

                for (HitInterval ib : intervalsB) {
                    if (start >= end - EPS) break;

                    if (ib.tExit <= start + EPS || ib.tEnter >= end - EPS) {
                        continue;
                    }

                    double overlapStart = Math.max(start, ib.tEnter);
                    double overlapEnd = Math.min(end, ib.tExit);

                    // Stück vor B bleibt sichtbar: endet an B-Eintritt -> B-Normale invertieren
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

                    // Nach B beginnt sichtbar wieder an B-Austritt -> B-Normale invertieren
                    start = overlapEnd;
                    startNormal = ib.normalExit.mul(-1);
                    startObject = ib.objectExit;
                }

                // Rest nach allen B-Intervallen: beginnt evtl. an B-Austritt, endet an A-Austritt
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
