package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Hit;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.scene.Camera;
import BennysRayTrayer.scene.Scene;

public class RayTracer {

    static int depth = 1;

    public static void setDepth(int depth) {
        RayTracer.depth = depth;
    }
    public static int getDepth() {
        return depth;
    }

    public static void render(int resX, int resY, Scene scene, int[] pixels) {
        Camera cam = scene.getCamera();
        Object3D[] objects = scene.getObjects();
        Light[] lights = scene.getLights();
        Vec3 bgColor = scene.getBackgroundColor();

        Vec3 camPos = cam.getPosition();
        for (int y = 0; y < resY; y++) {
            for (int x = 0; x < resX; x++) {
                Ray ray = cam.generateRay(x, y, resX, resY);
                Vec3 pixelColor = traceRay(ray, scene, depth);
                pixels[y * resX + x] = colorToPixel(pixelColor);
            }
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
        Vec3 N = hit.normal;
        Vec3 V = cam.getPosition().sub(hitPoint).normalize();

        Vec3 pixelColor = new Vec3(0, 0, 0);
        Material mat = hit.object.getMaterial();

        for (Light light : lights) {
            Vec3 L = light.getPosition().sub(hitPoint).normalize();

            // Prüfe, ob der Punkt im Schatten liegt
            if (!isInShadow(hitPoint, light, objects)) {
                if (mat != null) {
                    Vec3 lightColor = light.getColor().length() > 0 ? light.getColor() : new Vec3(1, 1, 1);
                    Vec3 contrib = CookTorrance.shade(
                            mat.albedo, mat.roughness, mat.metallic,
                            N, V, L, lightColor, light.getIntensity()
                    );
                    pixelColor = pixelColor.add(contrib);
                } else {
                    float intensity = Math.max(0, N.dot(L)) * (float) light.getIntensity();
                    pixelColor = pixelColor.add(hit.object.getColor().mul(intensity));
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
                return true;
            }
        }
        return false;
    }

    private static Vec3 traceRay(Ray ray, Scene scene, int depth) {

        Hit hit = findClosestHit(ray, scene.getObjects());

        if (hit == null) {
            return scene.getBackgroundColor();
        }

        if (depth <= 0) {
            return new Vec3(0, 0, 0);
        }

        Vec3 localColor = shade(scene.getCamera(), hit, scene.getLights(), scene.getObjects());

        if (depth == 1) return localColor;

        Material mat = hit.object.getMaterial();
        float reflectionStrength = 0.1f; // erstmal testweise hart

        Vec3 I = ray.direction.normalize();
        Vec3 N = hit.normal.normalize();

        Vec3 R = I.sub(N.mul(2.0f * I.dot(N))).normalize();

        Ray reflectionRay = new Ray(
                hit.position.add(N.mul(0.001f)), // kleiner Offset gegen Self-Hit
                R
        );

        if (mat == null || mat.reflectionStrength <= 0 || depth <= 1) {
            return localColor;
        }

        Vec3 reflectionColor = traceRay(reflectionRay, scene, depth - 1);

        return localColor.mul(1.0f - reflectionStrength)
                .add(reflectionColor.mul(reflectionStrength));
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
}
