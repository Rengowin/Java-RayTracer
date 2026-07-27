# Java Ray Tracer

> This README is provided in English first, followed by a German version below.

## English

### Java Ray Tracer

A Java-based ray tracer developed as part of the Ray Tracing course at HTW Berlin.  
The project evolved from a recursive ray tracer into a renderer featuring path tracing, ray marching, constructive solid geometry (CSG), physically based shading, and performance optimizations.

#### Preview

<img width="1917" height="1107" alt="FinalImageOfTheRayTracerProjekt" src="https://github.com/user-attachments/assets/33bee32f-150b-475c-97b3-ed99f63374b0" />

#### Features

##### Rendering
- Path tracing
- Cook-Torrance BRDF
- Reflections and refractions
- Soft shadows
- Ambient occlusion
- Anti-aliasing via jittered sampling

##### Geometry
- Sphere
- Quadric
- Constructive solid geometry (CSG)
- Signed distance field (SDF) objects
- Smooth CSG via ray marching

##### Environment
- Procedural sky
- Fog

##### Performance
- Multithreading
- Bounding sphere optimization
- Distance-only intersection tests
- Cached transform calculations
- Deterministic sampling

#### Technical Highlights

- Implemented both analytic and ray-marched intersection systems
- Designed a physically based shading model using a Cook-Torrance BRDF
- Added acceleration strategies to significantly reduce render time
- Balanced image quality and performance through sampling and multithreading

#### Performance

Rendering a 1024×768 image:

| Version | Time |
|---------|------:|
| Initial implementation | ~6 min |
| Final implementation | ~1 min 25 s |

The final version renders the scene significantly faster while also using improved lighting and a higher number of shadow rays.

#### Development Progress

- Early skybox-only stage
- Fog integration
- Ambient occlusion
- Material and lighting fixes
- Final path-traced render

#### What I Learned

- Ray tracing and ray marching fundamentals
- Physically based rendering concepts
- Performance optimization in Java
- Tradeoffs between accuracy, sampling, and speed

#### Running the Project

##### Requirements
- Java version: [add your version here]
- [Any other dependencies if needed]

##### Run from source
```bash
# add your build/run command here
```
----

## Deutsch

### Java Ray Tracer

Ein Java-basierter Raytracer, entwickelt im Rahmen des Ray-Tracing-Kurses an der HTW Berlin.
Das Projekt hat sich von einem einfachen rekursiven Raytracer zu einem Renderer mit Path Tracing, Ray Marching, Constructive Solid Geometry (CSG), physikalisch basierter Beleuchtung und Performance-Optimierungen weiterentwickelt.

#### Preview

<img width="1917" height="1107" alt="FinalImageOfTheRayTracerProjekt" src="https://github.com/user-attachments/assets/33bee32f-150b-475c-97b3-ed99f63374b0" />

#### Featuers

##### Render
- Path Tracing
- Cook-Torrance BRDF
- Spiegelungen und Brechungen
- Weiche Schatten
- Ambient Occlusion
- Anti-Aliasing mit Jittered Sampling

##### Geometrie
- Analytische Objekte (Quatricen)
- Constructive Solid Geometry (CSG)
- Signed Distance Field (SDF) Objekte
- Smooth CSG mit Ray Marching

##### Umgebung
- Prozeduraler Hintergrund
- Nebel

##### Performance
- Multithreading
- Bounding-Sphere-Optimierung
- Distance-only Intersection Tests
- Zwischenspeichern von Transformationsberechnungen
- Deterministisches Sampling

#### Technische Highloghts
- Implementierung von analytischen und Ray-Marching-basierten Schnittsystemen
- Entwicklung eines physikalisch basierten Shading-Modells mit Cook-Torrance BRDF
- Optimierungen zur deutlichen Reduzierung der Renderzeit
- Ausbalancierung von Bildqualität und Performance durch Sampling und Multithreading
