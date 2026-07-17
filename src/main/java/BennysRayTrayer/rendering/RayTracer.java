package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Hit;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.scene.Camera;
import BennysRayTrayer.scene.Scene;

public class RayTracer {

    static int depth = 3;

    static boolean useFog = false;
    static float fogDensity = 0.005f;

    public static void setDepth(int depth) {
        RayTracer.depth = depth;
    }
    public static int getDepth() {
        return depth;
    }

    public static void render(int resX, int resY, Scene scene, int[] pixels) {
        Camera cam = scene.getCamera();

        int cores = Runtime.getRuntime().availableProcessors();

        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(cores)) {
            java.util.List<java.util.concurrent.Callable<Void>> jobs = new java.util.ArrayList<>();

            for (int y = 0; y < resY; y++) {
                final int yy = y;

                jobs.add(() -> {
                    for (int x = 0; x < resX; x++) {
                        Ray ray = cam.generateRay(x, yy, resX, resY);
                        Vec3 pixelColor = traceRay(ray, scene, depth);
                        pixels[yy * resX + x] = colorToPixel(pixelColor);
                    }
                    return null;
                });
            }

            executor.invokeAll(jobs); // wartet bis ALLE Zeilen fertig sind
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Hit findClosestHit(Ray ray, Object3D[] objects) {
        double closestT = Double.MAX_VALUE;
        Hit closestHit = null;

        for (Object3D object : objects) {
            Hit hit = object.intersect(ray);
            if (hit != null && hit.t < closestT) {
                closestT = hit.t;
                closestHit = hit;
            }
        }

        return closestHit;
    }

    private static Vec3 shade(Camera cam, Hit hit, Light[] lights, Object3D[] objects) {
        Vec3 hitPoint = hit.position;
        Vec3 N = hit.normal.normalize();
        Vec3 V = cam.getPosition().sub(hitPoint).normalize();

        if (N.dot(V) < 0) {
            N = N.mul(-1);
        }

        Vec3 pixelColor = new Vec3(0.08f, 0.08f, 0.08f);
        Material mat = hit.object.getMaterialAt(hitPoint);

        for (Light light : lights) {
            Vec3 L = light.getPosition().sub(hitPoint).normalize();

            // Prüfe, ob der Punkt im Schatten liegt
            if (!isInShadow(hitPoint, light, objects)) {
                if (mat != null) {
                    Vec3 lightColor = light.getColorVec3();
                    if (lightColor == null || lightColor.length() == 0) {
                        lightColor = new Vec3(1, 1, 1);
                    }
                    Vec3 contrib = CookTorrance.shade(
                            mat.albedo, mat.roughness, mat.metallic,
                            N, V, L, lightColor, light.getIntensity()
                    );
                    pixelColor = pixelColor.add(contrib);
                } else {
                    float intensity = Math.max(0, N.dot(L)) * (float) light.getIntensity();
                    Vec3 objColor = hit.object.getColorVec3();
                    if (objColor != null) {
                        pixelColor = pixelColor.add(objColor.mul(intensity));
                    }
                }
            }
        }

        return pixelColor;
    }

    private static boolean isInShadow(
            Vec3 hitPoint,
            Light light,
            Object3D[] objects
    ) {
        Vec3 lightDir = light.getPosition().sub(hitPoint);
        double lightDist = lightDir.length();

        Ray shadowRay = new Ray(
                hitPoint.add(lightDir.normalize().mul(0.0001f)),
                lightDir.normalize()
        );

        for (Object3D object : objects) {
            Hit hit = object.intersect(shadowRay);

            if (hit != null && hit.t > 0 && hit.t < lightDist) {
                Material mat = hit.object.getMaterialAt(hit.position);

                if (mat != null && mat.transparency > 0.5) {
                    continue;
                }

                return true;
            }
        }

        return false;
    }

    private static Vec3 traceRay(Ray ray, Scene scene, int depth) {
        Hit hit = findClosestHit(ray, scene.getObjects());

        Vec3 skydome = skyDome(ray.direction);

        if (hit == null) {
            return skydome;
        }

        Vec3 fogColor = skydome.mul(0.75f);

        Vec3 localColor = shade(
                scene.getCamera(),
                hit,
                scene.getLights(),
                scene.getObjects()
        );

        Vec3 result = localColor;
        Material mat = hit.object.getMaterialAt(hit.position);

        if (depth > 0 && mat != null) {

            Vec3 I = ray.direction.normalize();
            Vec3 N = hit.normal.normalize();

            double n1 = 1.0;
            double n2 = mat.refractiveIndex;

            if (I.dot(N) > 0) {
                N = N.mul(-1);
                n1 = mat.refractiveIndex;
                n2 = 1.0;
            }

            if (mat.reflectionStrength > 0) {
                Vec3 reflectDir = reflect(I, N);

                Ray reflectRay = new Ray(
                        hit.position.add(reflectDir.mul(0.001f)),
                        reflectDir
                );

                Vec3 reflectColor = traceRay(reflectRay, scene, depth - 1);

                result = result.mul((float) (1.0 - mat.reflectionStrength))
                        .add(reflectColor.mul((float) mat.reflectionStrength));
            }

            if (mat.transparency > 0) {
                Vec3 refractDir = refract(I, N, n1, n2);

                if (refractDir != null) {
                    Ray refractRay = new Ray(
                            hit.position.add(refractDir.mul(0.001f)),
                            refractDir
                    );

                    Vec3 refractColor = traceRay(refractRay, scene, depth - 1);

                    result = result.mul((float) (1.0 - mat.transparency))
                            .add(refractColor.mul((float) mat.transparency));
                }
            }
        }

        if (useFog) {
            result = applyFog(
                    result,
                    hit.t,
                    fogColor,
                    fogDensity
            );
        }

        return result;
    }

    private static int colorToPixel(Vec3 color) {
        // Tone-Mapping: Reinhard (verhindert Überbelichtung)
        float r = color.x / (1.0f + color.x);
        float g = color.y / (1.0f + color.y);
        float b = color.z / (1.0f + color.z);

        // Gamma-Korrektur
        float gamma = 1.0f / 2.2f;
        r = (float) Math.pow(r, gamma);
        g = (float) Math.pow(g, gamma);
        b = (float) Math.pow(b, gamma);

        int ri = (int) Math.min(255, r * 255);
        int gi = (int) Math.min(255, g * 255);
        int bi = (int) Math.min(255, b * 255);

        return (ri << 16) | (gi << 8) | bi;
    }

    public static Vec3 reflect(Vec3 I, Vec3 N) {
        return I.sub(N.mul(2.0f * I.dot(N))).normalize();
    }

    public static Vec3 refract(Vec3 I, Vec3 N, double n1, double n2) {
        float eta = (float) (n1 / n2);
        float a = -I.dot(N);
        float k = 1.0f - eta * eta * (1.0f - a * a);
        if(k < 0){
            return null;
        }
        float b = (float) Math.sqrt(k);

        return I.mul(eta).add(N.mul(eta * a - b)).normalize();
    }

    public static Vec3 applyFog(Vec3 color, double distance, Vec3 fogColor, double fogDensity){
        float visibility = (float) Math.exp(-distance * fogDensity);
        return fogColor.mul(1.0f - visibility).add(color.mul(visibility));
    }

    public static Vec3 skyDome(Vec3 direction) {

        //Problem gewesen weil cam winkel xD
        return new Vec3(0.0f, 0.0f, 0.0f);

        /*Vec3 dir = direction.normalize();

        float t = (dir.y + 0.25f) / 0.55f;
        t = Math.max(0.0f, Math.min(1.0f, t));
        t = t * t * (3.0f - 2.0f * t);

        Vec3 horizon = new Vec3(1.4f, 0.55f, 0.18f);
        Vec3 zenith  = new Vec3(0.02f, 0.08f, 2.4f);

        Vec3 sky = horizon.mul(1.0f - t)
                .add(zenith.mul(t));

        Vec3 sunDirection =
                new Vec3(0.0f, -0.12f, -1.0f).normalize();

        float sunAmount =
                Math.max(0.0f, dir.dot(sunDirection));

        float sunGlow =
                (float) Math.pow(sunAmount, 12.0);

        float sunDisc =
                (float) Math.pow(sunAmount, 500.0);

        Vec3 glowColor =
                new Vec3(1.8f, 0.8f, 0.25f);

        Vec3 discColor =
                new Vec3(12.0f, 9.0f, 5.0f);

        return sky
                .add(glowColor.mul(sunGlow))
                .add(discColor.mul(sunDisc));*/
    }
}
