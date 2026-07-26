package BennysRayTrayer.objects.Normal.csg;

import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.objects.Normal.AnalyticObject;
import BennysRayTrayer.objects.Object3D;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.rendering.Material;

import java.util.List;

public abstract class NormalCSG extends AnalyticObject {

    protected final AnalyticObject a;
    protected final AnalyticObject b;
    protected final CSGMaterialBlendMode blendMode;

    protected static final double EPS = 1e-6;

    protected NormalCSG(AnalyticObject a, AnalyticObject b, CSGMaterialBlendMode blendMode) {
        this.a = a;
        this.b = b;
        this.blendMode = blendMode;

        applyDefaultMaterialAndColor();
        apply();
    }

    protected NormalCSG(
            AnalyticObject a,
            AnalyticObject b
    ) {
        this(
                a,
                b,
                CSGMaterialBlendMode.USE_A
        );
    }

    protected abstract List<HitInterval> intersectLocalIntervals(
            Ray localRay
    );

    protected abstract double intersectLocalDistance(
            Ray localRay,
            double maxDistance
    );

    @Override
    public final List<HitInterval> intersectIntervals(
            Ray ray
    ) {
        Ray localRay = toLocalRay(ray);

        return intersectLocalIntervals(localRay);
    }

    protected Ray toLocalRay(Ray ray) {
        Vec3 localOrigin =
                toLocalPoint(ray.origin);

        Vec3 localDirection =
                toLocalDirection(ray.direction);

        return new Ray(
                localOrigin,
                localDirection
        );
    }

    private void applyDefaultMaterialAndColor() {
        switch (blendMode) {
            case USE_B -> copyMaterialAndColorFrom(b);

            case BLEND, SMOOTH_BLEND -> {
                setMaterial(
                        blendMaterials(
                                a.getMaterial(),
                                b.getMaterial()
                        )
                );

                setColor(
                        blendColors(
                                a.getColor(),
                                b.getColor()
                        )
                );
            }

            default -> copyMaterialAndColorFrom(a);
        }
    }

    private void copyMaterialAndColorFrom(
            Object3D source
    ) {
        if (source.getMaterial() != null) {
            setMaterial(source.getMaterial());
        }

        if (source.getColor() != null) {
            setColor(source.getColor());
        }
    }

    private Material blendMaterials(
            Material first,
            Material second
    ) {
        if (first != null && second != null) {
            return Material.blend(
                    first,
                    second,
                    0.5
            );
        }

        return first != null
                ? first
                : second;
    }

    private Color blendColors(
            Color first,
            Color second
    ) {
        if (first != null && second != null) {
            return Color.blendColors(
                    first.getColor(),
                    second.getColor(),
                    0.5
            );
        }

        return first != null
                ? first
                : second;
    }

    @Override
    public double intersectDistance(
            Ray ray,
            double maxDistance
    ) {
        return intersectLocalDistance(
                toLocalRay(ray),
                maxDistance
        );
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
