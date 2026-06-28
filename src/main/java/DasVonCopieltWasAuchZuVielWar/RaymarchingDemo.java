package DasVonCopieltWasAuchZuVielWar;

import BennysRayTrayer.objects.*;
import BennysRayTrayer.rendering.Material;
import BennysRayTrayer.scene.Scene;
import BennysRayTrayer.core.Vec3;


/**
 * RaymarchingDemo: Beispiel für die Verwendung der neuen Raymarching-Klassen
 *
 * Unterstützte Objekte:
 * - Mandelbulb: 3D Fraktal (komplex, beeindruckend visuell)
 * - MengerSponge: Geometrisches Fraktal
 * - Torus: Einfache analytische Form
 */
public class RaymarchingDemo {

    public static void setupRaymarchingScene(Scene scene) {
        // ============ BEISPIEL 1: Mandelbulb ============
        Color purpleMat = new Color(0.8f, 0.2f, 0.9f);
        Mandelbulb mandelbulb = new Mandelbulb(purpleMat);

        // Mandelbulb-Parameter anpassen
        mandelbulb.setIterations(12);  // 12-16 ist ein gutes Balance zwischen Qualität und Performance
        mandelbulb.setPower(8.0);      // Klassische Mandelbulb-Potenz

        // Position und Skalierung
        mandelbulb.setPosition(new Vec3(0, 0, 0));
        mandelbulb.setScale(new Vec3(2, 2, 2));

        // Material mit etwas Reflektion
        Material mandelbulbMat = new Material(
                new Vec3(0.2f, 0.4f, 0.6f),
                0.3f,
                0.5f,
                0.0f,
                0.0f,
                0.0f
        );
        mandelbulb.setMaterial(mandelbulbMat);

        // scene.addObject(mandelbulb);  // Auskommentieren wenn gewünscht


        // ============ BEISPIEL 2: Menger Sponge ============
        Color orangeMat = new Color(1f, 0.5f, 0.2f);
        MengerSponge mengerSponge = new MengerSponge(orangeMat);

        mengerSponge.setIterations(3);  // 3-4 ist meist ausreichend
        mengerSponge.setBoxSize(1.5);
        mengerSponge.setPosition(new Vec3(0, 0, 0));

        Material mengerMat = new Material(
                new Vec3(0.8f, 0.6f, 0.4f),
                0.6f,
                0.1f,
                0.0f,
                0.0f,
                0.0f
        );

        mengerSponge.setMaterial(mengerMat);

        // scene.addObject(mengerSponge);  // Auskommentieren wenn gewünscht


        // ============ BEISPIEL 3: Torus (schnelle Alternative zum Testen) ============
        Color blueMat = new Color(0.2f, 0.6f, 1f);
        Torus torus = new Torus(blueMat);

        torus.setRadii(1.5, 0.4);  // Major/Minor Radius
        torus.setPosition(new Vec3(0, 0, 0));
        torus.setRotation(new Vec3(0, 0.5f, 0));

        Material torusMat = new Material(
                new Vec3(0.4f, 0.6f, 0.8f),
                0.2f,
                0.8f,
                0.4f,
                0.0f,
                0.0f
        );

        torus.setMaterial(torusMat);

        scene.addObject(torus);  // Den Torus aktivieren - schnell zu rendern!
    }

    /**
     * Performance-Tipps für Raymarching:
     *
     * 1. MAX_STEPS reduzieren (in RayMarchObject):
     *    - Default 256 is high
     *    - Schneller: 64-128
     *    - Höchste Qualität: 256-512
     *
     * 2. MAX_DISTANCE begrenzen:
     *    - Ändert die "Sichtweite" des Raymarchings
     *    - Näher bei Kamera = schneller
     *
     * 3. Iterationen bei Fraktalen runterfahren:
     *    - Mandelbulb: start mit 8, dann 12
     *    - MengerSponge: 2-3 meist ausreichend
     *
     * 4. Camera distance anpassen:
     *    - Näher bedeutet weniger Marchschritte
     *
     * 5. Resolution reduzieren zum Testen:
     *    - Statt 1920x1080: 800x600 ist viel schneller
     */
    public static void tunePerformance() {
        System.out.println("=== Raymarching Performance Tuning ===");
        System.out.println("Für schnelle Preview: iterations 8, MAX_STEPS 64");
        System.out.println("Für hohe Qualität: iterations 16, MAX_STEPS 256");
        System.out.println("Mandelbulb ist CPU-intensiv - kleinere Resolution empfohlen!");
    }
}

