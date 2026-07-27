# Java Ray Tracer

> This README is provided in English first, followed by a German version below.

## English

A Java-based ray tracer developed as part of the Ray Tracing course at HTW Berlin.  
The project evolved from a recursive ray tracer into a renderer featuring path tracing, ray marching, constructive solid geometry (CSG), physically based shading, and performance optimizations.

## Preview

<img width="1917" height="1107" alt="FinalImageOfTheRayTracerProjekt" src="https://github.com/user-attachments/assets/33bee32f-150b-475c-97b3-ed99f63374b0" />

## Features

### Rendering
- Path tracing
- Cook-Torrance BRDF
- Reflections and refractions
- Soft shadows
- Ambient occlusion
- Anti-aliasing via jittered sampling

### Geometry
- Sphere
- Quadric
- Constructive solid geometry (CSG)
- Signed distance field (SDF) objects
- Smooth CSG via ray marching

### Environment
- Procedural sky
- Fog

### Performance
- Multithreading
- Bounding sphere optimization
- Distance-only intersection tests
- Cached transform calculations
- Deterministic sampling

## Technical Highlights

- Implemented both analytic and ray-marched intersection systems
- Designed a physically based shading model using a Cook-Torrance BRDF
- Added acceleration strategies to significantly reduce render time
- Balanced image quality and performance through sampling and multithreading

## Performance

Rendering a 1024×768 image:

| Version | Time |
|---------|------:|
| Initial implementation | ~6 min |
| Final implementation | ~1 min 25 s |

The final version renders the scene significantly faster while also using improved lighting and a higher number of shadow rays.

## Development Progress

- Early skybox-only stage
- Fog integration
- Ambient occlusion
- Material and lighting fixes
- Final path-traced render

## What I Learned

- Ray tracing and ray marching fundamentals
- Physically based rendering concepts
- Performance optimization in Java
- Tradeoffs between accuracy, sampling, and speed

## Running the Project

### Requirements
- Java version: 17 or later
- [Any other dependencies if needed]

### Run from source
```bash
# add your build/run command here
```

### Run the release version
If you download the compiled release, run:

```bash
java -jar Java-RayTracer.jar
```

## Future Improvements

- Russian roulette termination for deeper recursion
- Texture mapping
- Animation
- Color variation effects

## License

[Add your license here if applicable]

---

## Deutsch

Ein Java-basierter Raytracer, entwickelt im Rahmen des Ray-Tracing-Kurses an der HTW Berlin.  
Das Projekt hat sich von einem einfachen rekursiven Raytracer zu einem Renderer mit Path Tracing, Ray Marching, Constructive Solid Geometry (CSG), physikalisch basierter Beleuchtung und Performance-Optimierungen weiterentwickelt.

## Vorschau

<img width="1917" height="1107" alt="FinalImageOfTheRayTracerProjekt" src="https://github.com/user-attachments/assets/33bee32f-150b-475c-97b3-ed99f63374b0" />

## Features

### Rendering
- Path Tracing
- Cook-Torrance BRDF
- Spiegelungen und Brechungen
- Weiche Schatten
- Ambient Occlusion
- Anti-Aliasing mit Jittered Sampling

### Geometrie
- Sphere
- Quadric
- Constructive Solid Geometry (CSG)
- Signed Distance Field (SDF) Objekte
- Smooth CSG mit Ray Marching

### Umgebung
- Prozeduraler Himmel
- Fog

### Performance
- Multithreading
- Bounding-Sphere-Optimierung
- Distance-only Intersection Tests
- Zwischenspeichern von Transformationsberechnungen
- Deterministisches Sampling

## Technische Highlights

- Implementierung von analytischen und Ray-Marching-basierten Schnittsystemen
- Entwicklung eines physikalisch basierten Shading-Modells mit Cook-Torrance BRDF
- Optimierungen zur deutlichen Reduzierung der Renderzeit
- Ausbalancierung von Bildqualität und Performance durch Sampling und Multithreading

## Performance

Rendering eines Bildes mit 1024×768 Pixeln:

| Version | Zeit |
|---------|------:|
| Erste Implementierung | ~6 Min |
| Finale Implementierung | ~1 Min 25 s |

Die finale Version rendert die Szene deutlich schneller und verwendet gleichzeitig eine verbesserte Beleuchtung sowie mehr Shadow Rays.

## Entwicklungsverlauf

- Erste Phase mit reinem Skybox-Rendering
- Fog-Integration
- Ambient Occlusion
- Material- und Lichtverbesserungen
- Finale Path-Traced Version

## Was ich gelernt habe

- Grundlagen von Ray Tracing und Ray Marching
- Physikalisch basiertes Rendering
- Performance-Optimierung in Java
- Abwägung zwischen Genauigkeit, Sampling und Geschwindigkeit

## Zukünftige Verbesserungen

- Russian Roulette zur besseren Behandlung tiefer Rekursionen
- Texture Mapping
- Animation
- Farbvariationen
