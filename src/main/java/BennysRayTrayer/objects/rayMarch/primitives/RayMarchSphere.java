package BennysRayTrayer.objects.rayMarch.primitives;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.objects.rayMarch.operations.Displacement;
import BennysRayTrayer.rendering.Material;

public class RayMarchSphere extends RayMarchObject {

    private double radius;
    private Displacement displacement;


    public RayMarchSphere(double radius) {
        this.radius = radius;

        setBoundingSphere(
                new Vec3(0, 0, 0),
                this.radius
        );
    }

    public RayMarchSphere(Color color, double radius) {
        super(color);
        this.radius = radius;

        setBoundingSphere(
                new Vec3(0, 0, 0),
                this.radius
        );
    }

    public RayMarchSphere(double radius, Material material) {
        super(material);
        this.radius = radius;

        setBoundingSphere(
                new Vec3(0, 0, 0),
                this.radius
        );
    }

    public RayMarchSphere(double radius, Displacement displacement) {
        this(radius);
        this.displacement = displacement;

        setBoundingSphere(
                new Vec3(0, 0, 0),
                this.radius
        );
    }

    public RayMarchSphere(Color color, double radius, Displacement displacement) {
        this(color, radius);
        this.displacement = displacement;

        setBoundingSphere(
                new Vec3(0, 0, 0),
                this.radius
        );
    }

    public RayMarchSphere(double radius, Material material, Displacement displacement) {
        this(radius, material);
        this.displacement = displacement;

        setBoundingSphere(
                new Vec3(0, 0, 0),
                this.radius
        );
    }

    public void setRadius(double radius) {
        this.radius = Math.max(0.01, radius);
    }

    public double getRadius() {
        return radius;
    }

    public Displacement getDisplacement() {
        return displacement;
    }

    public void setDisplacement(Displacement displacement) {
        this.displacement = displacement;
    }

    @Override
    public double getLocalSDF(Vec3 point) {
        double baseSdf = point.length() - radius;

        if (displacement != null) {
            return displacement.apply(point, baseSdf);
        }

        return baseSdf;
    }
}
