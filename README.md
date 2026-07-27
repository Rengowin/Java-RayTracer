Java Raytracer (Studie Projekt)

Below German :D

# Java Ray Tracer

A Java-based ray tracer developed during the **Ray Tracing** course at **HTW Berlin**.

The project started as a basic ray tracer and gradually evolved into a small renderer featuring **Path Tracing**, **Ray Marching**, **Constructive Solid Geometry (CSG)** and several rendering and performance improvements.

Below you can find the final render as well as some development snapshots that document the project's evolution.

Final Image
<img width="1917" height="1107" alt="FinalImageOfTheRayTracerProjekt" src="https://github.com/user-attachments/assets/33bee32f-150b-475c-97b3-ed99f63374b0" />

## Features
- Analytic Objects
  - Spheres
  - Quadrics
  - Constructive Solid Geometry (CSG)

- Ray Marching
  - SDF Objects
  - Smooth CSG Operations

- Rendering
  - Cook-Torrance BRDF
  - Reflections
  - Refractions
  - Path Tracing
  - Soft Shadows
  - Ambient Occlusion
  - Anti-Aliasing (Jittered Sampling)

- Environment
  - Procedural Sky
  - Fog

- Performance
  - Multithreading
  - Bounding Sphere Optimization
  - Distance-only Intersection Paths
  - Cached Transform Calculations
  - Deterministic Sampling


---
Development

Fog (10 times Stronger and less pixel then on the final Image :D):
(img)
<img width="1018" height="768" alt="FogPNG" src="https://github.com/user-attachments/assets/fbd276c5-4770-4306-9db1-1b2d8c3ec332" />

Skybox(only skybox)
<img width="1022" height="768" alt="SkyBoxPNG" src="https://github.com/user-attachments/assets/db02fc86-7352-4c05-bc92-38678db7018c" />

RayMarchObject (weil eigendlich sieht man das ja eigendlich in final Image sowie das analytische)
## Performance Improvements

During development I spent a significant amount of time improving the renderer's performance.

The initial renderer required approximately **6 minutes** to render a **1024×768** image using **4 shadow rays**.

After introducing several optimizations and rendering improvements, including:

- Cook-Torrance material model
- Path Tracing
- Distance-only intersection paths
- Cached ray marching calculations
- Bounding sphere optimizations
- Faster analytic CSG traversal

the same image now renders in approximately **1 minute 25 seconds**, while using improved lighting and **12 shadow rays** for the main light source.

Before (Skybox more present on Object/ reflection problems):

<img width="599" height="443" alt="BeforeFixes" src="https://github.com/user-attachments/assets/4c69cee4-519b-4fe3-81f1-34c0b30d45c2" />

Material & Light fix:

<img width="1026" height="769" alt="Material LightFix" src="https://github.com/user-attachments/assets/a6d37ddc-21ba-46a0-83a4-6d796d5b44df" />

Ambient Occlusion


---
Deutsch

Java Raytracer

(vlt nochmal final build und so :D idk jetzt)

