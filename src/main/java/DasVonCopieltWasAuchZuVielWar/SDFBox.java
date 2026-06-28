package DasVonCopieltWasAuchZuVielWar;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.rendering.Material;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;


/**
 * SDFBox: Box-förmiges Objekt via Signed Distance Function
 * Analytische SDF für Quader - schnell und präzise
 */
public class SDFBox extends RayMarchObject {

    private Vec3 halfExtents;  // Halbe Größe in jede Richtung

    public SDFBox(Color color, Material material, Vec3 halfExtents) {
        super(color, material);
        this.halfExtents = halfExtents;
    }

    public SDFBox(Color color, Vec3 halfExtents) {
        super(color);
        this.halfExtents = halfExtents;
    }

    public SDFBox(Vec3 halfExtents) {
        super();
        this.halfExtents = halfExtents;
    }

    /**
     * Setzt die Größe der Box
     * @param size in jede Richtung (symmetrisch)
     */
    public void setSize(double size) {
        this.halfExtents = new Vec3((float)size, (float)size, (float)size);
    }

    /**
     * Setzt die Größe asymmetrisch
     */
    public void setExtents(Vec3 halfExtents) {
        this.halfExtents = halfExtents;
    }

    @Override
    public double getSDF(Vec3 p) {
        // Box SDF: http://mercury.sexy/hg_sdf/
        Vec3 q = new Vec3(
            Math.abs(p.x) - halfExtents.x,
            Math.abs(p.y) - halfExtents.y,
            Math.abs(p.z) - halfExtents.z
        );

        // Außere Distanz (für Punkte außerhalb)
        double outDist = Math.max(0, Math.max(q.x, Math.max(q.y, q.z)));

        // Innere Distanz (für Punkte innerhalb)
        double inDist = Math.min(0, Math.max(q.x, Math.max(q.y, q.z)));

        return outDist + inDist;
    }
}

