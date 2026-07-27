package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Hit;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.scene.Camera;
import BennysRayTrayer.scene.Scene;

import java.util.Random;

public class RayTracer {

    static int depth = 3;

    static boolean useFog = true;
    static float fogDensity = 0.005f;

    static int samplesPerPixel = 24;

    //AO Settings
    static boolean useAmbientOcclusion = true;

    static int ambientOcclusionSamples = 8;
    static float ambientOcclusionDistance = 2.0f;
    static float ambientOcclusionStrength = 1.0f;

    public static void render(int resX, int resY, Scene scene, int[] pixels) {
        Camera cam = scene.getCamera();

        int cores = Runtime.getRuntime().availableProcessors();

        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(cores)) {
            java.util.List<java.util.concurrent.Callable<Void>> jobs = new java.util.ArrayList<>();

            for (int y = 0; y < resY; y++) {
                final int yy = y;

                jobs.add(() -> {
                    for (int x = 0; x < resX; x++) {

                        Vec3 pixelColor = new Vec3(0.0f);

                        for (int sample = 0; sample < samplesPerPixel; sample++) {
                            Random random = new Random(
                                    SampleSeed.createSeed(x, yy, sample)
                            );

                            float jitterX = random.nextFloat();
                            float jitterY = random.nextFloat();

                            Ray ray = cam.generateRay(
                                    x+jitterX,
                                    yy+jitterY,
                                    resX,
                                    resY
                            );

                            pixelColor = pixelColor.add(
                                    traceRay(ray, scene, depth, random)
                            );
                        }

                        pixelColor = pixelColor.mul(
                                1.0f / samplesPerPixel
                        );

                        pixels[yy * resX + x] =
                                colorToPixel(pixelColor);
                    }

                    return null;
                });
            }

            executor.invokeAll(jobs); // wartet bis ALLE Zeilen fertig sind
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Hit findClosestHit(
            Ray ray,
            Object3D[] objects
    ) {
        double closestT = Double.POSITIVE_INFINITY;
        Object3D closestObject = null;

        for (Object3D object : objects) {
            double distance = object.intersectDistance(
                    ray,
                    closestT
            );

            if (distance > 0.0
                    && distance < closestT) {
                closestT = distance;
                closestObject = object;
            }
        }

        if (closestObject == null) {
            return null;
        }

        return closestObject.createHit(
                ray,
                closestT
        );
    }

    private static Vec3 shade(
            Camera cam,
            Hit hit,
            Light[] lights,
            Object3D[] objects,
            Random random,
            float ambientOcclusion
    ) {
        Vec3 hitPoint = hit.position;
        Vec3 normal = hit.normal.normalize();
        Vec3 viewDirection =
                cam.getPosition().sub(hitPoint).normalize();

        if (normal.dot(viewDirection) < 0.0f) {
            normal = normal.mul(-1.0f);
        }

        Material material =
                hit.object.getMaterialAt(hitPoint);

        if (material == null) {
            return new Vec3(0.0f);
        }

        Vec3 result = new Vec3(
                0.08f,
                0.08f,
                0.08f
        ).mul(ambientOcclusion);

        for (Light light : lights) {
            float visibility = shadowVisibility(
                    hitPoint,
                    normal,
                    light,
                    objects,
                    random
            );

            if (visibility <= 0.0f) {
                continue;
            }

            Vec3 lightDirection =
                    light.getPosition()
                            .sub(hitPoint)
                            .normalize();

            Vec3 lightColor = light.getColor().toVec3();

            if (lightColor == null
                    || lightColor.length() == 0.0f) {
                lightColor = new Vec3(1.0f);
            }

            Vec3 contribution =
                    CookTorrance.shade(
                            material,
                            normal,
                            viewDirection,
                            lightDirection,
                            lightColor,
                            light.getIntensity()
                    );

            result = result.add(
                    contribution.mul(visibility)
            );
        }

        return result;
    }

    private static Vec3 traceRay(
            Ray ray,
            Scene scene,
            int depth,
            Random random
    ) {
        if (depth <= 0) {
            return new Vec3(0.0f);
        }

        Hit hit = findClosestHit(
                ray,
                scene.getObjects()
        );

        Vec3 skyColor = skyDome(ray.direction);

        if (hit == null) {
            return skyColor;
        }

        Vec3 indirectLight = indirectBounce(
                ray,
                hit,
                scene,
                depth,
                random
        );

        float ao = ambientOcclusion(
                ray,
                hit,
                scene.getObjects(),
                random
        );

        Vec3 directLight = shade(
                scene.getCamera(),
                hit,
                scene.getLights(),
                scene.getObjects(),
                random,
                ao
        );

        float indirectAo =
                1.0f - 0.5f * (1.0f - ao);

        Vec3 result = directLight.add(
                indirectLight.mul(indirectAo)
        );

        if (useFog) {
            Vec3 fogColor =
                    skyColor.mul(0.75f);

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
        /*return new Vec3(0.0f, 0.0f, 0.0f);*/
        Vec3 dir = direction.normalize();

        float t = (dir.y + 0.25f) / 0.55f;
        t = Math.clamp(t, 0.0f, 1.0f);
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
                .add(discColor.mul(sunDisc));
    }

    private static float shadowVisibility(
            Vec3 hitPoint,
            Vec3 normal,
            Light light,
            Object3D[] objects,
            Random random
    ) {
        if (!light.castsShadow()) {
            return 1.0f;
        }

        if (!light.isAreaLight() || light.getShadowSamples() == 1) {
            return isPointInShadow(
                    hitPoint,
                    normal,
                    light.getPosition(),
                    objects
            ) ? 0.0f : 1.0f;
        }

        int visibleSamples = 0;

        for (int i = 0; i < light.getShadowSamples(); i++) {

            Vec3 offset = randomPointInSphere(random)
                    .mul((float) light.getRadius());

            Vec3 sampledLightPosition = light.getPosition()
                    .add(offset);

            if (!isPointInShadow(
                    hitPoint,
                    normal,
                    sampledLightPosition,
                    objects
            )) {
                visibleSamples++;
            }
        }

        return visibleSamples / (float) light.getShadowSamples();
    }

    private static boolean isPointInShadow(
            Vec3 hitPoint,
            Vec3 normal,
            Vec3 lightPosition,
            Object3D[] objects
    ) {
        Vec3 toLight = lightPosition.sub(hitPoint);
        double lightDistance = toLight.length();
        Vec3 lightDirection = toLight.normalize();

        Ray shadowRay = new Ray(
                // Lieber entlang der Normale verschieben als entlang des Lichtstrahls
                hitPoint.add(normal.mul(0.001f)),
                lightDirection
        );

        for (Object3D object : objects) {
            double distance = object.intersectDistance(
                    shadowRay,
                    lightDistance
            );

            if (!Double.isFinite(distance)
                    || distance <= 0.0
                    || distance >= lightDistance) {
                continue;
            }

            Vec3 shadowHitPosition = shadowRay.origin.add(
                    shadowRay.direction.mul((float) distance)
            );

            Material material =
                    object.getMaterialAt(shadowHitPosition);

            if (material != null
                    && material.transparency > 0.5) {
                continue;
            }

            return true;
        }

        return false;
    }

    private static Vec3 randomPointInSphere(Random random) {
        while (true) {
            Vec3 point = new Vec3(
                    random.nextFloat() * 2.0f - 1.0f,
                    random.nextFloat() * 2.0f - 1.0f,
                    random.nextFloat() * 2.0f - 1.0f
            );

            if (point.length() <= 1.0f) {
                return point;
            }
        }
    }

    private static Vec3 sampleHemisphere(Vec3 normal, Random random) {
        Vec3 n = normal.normalize();

        float u1 = random.nextFloat();
        float u2 = random.nextFloat();

        float r = (float) Math.sqrt(u1);
        float theta = 2.0f * (float) Math.PI * u2;

        float localX = r * (float) Math.cos(theta);
        float localY = r * (float) Math.sin(theta);
        float localZ = (float) Math.sqrt(1.0f - u1);

        Vec3 helper;

        if (Math.abs(n.y) < 0.999f) {
            helper = new Vec3(0.0f, 1.0f, 0.0f);
        } else {
            helper = new Vec3(1.0f, 0.0f, 0.0f);
        }

        Vec3 tangent = helper.cross(n).normalize();
        Vec3 bitangent = n.cross(tangent).normalize();

        return tangent.mul(localX)
                .add(bitangent.mul(localY))
                .add(n.mul(localZ))
                .normalize();
    }

    private static float ambientOcclusion(
            Ray incomingRay,
            Hit hit,
            Object3D[] objects,
            Random random
    ) {
        if (!useAmbientOcclusion
                || ambientOcclusionSamples <= 0) {
            return 1.0f;
        }

        Vec3 normal = hit.normal.normalize();

        if (normal.dot(incomingRay.direction) > 0.0f) {
            normal = normal.mul(-1.0f);
        }

        float distanceSum = 0.0f;

        for (int i = 0; i < ambientOcclusionSamples; i++) {
            Vec3 direction = sampleHemisphere(
                    normal,
                    random
            );

            Ray aoRay = new Ray(
                    hit.position.add(
                            normal.mul(0.001f)
                    ),
                    direction
            );

            double hitDistance = findClosestDistance(
                    aoRay,
                    objects,
                    ambientOcclusionDistance
            );

            if (!Double.isFinite(hitDistance)
                    || hitDistance >= ambientOcclusionDistance) {
                distanceSum += ambientOcclusionDistance;
                continue;
            }

            distanceSum += (float) hitDistance;
        }

        float averageDistance =
                distanceSum / ambientOcclusionSamples;

        float visibility =
                averageDistance / ambientOcclusionDistance;

        visibility = Math.clamp(
                visibility,
                0.0f,
                1.0f
        );

        return 1.0f
                - ambientOcclusionStrength
                * (1.0f - visibility);
    }

    private static Vec3 indirectBounce(
            Ray incomingRay,
            Hit hit,
            Scene scene,
            int depth,
            Random random
    ) {
        Material material =
                hit.object.getMaterialAt(hit.position);

        if (material == null || depth <= 1) {
            return new Vec3(0.0f);
        }

        Vec3 normal = hit.normal.normalize();

        if (normal.dot(incomingRay.direction) > 0.0f) {
            normal = normal.mul(-1.0f);
        }

        Vec3 bounceDirection;

        float r = random.nextFloat();

        float diffuseProbability = (float) (1.0 - material.metallic - material.transparency);
        diffuseProbability = Math.max(0.0f, diffuseProbability);

        float reflectionProbability =
                (float) material.metallic;

        float refractionProbability =
                (float) material.transparency;

        float sum =
                diffuseProbability + reflectionProbability + refractionProbability;

        if (sum <= 0.0f) {
            return new Vec3(0.0f);
        }

        diffuseProbability /= sum;
        reflectionProbability /= sum;
        refractionProbability /= sum;

        if (r < refractionProbability) {

            Vec3 outwardNormal = hit.normal.normalize();

            boolean entering =
                    incomingRay.direction.dot(outwardNormal) < 0.0f;

            double n1;
            double n2;

            if (entering) {
                normal = outwardNormal;
                n1 = 1.0;
                n2 = material.refractiveIndex;
            } else {
                normal = outwardNormal.mul(-1.0f);
                n1 = material.refractiveIndex;
                n2 = 1.0;
            }

            float cosTheta = Math.max(
                    0.0f,
                    -incomingRay.direction.dot(normal)
            );

            float fresnelProbability =
                    Fresnel.schlickDielectric(
                            cosTheta,
                            n1,
                            n2
                    );

            if (random.nextFloat() < fresnelProbability) {
                bounceDirection = reflect(
                        incomingRay.direction,
                        normal
                );
            } else {
                bounceDirection = refract(
                        incomingRay.direction,
                        normal,
                        n1,
                        n2
                );

                if (bounceDirection == null) {
                    bounceDirection = reflect(
                            incomingRay.direction,
                            normal
                    );
                }
            }
        } else if (r < refractionProbability + reflectionProbability) {
            bounceDirection = reflect(
                    incomingRay.direction,
                    normal
            );

        } else {
            bounceDirection = sampleHemisphere(normal, random);
        }

        Ray bounceRay = new Ray(
                hit.position.add(
                        bounceDirection.mul(0.001f)
                ),
                bounceDirection
        );

        Vec3 incomingLight = traceRay(
                bounceRay,
                scene,
                depth - 1,
                random
        );

        return incomingLight.mul(material.albedo);
    }

    private static double findClosestDistance(
            Ray ray,
            Object3D[] objects,
            double maxDistance
    ) {
        double closestDistance = maxDistance;

        for (Object3D object : objects) {
            double distance = object.intersectDistance(
                    ray,
                    closestDistance
            );

            if (distance > 0.0
                    && distance < closestDistance) {
                closestDistance = distance;
            }
        }

        return closestDistance;
    }
/*
    private static boolean russianRoulette(
            Material material,
            int depth,
            Random random
    ){

    }*/
}
