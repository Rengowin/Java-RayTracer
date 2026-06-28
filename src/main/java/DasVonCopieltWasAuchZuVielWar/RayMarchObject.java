package DasVonCopieltWasAuchZuVielWar;

import BennysRayTrayer.core.Hit;
import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.rendering.Material;

import java.util.ArrayList;
import java.util.List;

/**
hey, magst du mir ein wenig helfen beim raymarching zu bauen und implementiern?
 //das war der promt den ich genutzt habe -.- wollte eigendlich nur die base sachen haben und nicht alles war in den andern ordner liegt
 */

/**
 * RayMarchObject: Implementiert Ray-Marching für komplexe Geometrien (Fraktale, implizite Funktionen)
 * basierend auf Signed Distance Functions (SDFs).
 */
/*public abstract class RayMarchObject extends Object3D {

    // Raymarching-Parameter
    protected static final double MAX_DISTANCE = 100.0;      // Maximale Marchierungsdistanz
    protected static final double MIN_DISTANCE = 0.001;      // Minimales Sicherheitsabstand
    protected static final int MAX_STEPS = 256;              // Maximale Marchierungsschritte
    protected static final double EPSILON = 1e-4;            // Toleranz für Oberflächenerkennung

    // Für Normale-Berechnung via Finite Differences
    private static final double SDF_DELTA = 1e-4;

    public RayMarchObject(Color color, Material material) {
        super(color, material);
    }

    public RayMarchObject(Color color) {
        super(color);
    }

    public RayMarchObject(Material material) {
        super(material);
    }

    public RayMarchObject() {
        super();
    }

    *//**
     * Abstrakte Methode: Liefert die Signed Distance Function
     * @param point Punkt im Objektraum
     * @return Vorzeichenbehafteter Abstand zur nächsten Oberfläche
     *         - Negative Werte: innen
     *         - Positive Werte: außen
     *         - 0: auf der Oberfläche
     *//*
    public abstract double getSDF(Vec3 point);

    *//**
     * Ray-Marching Algorithmus:
     * Verfolgt einen Ray iterativ entlang der SDF bis zur Oberflächenerkennung
     *//*
    private Hit raymarchRay(Ray ray) {
        Vec3 currentPos = ray.origin;
        double totalDistance = 0.0;
        int steps = 0;

        // Iteratives Marschieren
        while (steps < MAX_STEPS && totalDistance < MAX_DISTANCE) {
            // In lokalen Koordinaten transformieren
            Vec3 localPos = toLocalPoint(currentPos);

            // SDF-Wert an der aktuellen Position
            double sdfValue = getSDF(localPos);

            // Oberflächenerkennung
            if (sdfValue < EPSILON) {
                // Normale berechnen via Finite Differences
                Vec3 normal = calculateNormal(localPos);
                normal = toWorldDirection(normal);

                return new Hit(
                    totalDistance,
                    currentPos,
                    normal,
                    this
                );
            }

            // Sicherheitsabstand einhalten (verhindert Durchdringung)
            double stepSize = Math.max(sdfValue * 0.8, MIN_DISTANCE);

            // Ray um den SDF-Wert vorwärtsbewegen
            currentPos = currentPos.add(ray.direction.mul((float) stepSize));
            totalDistance += stepSize;
            steps++;
        }

        // Keine Oberflächenerkennung
        return null;
    }

    *//**
     * Normale via Gradient der SDF berechnen (Finite Differences)
     *//*
    private Vec3 calculateNormal(Vec3 p) {
        double fx = getSDF(p.add(new Vec3((float)SDF_DELTA, 0, 0))) -
                   getSDF(p.add(new Vec3((float)-SDF_DELTA, 0, 0)));
        double fy = getSDF(p.add(new Vec3(0, (float)SDF_DELTA, 0))) -
                   getSDF(p.add(new Vec3(0, (float)-SDF_DELTA, 0)));
        double fz = getSDF(p.add(new Vec3(0, 0, (float)SDF_DELTA))) -
                   getSDF(p.add(new Vec3(0, 0, (float)-SDF_DELTA)));

        return new Vec3((float)fx, (float)fy, (float)fz).normalize();
    }

    @Override
    public List<HitInterval> intersectIntervals(Ray ray) {
        // Ray-Marching durchführen
        Hit hit = raymarchRay(ray);

        List<HitInterval> intervals = new ArrayList<>();

        if (hit != null && hit.t > 0) {
            // Ein Hit gefunden: Erstelle ein degeneriertes Intervall
            // tEnter und tExit sind quasi identisch (Oberfläche)
            HitInterval interval = new HitInterval(
                hit.t,
                hit.t + 0.001,  // Minimales Interval für Konsistenz
                hit.normal,
                hit.normal.mul(-1),
                this
            );
            intervals.add(interval);
        }

        return intervals;
    }
}*/


