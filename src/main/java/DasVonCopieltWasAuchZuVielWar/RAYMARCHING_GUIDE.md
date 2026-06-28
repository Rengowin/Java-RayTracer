# 🎨 Raymarching-System für RayTraycing

## 📚 Überblick

Du hast jetzt ein vollständiges **Raymarching-System** für dein Ray Tracing Engine! Raymarching ist eine Technik zum Rendern von:

- **Fraktalen** (Mandelbulb, Menger Sponge)
- **Impliziten Oberflächen** (alles, das als Signed Distance Function (SDF) definierbar ist)
- **Komplexer Geometrie**, die analytisch schwierig zu berechnen ist

## 🏗️ Architektur

```
Object3D (Basisklasse)
    ↓
RayMarchObject (abstrakt)
    ├─ Mandelbulb
    ├─ MengerSponge
    └─ Torus
```

### Kernkonzepte

**Signed Distance Function (SDF):**
- Gibt an, wie weit ein Punkt von der nächsten Oberfläche entfernt ist
- Positive Werte = außerhalb
- Negative Werte = innerhalb
- 0 = auf der Oberfläche

**Ray-Marching Algorithmus:**
```
1. Starte Ray bei der Kamera
2. Wiederhole bis Oberflächenerkennung:
   a. Berechne SDF-Wert an aktueller Position
   b. Wenn SDF < EPSILON → Oberfläche gefunden ✓
   c. Sonst: Bewege Ray um SDF-Wert vorwärts
   d. Wenn zu viele Schritte → abbrechen
3. Berechne Oberflächennormale via Finite Differences
4. Rendern wie normaler Ray-Tracing Hit
```

## 🎯 Verwendung

### Einfaches Beispiel: Torus rendern

```java
// Torus erstellen (schnell!)
Torus torus = new Torus(new Color(0.2f, 0.6f, 1f));
torus.setRadii(1.5, 0.4);  // Major Radius, Minor Radius
torus.setPosition(new Vec3(0, 0, 0));

// Zu Szene hinzufügen
scene.addObject(torus);

// Rendern
RayTracer.render(1920, 1080, scene, pixels);
```

### Complexes Beispiel: Mandelbulb

```java
// Mandelbulb erstellen (spektakulär aber langsam!)
Mandelbulb mandelbulb = new Mandelbulb(new Color(0.8f, 0.2f, 0.9f));

// Parameter tunen
mandelbulb.setIterations(12);  // Höher = detaillierter aber langsamer
mandelbulb.setPower(8.0);      // Potenz des Fraktals

mandelbulb.setPosition(new Vec3(0, 0, 0));
mandelbulb.setScale(new Vec3(2, 2, 2));

// Material
Material mat = new Material();
mat.roughness = 0.3;
mat.metallic = 0.5;
mandelbulb.setMaterial(mat);

scene.addObject(mandelbulb);
```

## ⚙️ Parameter & Konfiguration

### In `RayMarchObject.java`:

```java
MAX_DISTANCE = 100.0;      // Wie weit der Ray marschiert bevor er abbricht
MIN_DISTANCE = 0.001;      // Sicherheitsabstand (verhindert Durchdringung)
MAX_STEPS = 256;           // Max Marchierungsschritte
EPSILON = 1e-4;            // Toleranz für Oberflächenerkennung
```

**Performance-Tuning:**
- `MAX_STEPS` reduzieren → schneller, aber weniger genau
  - Schnell: 64
  - Balanciert: 128
  - Hochwertig: 256+

- `EPSILON` erhöhen → schneller, aber "unscharfere" Oberflächen
  - Default: 1e-4 (gut)
  - Schneller: 1e-3
  - Qualität: 1e-5

### Fraktal-spezifische Parameter:

**Mandelbulb:**
```java
mandelbulb.setIterations(n);   // 8-16 empfohlen
mandelbulb.setPower(8.0);      // Typisch 4, 6, 8, 10
```

**MengerSponge:**
```java
mengerSponge.setIterations(n); // 2-4 empfohlen
mengerSponge.setBoxSize(1.0);  // Größe der Struktur
```

**Torus:**
```java
torus.setRadii(major, minor);  // Major Radius, Minor Radius
```

## 🚀 Eigene SDF implementieren

### Template:

```java
public class MyFractal extends RayMarchObject {
    
    @Override
    public double getSDF(Vec3 p) {
        // Deine SDF-Logik hier
        // return distance_to_surface;
        
        // Beispiel: Sphere
        return p.length() - 1.0;  // Radius 1
    }
}
```

