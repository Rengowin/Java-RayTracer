Java Raytracer (Studie Projekt)
(Below German :D)
(and below below is picturs of development/ imporments/ bug fixes :D, that happend while i was working on it)

About the Project
While i took the Course at the HTW Berlin 

Final Image
<img width="1917" height="1107" alt="FinalImageOfTheRayTracerProjekt" src="https://github.com/user-attachments/assets/33bee32f-150b-475c-97b3-ed99f63374b0" />

Features:
- Analytic Objects
  - CSG
  - Quadric
- RayMarch
  - CSG (Smooth) 
- PathTracing
  - SoftShadows
  - Ambient Occlution
- SkyBox (computet)
- Fog 
- 

---
Deutsch

Java Raytracer

(vlt nochmal final build und so :D idk jetzt)

---
Development

Fog (10 times Stronger and less pixel then on the final Image :D):
(img)
<img width="1018" height="768" alt="FogPNG" src="https://github.com/user-attachments/assets/fbd276c5-4770-4306-9db1-1b2d8c3ec332" />

Skybox(only skybox)
<img width="1022" height="768" alt="SkyBoxPNG" src="https://github.com/user-attachments/assets/db02fc86-7352-4c05-bc92-38678db7018c" />

RayMarchObject (weil eigendlich sieht man das ja eigendlich in final Image sowie das analytische)

Imporments (here i kept trac with AI that i dont miss somethink/ gave me idears how i can imporev render time)
Before anythink my render time for an image with 4 shadow Rays and 1024*768 Pixel it took 6mins
and with first improments like material, also an rework to cookTorance and with Pathtracing (wasnt in before :D) it takes for the same size 1:25 mins and now they is a main light (this is now only casting shadows with 12 Rays) insteat 4 that before where used for shaadows
