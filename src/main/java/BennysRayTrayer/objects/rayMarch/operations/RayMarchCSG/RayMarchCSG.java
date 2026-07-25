package BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG;

import BennysRayTrayer.CSGMaterialBlendMode;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.rendering.Material;

public abstract class RayMarchCSG extends RayMarchObject {

    protected final RayMarchObject a;
    protected final RayMarchObject b;
    protected final CSGMaterialBlendMode blendMode;

    protected RayMarchCSG(
            RayMarchObject a,
            RayMarchObject b,
            CSGMaterialBlendMode blendMode,
            BoundingMode boundingMode
    ) {
        this.a = a;
        this.b = b;
        this.blendMode = blendMode;

        applyDefaultMaterialAndColor();
        applyBoundingMode(boundingMode);
    }

    protected double distanceA(Vec3 point) {
        return a.getSDF(point);
    }

    protected double distanceB(Vec3 point) {
        return b.getSDF(point);
    }

    private void applyBoundingMode(
            BoundingMode boundingMode
    ) {
        switch (boundingMode) {
            case UNION -> useCombinedBoundingSphere();
            case INTERSECTION -> useSmallerBoundingSphere();
            case USE_A -> useBoundingSphereOfA();
        }
    }

    protected void applyDefaultMaterialAndColor() {
        switch (blendMode) {
            case USE_B -> copyMaterialAndColorFrom(b);

            case BLEND, SMOOTH_BLEND -> {
                setMaterial(blendMaterials(a.getMaterial(), b.getMaterial(), 0.5));
                setColor(blendColors(a.getColor(), b.getColor(), 0.5));
            }

            default -> copyMaterialAndColorFrom(a);
        }
    }

    protected void copyMaterialAndColorFrom(RayMarchObject source) {
        if (source.getMaterial() != null) {
            setMaterial(source.getMaterial());
        }

        if (source.getColor() != null) {
            setColor(source.getColor());
        }
    }

    protected Material blendMaterials(Material first, Material second, double factor) {
        if (first != null && second != null) {
            return Material.blend(first, second, factor);
        }

        return first != null ? first : second;
    }

    protected Color blendColors(Color first, Color second, double factor) {
        if (first != null && second != null) {
            return Color.blendColors(
                    first.getColor(),
                    second.getColor(),
                    factor
            );
        }

        return first != null ? first : second;
    }

    @Override
    public Material getMaterialAt(Vec3 worldPoint) {
        Vec3 point = toLocalPoint(worldPoint);

        return switch (blendMode) {
            case USE_A -> a.getMaterialAt(point);

            case USE_B -> b.getMaterialAt(point);

            case BLEND -> blendMaterials(
                    a.getMaterialAt(point),
                    b.getMaterialAt(point),
                    0.5
            );

            case NEAREST, HARD_NEAREST -> {
                double d1 = Math.abs(distanceA(point));
                double d2 = Math.abs(distanceB(point));

                yield d1 <= d2
                        ? a.getMaterialAt(point)
                        : b.getMaterialAt(point);
            }

            case PRESERVE_MATERIALS -> {
                double d1 = distanceA(point);
                double d2 = distanceB(point);

                yield d1 <= d2
                        ? a.getMaterialAt(point)
                        : b.getMaterialAt(point);
            }

            case SMOOTH_BLEND -> {
                double factor = calculateMaterialBlendFactor(point);

                yield blendMaterials(
                        a.getMaterialAt(point),
                        b.getMaterialAt(point),
                        factor
                );
            }
        };
    }

    protected double calculateMaterialBlendFactor(Vec3 point) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        return Math.abs(d1) <= Math.abs(d2) ? 0.0 : 1.0;
    }

    protected RayMarchObject getSurfaceObject(Vec3 point) {
        double d1 = distanceA(point);
        double d2 = distanceB(point);

        return d1 <= d2 ? a : b;
    }

    protected void useBoundingSphereOfA() {
        if (!a.hasBoundingSphere()) {
            return;
        }

        setBoundingSphere(
                a.getBoundingCenterInParentSpace(),
                a.getBoundingRadiusInParentSpace()
        );
    }

    protected void useCombinedBoundingSphere() {
        if (!a.hasBoundingSphere()
                || !b.hasBoundingSphere()) {
            return;
        }

        Vec3 centerA =
                a.getBoundingCenterInParentSpace();

        Vec3 centerB =
                b.getBoundingCenterInParentSpace();

        double radiusA =
                a.getBoundingRadiusInParentSpace();

        double radiusB =
                b.getBoundingRadiusInParentSpace();

        Vec3 difference = centerB.sub(centerA);
        double centerDistance = difference.length();

        if (radiusA >= centerDistance + radiusB) {
            setBoundingSphere(centerA, radiusA);
            return;
        }

        if (radiusB >= centerDistance + radiusA) {
            setBoundingSphere(centerB, radiusB);
            return;
        }

        if (centerDistance < 1e-9) {
            setBoundingSphere(
                    centerA,
                    Math.max(radiusA, radiusB)
            );
            return;
        }

        double combinedRadius =
                (centerDistance + radiusA + radiusB) * 0.5;

        double shiftFromA =
                combinedRadius - radiusA;

        Vec3 combinedCenter = centerA.add(
                difference.mul(
                        (float) (shiftFromA / centerDistance)
                )
        );

        setBoundingSphere(
                combinedCenter,
                combinedRadius
        );
    }

    protected void useSmallerBoundingSphere() {
        boolean hasA = a.hasBoundingSphere();
        boolean hasB = b.hasBoundingSphere();

        if (!hasA && !hasB) {
            return;
        }

        if (hasA && !hasB) {
            setBoundingSphere(
                    a.getBoundingCenterInParentSpace(),
                    a.getBoundingRadiusInParentSpace()
            );
            return;
        }

        if (!hasA) {
            setBoundingSphere(
                    b.getBoundingCenterInParentSpace(),
                    b.getBoundingRadiusInParentSpace()
            );
            return;
        }

        double radiusA =
                a.getBoundingRadiusInParentSpace();

        double radiusB =
                b.getBoundingRadiusInParentSpace();

        if (radiusA <= radiusB) {
            setBoundingSphere(
                    a.getBoundingCenterInParentSpace(),
                    radiusA
            );
        } else {
            setBoundingSphere(
                    b.getBoundingCenterInParentSpace(),
                    radiusB
            );
        }
    }
}