### Häufige SDFs:

```java
// Sphere mit Radius r
return p.length() - r;

// Box mit Größe s
Vec3 q = new Vec3(Math.abs(p.x) - s, 
                   Math.abs(p.y) - s, 
                   Math.abs(p.z) - s);
float outDist = Math.max(0, Math.max(q.x, Math.max(q.y, q.z)));
float inDist = Math.min(0, Math.max(q.x, Math.max(q.y, q.z)));
return outDist + inDist;

// Plane bei y=0
return Math.abs(p.y);

// Torus
double xzLen = Math.sqrt(p.x*p.x + p.z*p.z);
return Math.sqrt((xzLen - major)*(xzLen - major) + p.y*p.y) - minor;
```

### Boolesche Operationen (Union, Intersection, Difference):

```java
// Union: min(sdf1, sdf2)
double union(double sdf1, double sdf2) {
    return Math.min(sdf1, sdf2);
}

// Intersection: max(sdf1, sdf2)
double intersection(double sdf1, double sdf2) {
    return Math.max(sdf1, sdf2);
}

// Difference: sdf1 \ sdf2 = max(sdf1, -sdf2)
double difference(double sdf1, double sdf2) {
    return Math.max(sdf1, -sdf2);
}

// Smooth Union (für glattere Ränder):
double smoothUnion(double sdf1, double sdf2, double k) {
    double h = Math.max(k - Math.abs(sdf1 - sdf2), 0) / k;
    return Math.min(sdf1, sdf2) - h * h * h * k / 6.0;
}
```

## 📊 Performance-Analyse

### Render-Zeiten (Richtwerte):

| Form | Auflösung | Iterations | Zeit |
|------|-----------|-----------|------|
| Torus | 1920x1080 | - | ~100ms |
| Mandelbulb | 1920x1080 | 12 | ~30s |
| MengerSponge | 1920x1080 | 3 | ~5s |

**Tipps zur Optimierung:**
1. Auflösung reduzieren (800x600 zum Testen)
2. MAX_STEPS auf 128 setzen
3. Fraktal-Iterationen reduzieren
4. Camera näher heranzoomen (weniger Marchschritte)

## 🎓 Mathematische Hintergründe

### Gradient-basierte Normalen:

```
N = normalize(∇SDF)
  = normalize([
      SDF(p + (δ, 0, 0)) - SDF(p - (δ, 0, 0)),
      SDF(p + (0, δ, 0)) - SDF(p - (0, δ, 0)),
      SDF(p + (0, 0, δ)) - SDF(p - (0, 0, δ))
    ])
```

Das ist bereits in `calculateNormal()` implementiert!

### Escape-Time Formeln:

**Mandelbulb:**
```
z_{n+1} = z_n^8 + c
distance ≈ 0.5 * ln(|z|) * |z| / |dz|
```

**Julia Set (verwandt):**
```
z_{n+1} = z_n^2 + p
```

## 🐛 Debugging-Tipps

1. **Schwarze Pixel überall?**
   - MAX_DISTANCE ist zu klein
   - oder EPSILON zu groß
   - oder Camera ist außerhalb des Objekts

2. **Zu langsam?**
   - MAX_STEPS reduzieren
   - Auflösung reduzieren
   - Fraktal-Iterationen reduzieren

3. **Gezackte Kanten?**
   - EPSILON kleiner machen
   - SDF_DELTA anpassen (aktuell 1e-4)

## 📝 Nächste Schritte

1. **Testen:** `RaymarchingDemo.java` hat Beispiele!
2. **Eigen-SDFs:** Implementiere eine Sphere oder Box
3. **Kombination:** Nutze mehrere RayMarchObjects gleichzeitig mit analytischen Objekten
4. **Smooth Blending:** Probiere smooth unions für coole Effekte
5. **Animation:** Variiere die SDF-Parameter über Zeit

## 🎨 Coole Experimente

```java
// Animiertes Mandelbulb
for (int frame = 0; frame < 100; frame++) {
    mandelbulb.setPower(6.0 + Math.sin(frame * 0.1) * 2);
    RayTracer.render(1920, 1080, scene, pixels);
}

// Zwei Objekte kombinieren
Union combined = new Union(mandelbulb, mengerSponge);
scene.addObject(combined);
```

---

**Viel Spaß beim Raymarching! 🚀✨**

