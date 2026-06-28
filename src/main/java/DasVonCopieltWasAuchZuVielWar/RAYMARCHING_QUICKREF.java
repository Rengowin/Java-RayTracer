/*
// RAYMARCHING QUICK REFERENCE
// ================================

// 1. STANDARD FORMEN VERWENDEN
// =============================

// Schnell & einfach: Torus
Torus torus = new Torus(new Color(1, 0, 0));
torus.setRadii(1.5, 0.4);
scene.addObject(torus);

// Einfach: SDF-Sphere
SDFSphere sphere = new SDFSphere(new Color(0, 1, 0));
sphere.setRadius(1.0);
scene.addObject(sphere);

// Einfach: SDF-Box
SDFBox box = new SDFBox(new Color(0, 0, 1), new Vec3(1, 0.5f, 0.5f));
scene.addObject(box);

// Komplex: Fraktal-Mandelbulb
Mandelbulb mandelbulb = new Mandelbulb(new Color(0.8f, 0.2f, 0.9f));
mandelbulb.setIterations(12);  // 8-16 empfohlen
mandelbulb.setPower(8.0);       // Klassisch 8
scene.addObject(mandelbulb);

// Komplex: Fraktal-Menger Sponge
MengerSponge sponge = new MengerSponge(new Color(1, 0.5f, 0));
sponge.setIterations(2);  // 2-4 empfohlen
scene.addObject(sponge);


// 2. EIGENES FRAKTAL SCHREIBEN
// ============================

public class MyFractal extends RayMarchObject {

    // MUSS IMPLEMENTIERT SEIN:
    @Override
    public double getSDF(Vec3 p) {
        // Einfaches Beispiel: Sphere mit Störung
        double dist = p.length() - 1.0;  // Basis-Sphäre
        dist += Math.sin(p.x * 5) * 0.1; // Störung
        return dist;
    }
}


// 3. PERFORMANCE TUNING
// ====================

// Schnell (weniger Qualität)
RayMarchObject obj = ...;
// Problem: In RayMarchObject.java direkt editieren:
//   MAX_STEPS = 64
//   EPSILON = 1e-3

// Balanciert (Standard)
//   MAX_STEPS = 256
//   EPSILON = 1e-4

// Hochwertig (langsam)
//   MAX_STEPS = 512
//   EPSILON = 1e-5


// 4. MATERIAL + RENDERING
// =======================

SDFSphere sphere = new SDFSphere(new Color(1, 0, 0));

// Material für Metallic-Effekt
Material metalMat = new Material();
metalMat.roughness = 0.1;
metalMat.metallic = 0.9;
metalMat.reflectionStrength = 0.8;
sphere.setMaterial(metalMat);

// Material für matte Oberfläche
Material matteMat = new Material();
matteMat.roughness = 0.8;
matteMat.metallic = 0.0;
sphere.setMaterial(matteMat);

// Material mit Transparenz
Material glassMat = new Material();
glassMat.transparency = 0.7;
glassMat.refractiveIndex = 1.5;
sphere.setMaterial(glassMat);


// 5. TRANSFORMATIONEN
// ===================

SDFBox box = new SDFBox(new Color(1, 0, 0));

// Position setzen
box.setPosition(new Vec3(2, 3, 4));

// Skalierung
box.setScale(new Vec3(2, 2, 2));

// Rotation (in Radianten)
box.setRotation(new Vec3(0, (float)Math.PI / 4, 0));


// 6. HÄUFIGE SDFs FÜR CUSTOM IMPLEMENTIERUNG
// ===========================================

// Sphere (Radius r)
return p.length() - r;

// Box (Halbe-Größe s)
Vec3 q = new Vec3(Math.abs(p.x) - s, Math.abs(p.y) - s, Math.abs(p.z) - s);
double out = Math.max(0, Math.max(q.x, Math.max(q.y, q.z)));
double in = Math.min(0, Math.max(q.x, Math.max(q.y, q.z)));
return out + in;

// Plane (bei y=0)
return Math.abs(p.y);

// Cylinder (Radius r)
return Math.sqrt(p.x*p.x + p.z*p.z) - r;

// Torus (Major R, Minor r)
double xz = Math.sqrt(p.x*p.x + p.z*p.z);
return Math.sqrt((xz - R)*(xz - R) + p.y*p.y) - r;


// 7. OPERATIONEN (Union, Difference, etc.)
// ========================================

// Union (min)
double combine(double sd1, double sd2) {
    return Math.min(sd1, sd2);
}

// Intersection (max)
double combine(double sd1, double sd2) {
    return Math.max(sd1, sd2);
}

// Difference (subtract sd2 from sd1)
double combine(double sd1, double sd2) {
    return Math.max(sd1, -sd2);
}

// Smooth Union (glattere Kanten) - k ist glattungsparameter
double smoothUnion(double sd1, double sd2, double k) {
    double h = Math.max(k - Math.abs(sd1 - sd2), 0) / k;
    return Math.min(sd1, sd2) - h*h*h*k / 6.0;
}


// 8. DEBUGGING
// ===========

// Wenn nichts sichtbar:
// 1. MAX_DISTANCE in RayMarchObject erhöhen
// 2. EPSILON erhöhen (z.B. 1e-3)
// 3. MAX_STEPS erhöhen
// 4. Camera näher heranzoomen

// Wenn zu langsam:
// 1. MAX_STEPS reduzieren (z.B. 64)
// 2. Auflösung reduzieren (z.B. 800x600)
// 3. Fraktal-Iterationen reduzieren
// 4. SDF_DELTA erhöhen für schnellere Normalen

// Wenn gezackt:
// 1. EPSILON reduzieren (z.B. 1e-5)
// 2. SDF_DELTA reduzieren


// 9. ANIMATION BEISPIEL
// ====================

for (int frame = 0; frame < 100; frame++) {
    // Parameter über Zeit ändern
    mandelbulb.setPower(6.0 + Math.sin(frame * 0.05) * 2);

    // Rendern
    RayTracer.render(1920, 1080, scene, pixels);

    // Frame speichern / anzeigen...
}


// 10. TIPPS & TRICKS
// =================

// Torus vs Mandelbulb zum Testen?
// → Nutze Torus! Super schnell für Settings.

// Mehrere Fraktale kombinieren?
// → Nutze Operationen (Union, Difference) in einer SDF

// Zu detailliert?
// → Reduziere Iterationen, nicht Resolution

// Zu unscharf?
// → Reduziere EPSILON oder erhöhe MAX_STEPS

// Wollt ihr mehr Qualität?
// → Höhere Resolution + mehr Samples (Super-Sampling)

*/
