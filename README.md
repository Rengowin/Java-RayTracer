# Java Ray Tracer

A Java-based ray tracer developed as part of the Ray Tracing course at HTW Berlin.  
The project evolved from a recursive ray tracer into a renderer featuring path tracing, ray marching, constructive solid geometry, physically based shading, and performance optimizations.

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
- Spheres
- Quadrics
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

- Implemented both analytic and ray-marched object intersection systems
- Designed a physically based shading model using Cook-Torrance BRDF
- Added acceleration strategies to reduce render time significantly
- Balanced image quality and performance through sampling and multithreading


## Performance

Rendering a 1024×768 image:

| Version | Time |
|---------|------:|
| Initial implementation | ~6 min |
| Final implementation | ~1 min 25 s |


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

## Future Improvements

- Russian roulette termination
- Texture mapping
- Animation
- Color variation effects
