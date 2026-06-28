package BennysRayTrayer.objects.rayMarch;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.rendering.Material;

public class RayMarchSphere extends RayMarchObject {

    private double radius;

    public RayMarchSphere(double radius) {
        this.radius = radius;
    }

    public RayMarchSphere(Color color, double radius) {
        super(color);
        this.radius = radius;
    }

    public RayMarchSphere(double radius, Material material) {
        super(material);
        this.radius = radius;
    }

    public void setRadius(double radius) {
        this.radius = Math.max(0.01, radius);
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double getSDF(Vec3 point) {
        // SDF für eine Kugel: distance = ||point|| - radius
        return point.length() - radius;
    }
}
