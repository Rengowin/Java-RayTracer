package DasVonCopieltWasAuchZuVielWar;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.rendering.Material;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;

/**
 * Mandelbulb: 3D-Fraktal basierend auf der Mandelbrot-Menge
 * SDF-Implementierung nach der klassischen Formel
 */
public class Mandelbulb extends RayMarchObject {

    private int iterations = 16;  // Anzahl der Iterationen (höher = detaillierter aber langsamer)
    private double bailout = 2.0; // Bailout-Radius
    private double power = 8.0;   // Potenz (typisch 8 für klassisches Mandelbulb)

    public Mandelbulb(Color color, Material material) {
        super(color, material);
    }

    public Mandelbulb(Color color) {
        super(color);
    }

    public Mandelbulb(Material material) {
        super(material);
    }

    public Mandelbulb() {
        super();
    }

    /**
     * Setzt die Iterations-Tiefe des Fraktals
     * Höhere Werte = mehr Detail aber langsamer
     */
    public void setIterations(int iterations) {
        this.iterations = Math.max(1, Math.min(32, iterations));
    }

    /**
     * Setzt die Potenz der Mandelbulb-Formel
     * Klassisch: 8 (aber auch 4, 6, 10, etc. sind interessant)
     */
    public void setPower(double power) {
        this.power = power;
    }

    @Override
    public double getSDF(Vec3 p) {
        // Die folgende Implementierung berechnet eine Annäherung der SDF
        // basierend auf der Escape-Time-Formel der Mandelbrot-Menge

        Vec3 z = new Vec3(p.x, p.y, p.z);
        double dr = 0.0; // Derivative
        double r = 0.0;

        for (int i = 0; i < iterations; i++) {
            r = z.length();

            // Bailout-Bed: wenn wir weit genug weg sind, terminieren
            if (r > bailout) break;

            // Berechne die Ableitung für bessere SDF-Approximation
            // dr = dr * power * r^(power-1)
            dr = dr * (power - 1.0) * Math.pow(r, power - 2.0) + 1.0;

            // Berechne z_new = z^power + p
            z = complexPowerVec3(z, power).add(p);
        }

        r = z.length();

        // SDF-Approximation nach dem Escape-Time-Verfahren
        // Formel: distance ≈ 0.5 * ln(r) * r / dr
        double distEstimate = 0.5 * Math.log(r) * r / dr;

        // Negative Werte sind keine echte SDF - normalisieren
        return Math.max(0.0, distEstimate - 0.5);
    }

    /**
     * Berechnet z^n für komplexe Zahlen, dargestellt als Vec3
     * (x, y, z) als sphärische Koordinaten der komplexen Potenz
     */
    private Vec3 complexPowerVec3(Vec3 z, double n) {
        // Konvertiere zu sphärischen Koordinaten
        double r = z.length();
        double theta = Math.atan2(z.y, z.x);
        double phi = Math.atan2(z.z,
                               Math.sqrt(z.x * z.x + z.y * z.y));

        // Potenz in sphärischen Koordinaten
        double rn = Math.pow(r, n);
        double nTheta = n * theta;
        double nPhi = n * phi;

        // Zurück zu kartesischen Koordinaten
        double sinNPhi = Math.sin(nPhi);
        double cosNPhi = Math.cos(nPhi);
        double sinNTheta = Math.sin(nTheta);
        double cosNTheta = Math.cos(nTheta);

        float x = (float) (rn * cosNTheta * cosNPhi);
        float y = (float) (rn * sinNTheta * cosNPhi);
        float z_new = (float) (rn * sinNPhi);

        return new Vec3(x, y, z_new);
    }
}

