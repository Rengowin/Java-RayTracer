package DasVonCopieltWasAuchZuVielWar;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.rendering.Material;

import BennysRayTrayer.objects.rayMarch.RayMarchObject;


/**
 * SDFSphere: Einfache Kugel via Signed Distance Function
 * Analytische SDF für perfekte Sphären - schneller als Mandelbulb!
 */
public class SDFSphere extends RayMarchObject {

    private double radius = 1.0;

    public SDFSphere(Color color, Material material) {
        super(color, material);
    }

    public SDFSphere(Color color) {
        super(color);
    }

    public SDFSphere(Material material) {
        super(material);
    }

    public SDFSphere() {
        super();
    }

    public void setRadius(double radius) {
        this.radius = Math.max(0.01, radius);
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double getSDF(Vec3 p) {
        // PerfekteSphere: distance = ||p|| - radius
        return p.length() - radius;
    }
}

