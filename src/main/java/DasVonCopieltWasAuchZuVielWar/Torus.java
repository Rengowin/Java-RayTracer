package DasVonCopieltWasAuchZuVielWar;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.rendering.Material;

import BennysRayTrayer.objects.rayMarch.RayMarchObject;

/**
 * Torus: Einfache analytische SDF für einen geometrischen Torus
 * Schnell und stabil, ideal zum Testen des Raymarching-Systems
 */
public class Torus extends RayMarchObject {

    private double majorRadius = 1.0;  // Großer Radius (Mittellinie)
    private double minorRadius = 0.25; // Kleiner Radius (Dicke des Rings)

    public Torus(Color color, Material material) {
        super(color, material);
    }

    public Torus(Color color) {
        super(color);
    }

    public Torus(Material material) {
        super(material);
    }

    public Torus() {
        super();
    }

    /**
     * Setzt die Radii des Torus
     * @param major Großer Radius
     * @param minor Kleiner Radius
     */
    public void setRadii(double major, double minor) {
        this.majorRadius = Math.max(0.1, major);
        this.minorRadius = Math.max(0.05, minor);
    }

    @Override
    public double getSDF(Vec3 p) {
        // Torus SDF: bekannte analytische Formel
        // q = (||xy|| - R1, z), distance = ||q|| - R2

        double xLength = Math.sqrt(p.x * p.x + p.y * p.y);
        double q_x = xLength - majorRadius;
        double q_z = p.z;

        double distance = Math.sqrt(q_x * q_x + q_z * q_z) - minorRadius;

        return distance;
    }
}

