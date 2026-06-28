package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Hit;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.scene.Camera;
import BennysRayTrayer.scene.Scene;

public class RayTracer {

    //TODO: SHADOWRAY FIXEN (bei denn csg)
    //scahuen ob nur geht wenn der startpunkt aushalb liegt
    //bei einer spehere einen cylinder

    static int depth = 3;

    public static void setDepth(int depth) {
        RayTracer.depth = depth;
    }
    public static int getDepth() {
        return depth;
    }

    /*public static void render(int resX, int resY, Scene scene, int[] pixels) {
        Camera cam = scene.getCamera();
        Object3D[] objects = scene.getObjects();
        Light[] lights = scene.getLights();
        Vec3 bgColor = scene.getBackgroundColorVec3();

        Vec3 camPos = cam.getPosition();
        for (int y = 0; y < resY; y++) {
            //TODO: das y besser rüber geben
            final int yy = y;
            new Thread(() -> {
                for (int x = 0; x < resX; x++) {
                    Ray ray = cam.generateRay(x, yy, resX, resY);
                    Vec3 pixelColor = traceRay(ray, scene, depth);
                    pixels[yy * resX + x] = colorToPixel(pixelColor);
                }
            }).start();
        }
    }*/

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
        Material mat = hit.object.getMaterial();

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

    private static boolean isInShadow(Vec3 hitPoint, Light light, Object3D[] objects) {
        Vec3 lightDir = light.getPosition().sub(hitPoint);
        double lightDist = lightDir.length();
        Ray shadowRay = new Ray(hitPoint.add(lightDir.normalize().mul(0.0001f)), lightDir.normalize());

        for (Object3D object : objects) {
            Hit hit = object.intersect(shadowRay);
            if (hit != null && hit.t > 0 && hit.t < lightDist) {
                Material mat = hit.object.getMaterial();

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

        if (hit == null) {
            return scene.getBackgroundColorVec3();
        }

        Vec3 localColor = shade(scene.getCamera(), hit, scene.getLights(), scene.getObjects());

        if (depth <= 0) {
            return localColor;
        }

        Material mat = hit.object.getMaterial();

        if (mat == null) {
            return localColor;
        }

        Vec3 I = ray.direction.normalize();
        Vec3 N = hit.normal.normalize();

        double n1 = 1.0;
        double n2 = mat.refractiveIndex;

        if (I.dot(N) > 0) {
            N = N.mul(-1);
            n1 = mat.refractiveIndex;
            n2 = 1.0;
        }

        Vec3 result = localColor;

        if (mat.reflectionStrength > 0) {
            Vec3 reflectDir = reflect(I, N);

            Ray reflectRay = new Ray(
                    hit.position.add(reflectDir.mul(0.001f)),
                    reflectDir
            );

            Vec3 reflectColor = traceRay(reflectRay, scene, depth - 1);

            result = result.mul((float)(1.0 - mat.reflectionStrength))
                    .add(reflectColor.mul((float)mat.reflectionStrength));
        }

        if (mat.transparency > 0) {
            Vec3 refractDir = refract(I, N, n1, n2);

            if (refractDir != null) {
                Ray refractRay = new Ray(
                        hit.position.add(refractDir.mul(0.001f)),
                        refractDir
                );

                Vec3 refractColor = traceRay(refractRay, scene, depth - 1);

                result = result.mul((float)(1.0 - mat.transparency))
                        .add(refractColor.mul((float)mat.transparency));
            }
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
}
