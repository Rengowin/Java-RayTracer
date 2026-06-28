package DasVonCopieltWasAuchZuVielWar;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.rendering.Material;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;


/**
 * MengerSponge: Klassisches geometrisches Fraktal
 * Definiert durch rekursives Entfernen von Boxen
 */
public class MengerSponge extends RayMarchObject {

    private int iterations = 4;  // Rekursions-Tiefe (höher = komplexer)
    private double boxSize = 1.0; // Größe der Basis-Box

    public MengerSponge(Color color, Material material) {
        super(color, material);
    }

    public MengerSponge(Color color) {
        super(color);
    }

    public MengerSponge(Material material) {
        super(material);
    }

    public MengerSponge() {
        super();
    }

    /**
     * Setzt die Rekursions-Tiefe
     * Höhere Werte = feinere Details aber langsamer
     */
    public void setIterations(int iterations) {
        this.iterations = Math.max(1, Math.min(8, iterations));
    }

    /**
     * Setzt die Größe der Menger Sponge
     */
    public void setBoxSize(double size) {
        this.boxSize = size;
    }

    @Override
    public double getSDF(Vec3 p) {
        // SDF der Menger Sponge basierend auf der Wiederholungs-Struktur
        return mengerSDF(p, iterations);
    }

    /**
     * Rekursive SDF-Berechnung für Menger Sponge
     */
    private double mengerSDF(Vec3 p, int depth) {
        // Basis-Box SDF
        Vec3 q = new Vec3(Math.abs(p.x), Math.abs(p.y), Math.abs(p.z));

        // Größe adjustieren
        double s = boxSize;

        // SDF einer Box
        double boxSDF = boxSDF(q, s);

        // Bei Tiefe 0 zurückgeben
        if (depth <= 0) {
            return boxSDF;
        }

        // Rekursiv kleinere Boxes entfernen
        double scale = 1.0 / 3.0;
        Vec3 offset = new Vec3((float)(s * 2.0 * scale),
                               (float)(s * 2.0 * scale),
                               (float)(s * 2.0 * scale));

        // Diese neun Positionen entfernen (Menger-Struktur)
        double[] dists = new double[] {
            // Kanten entfernen
            mengerSDF(q.sub(new Vec3((float)(s * 2.0 * scale), 0, 0)), depth - 1),
            mengerSDF(q.sub(new Vec3((float)(-s * 2.0 * scale), 0, 0)), depth - 1),
            mengerSDF(q.sub(new Vec3(0, (float)(s * 2.0 * scale), 0)), depth - 1),
            mengerSDF(q.sub(new Vec3(0, (float)(-s * 2.0 * scale), 0)), depth - 1),
            mengerSDF(q.sub(new Vec3(0, 0, (float)(s * 2.0 * scale))), depth - 1),
            mengerSDF(q.sub(new Vec3(0, 0, (float)(-s * 2.0 * scale))), depth - 1),
        };

        // Kombiniere mit einem "subtract" Operation
        double result = boxSDF;
        for (double d : dists) {
            result = Math.max(result, -d);  // Subtrahiere (smooth-ish)
        }

        return result;
    }

    /**
     * SDF für eine Box (symmetrisch um Ursprung)
     */
    private double boxSDF(Vec3 p, double size) {
        Vec3 q = new Vec3(Math.abs(p.x) - (float)size,
                         Math.abs(p.y) - (float)size,
                         Math.abs(p.z) - (float)size);

        float outDist = Math.max(0, Math.max(q.x, Math.max(q.y, q.z)));
        float inDist = Math.min(0, Math.max(q.x, Math.max(q.y, q.z)));

        return outDist + inDist;
    }
}

