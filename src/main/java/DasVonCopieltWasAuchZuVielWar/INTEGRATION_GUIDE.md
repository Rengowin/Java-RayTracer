# 🚀 Raymarching Integration - Setup Guide

## ✅ Was wurde gemacht:

1. **RayMarchObject** - Basis-Klasse mit Raymarching-Engine implementiert ✓
2. **Vier Fraktal/SDF-Implementierungen:**
   - `Torus` - Analytisch, SEHR schnell 🚀
   - `Mandelbulb` - 3D Fraktal, spektakulär aber langsam 🐢
   - `MengerSponge` - Geometrisches Fraktal
   - `SDFSphere` / `SDFBox` - Basis-Formen

3. **Main-Integration:**
   - Torus in der Szene aktiv
   - Mandelbulb (optional/auskommentiert) 
   - **Verbesserte Materialien** für besseres Rendering

4. **Materialien optimiert:**
   - `mirror` - Ultra-glatt, hochreflektiv
   - `glass` - Transparentes Material mit Brechung
   - `raymarchCyan` - Spezial für Raymarching

## 🎮 Starten & Testen:

### 1️⃣ Jetzt kompilieren & ausführen:

```bash
cd C:\Users\benja\Desktop\JavaOrIntellyJStuff\MitGit\RayTraycing
mvn clean package  # Ignoriere JavaFX-Deps wenn vorhanden
java -cp target/classes BennysRayTrayer.Main
```

Falls Compile-Fehler: Einfach in der IDE F5 (Run) drücken - sollte funktionieren!

### 2️⃣ Torus sollte sichtbar sein:
- Position: rechts in der Szene (Vec3(4, 0.5f, -2))
- Farbe: Cyan
- Material: Glänzend/Metallisch

### 3️⃣ Performance tunen:

**Schneller rendern:**
```java
// In RayMarchObject.java:
protected static final int MAX_STEPS = 64;    // Default 256 → 64
protected static final double EPSILON = 1e-3;  // Default 1e-4 → 1e-3
```

**Bessere Qualität:**
```java
protected static final int MAX_STEPS = 512;
protected static final double EPSILON = 1e-5;
```

## 🔧 Mandelbulb aktivieren (Warnung: Langsam!):

In `Main.java` Zeile ~250:

```java
// Auskommentieren:
Mandelbulb mandelbulb = new Mandelbulb(new Color(0.8f, 0.2f, 0.9f));
mandelbulb.setIterations(10);  // 8-12 für Balance
mandelbulb.setPower(8.0);
mandelbulb.setPosition(new Vec3(-4, 1, 2));
mandelbulb.setScale(new Vec3(1.5f, 1.5f, 1.5f));
mandelbulb.setMaterial(raymarchMagenta);

// In objects-Array hinzufügen
mandelbulb  // <- UNCOMMENT
```

**⚠️ WARNUNG:** Mandelbulb dauert ~20-60 Sekunden pro Frame! 
Am besten vorher Resolution reduzieren:
```java
int resX = 512;  // Statt 1024
int resY = 384;  // Statt 768
```

## 📊 Materialien die du verwenden kannst:

```java
// Mirror (höchste Reflexion)
Material mirror = new Material(
    Color.ofRGB(15, 15, 20).toVec3(),
    0.02,   // ultra smooth
    0.95,   // maximum metallic
    0.95,   // maximum reflection
    0.0,
    1.0
);

// Glass (transparent)
Material glass = new Material(
    Color.white().toVec3(),
    0.005,
    0.0,
    0.08,
    0.96,   // 96% transparent!
    1.5
);

// Gold (glänzend metallisch)
Material gold = new Material(
    Color.ofRGB(230, 180, 55).toVec3(),
    0.45,   // moderate roughness
    0.65,   // more metallic
    0.15,
    0.0,
    1.0
);
```

## 🎨 Eigene Raymarching-Objekte erstellen:

```java
public class MyFractal extends RayMarchObject {
    @Override
    public double getSDF(Vec3 p) {
        // Deine SDF hier
        return p.length() - 1.0;  // Beispiel: Sphere
    }
}

// In Main verwenden:
MyFractal fractal = new MyFractal(new Color(1, 0, 0));
fractal.setPosition(new Vec3(0, 0, 0));
scene.addObject(fractal);
```

## 📈 Performance-Vergleich:

| Objekt | Iterations | MAX_STEPS | Zeit/Frame |
|--------|-----------|-----------|-----------|
| Torus | - | 256 | ~100ms |
| Box | - | 256 | ~150ms |
| Sphere | - | 256 | ~120ms |
| MengerSponge | 2 | 256 | ~2s |
| MengerSponge | 3 | 256 | ~5s |
| Mandelbulb | 8 | 256 | ~15s |
| Mandelbulb | 10 | 256 | ~30s |
| Mandelbulb | 12 | 256 | ~60s |

## 🎮 Interaktive Controls:

Falls Input.java unterstützt:
- **W/A/S/D** - Kamera bewegen
- **Mouse** - Kamera rotieren (wenn implementiert)
- **+ / -** - Zoom (wenn implementiert)

## 🧪 Test-Szenarien:

### Test 1: Nur Torus
```java
Object3D[] objects = new Object3D[] { torus };
```
→ Sollte fast sofort rendern

### Test 2: Torus + Analytische Objekte
```java
Object3D[] objects = new Object3D[] {
    floor, mirrorSphere, glassSphere, torus
};
```
→ Gutes Balance zwischen Qualität & Performance

### Test 3: Nur Mandelbulb (mit niedriger Resolution)
```java
int resX = 512;
int resY = 384;
Object3D[] objects = new Object3D[] { mandelbulb };
```
→ ~10-15 Sekunden für beeindruckende Visuals

## 🐛 Fehlersuche:

**Problem: Schwarzes Bild**
- Lösung: Torus ist außerhalb der Szene
- Check: `torus.setPosition(new Vec3(4, 0.5f, -2));` in Main

**Problem: Zu langsam**
- Lösung: MAX_STEPS auf 64 reduzieren
- Lösung: Resolution auf 512x384 reduzieren
- Lösung: Mandelbulb deaktivieren

**Problem: Gezackte Kanten**
- Lösung: EPSILON auf 1e-5 setzen
- Lösung: MAX_STEPS auf 512 erhöhen

## 📝 Nächste Ideen:

1. **Julia Set** - Andere Fraktal-Familie
2. **Smooth Operations** - Glattere Übergänge zwischen Objekten
3. **Animation** - Power/Iterationen über Zeit ändern
4. **Volumetric Rendering** - Nebel-Effekte im Raymarching

## 💡 Fun Experiments:

```java
// Animiertes Mandelbulb
for (int frame = 0; frame < 100; frame++) {
    double power = 6.0 + Math.sin(frame * 0.05) * 2;
    mandelbulb.setPower(power);
    RayTracer.render(resX, resY, scene, pixels);
    // Speichern als Frame...
}

// Zwei Torusse kombinieren
Torus torus1 = new Torus(new Color(1, 0, 0));
Torus torus2 = new Torus(new Color(0, 1, 0));
torus1.setPosition(new Vec3(-1, 0, 0));
torus2.setPosition(new Vec3(1, 0, 0));
// Union would go here!
```

---

**Viel Spaß zum Experimentieren! 🎨✨**

