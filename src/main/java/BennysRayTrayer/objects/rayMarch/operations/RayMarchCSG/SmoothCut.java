package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public class SmoothCut extends RayMarchObject {
    private final RayMarchObject a;
    private final RayMarchObject b;
    private final double k;
    private final CSGMaterialBlendMode blendMode;

    public SmoothCut(RayMarchObject a, RayMarchObject b, double k, CSGMaterialBlendMode blendMode) {
        this.a = a;
        this.b = b;
        this.k = Math.max(1e-6, k);

        this.blendMode = blendMode;

        applyMaterialAndColor();
    }

    public SmoothCut(RayMarchObject a, RayMarchObject b, double k) {
        this(a, b, k, CSGMaterialBlendMode.USE_A);
    }

    public SmoothCut(RayMarchObject a, RayMarchObject b) {
        this(a, b, 1.0, CSGMaterialBlendMode.USE_A);
    }

    private void applyMaterialAndColor() {
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
            case NEAREST -> {
                // Nearest material and color based on SDF values
                double d1 = a.getSDF(a.getTransform().worldToLocalPoint(new Vec3(0, 0, 0)));
                double d2 = b.getSDF(b.getTransform().worldToLocalPoint(new Vec3(0, 0, 0)));

                if (d1 < d2) {
                    if (a.getMaterial() != null) {
                        setMaterial(a.getMaterial());
                    }
                    if (a.getColor() != null) {
                        setColor(a.getColor());
                    }
                } else {
                    if (b.getMaterial() != null) {
                        setMaterial(b.getMaterial());
                    }
                    if (b.getColor() != null) {
                        setColor(b.getColor());
                    }
                }
            }
            case HARD_NEAREST -> {
                double d1 = a.getSDF(a.getTransform().worldToLocalPoint(new Vec3(0, 0, 0)));
                double d2 = b.getSDF(b.getTransform().worldToLocalPoint(new Vec3(0, 0, 0)));

                if (Math.abs(d1) < Math.abs(d2)) {
                    if (a.getMaterial() != null) setMaterial(a.getMaterial());
                    if (a.getColor() != null) setColor(a.getColor());
                } else {
                    if (b.getMaterial() != null) setMaterial(b.getMaterial());
                    if (b.getColor() != null) setColor(b.getColor());
                }
            }
            case SMOOTH_BLEND -> {
                double d1 = a.getSDF(a.getTransform().worldToLocalPoint(new Vec3(0, 0, 0)));
                double d2 = b.getSDF(b.getTransform().worldToLocalPoint(new Vec3(0, 0, 0)));

                // Blendingfaktor basierend auf SDF-Differenz
                double diff = Math.abs(d1 - d2);
                double blendFactor = Math.max(0, 1.0 - (diff / k)); // k als Übergangszonen-Größe

                if (a.getMaterial() != null && b.getMaterial() != null) {
                    setMaterial(Material.blend(a.getMaterial(), b.getMaterial(), blendFactor));
                } else if (a.getMaterial() != null) {
                    setMaterial(a.getMaterial());
                } else if (b.getMaterial() != null) {
                    setMaterial(b.getMaterial());
                }

                if (a.getColor() != null && b.getColor() != null) {
                    setColor(Color.blendColors(a.getColor().toVec3(), b.getColor().toVec3(), blendFactor));
                } else if (a.getColor() != null) {
                    setColor(a.getColor());
                } else if (b.getColor() != null) {
                    setColor(b.getColor());
                }
            }
            default -> {
                // Default behavior: use material and color from object A
                if (a.getMaterial() != null) {
                    setMaterial(a.getMaterial());
                }
                if (a.getColor() != null) {
                    setColor(a.getColor());
                }
            }
        }
    }

    @Override
    public double getSDF(Vec3 point) {
        Vec3 pointInA = a.getTransform().worldToLocalPoint(point);
        Vec3 pointInB = b.getTransform().worldToLocalPoint(point);

        double d1 = a.getSDF(pointInA);
        double d2 = b.getSDF(pointInB);
        double h = Math.max(k - Math.abs(d1 - (-d2)), 0.0) / k;
        return Math.max(d1, -d2) - h * h * k * 0.25;
    }
}
