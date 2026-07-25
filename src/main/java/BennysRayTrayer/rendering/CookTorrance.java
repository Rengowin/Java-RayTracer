package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;

public final class CookTorrance {

    private static final float MIN_ROUGHNESS = 0.04f;
    private static final float EPSILON = 1e-6f;

    private CookTorrance() {
        // Utility-Klasse
    }

    public static Vec3 shade(
            Material material,
            Vec3 normal,
            Vec3 viewDirection,
            Vec3 lightDirection,
            Vec3 lightColor,
            double lightIntensity
    ) {
        Vec3 N = normal.normalize();
        Vec3 V = viewDirection.normalize();
        Vec3 L = lightDirection.normalize();

        float nDotL = saturate(N.dot(L));
        float nDotV = saturate(N.dot(V));

        if (nDotL <= 0.0f || nDotV <= 0.0f) {
            return new Vec3(0.0f);
        }

        Vec3 halfVector = V.add(L);

        // V und L können exakt entgegengesetzt sein.
        if (halfVector.length() <= EPSILON) {
            return new Vec3(0.0f);
        }

        Vec3 H = halfVector.normalize();

        float nDotH = saturate(N.dot(H));

        float roughness = Math.max(
                MIN_ROUGHNESS,
                saturate((float) material.roughness)
        );

        float metallic = saturate((float) material.metallic);

        float distribution = distributionGGX(
                nDotH,
                roughness
        );

        float geometry = geometrySmith(
                nDotV,
                nDotL,
                roughness
        );

        Vec3 f0 = Fresnel.baseReflectance(material);
        Vec3 fresnel = Fresnel.schlick(nDotV, f0);

        Vec3 specular = fresnel.mul(
                distribution * geometry
        );

        Vec3 reflectedFraction = clamp01(specular);

        Vec3 diffuseFraction = new Vec3(1.0f)
                .sub(reflectedFraction)
                .mul(1.0f - metallic);

        Vec3 diffuse = multiply(
                diffuseFraction,
                material.albedo
        );

        Vec3 brdf = diffuse.add(specular);

        Vec3 radiance = lightColor.mul(
                (float) lightIntensity
        );

        return multiply(brdf, radiance)
                .mul(nDotL);
    }

    private static float distributionGGX(
            float nDotH,
            float roughness
    ) {
        float roughnessSquared = roughness * roughness;

        float denominatorPart =
                nDotH * nDotH
                        * (roughnessSquared - 1.0f)
                        + 1.0f;

        float denominator =
                (float) Math.PI
                        * denominatorPart
                        * denominatorPart;

        return roughnessSquared
                / Math.max(denominator, EPSILON);
    }

    private static float geometrySmith(
            float nDotV,
            float nDotL,
            float roughness
    ) {
        float geometryView = geometrySchlickGGX(
                nDotV,
                roughness
        );

        float geometryLight = geometrySchlickGGX(
                nDotL,
                roughness
        );

        return geometryView * geometryLight;
    }

    private static float geometrySchlickGGX(
            float nDotDirection,
            float roughness
    ) {
        float k = roughness / 2.0f;

        float denominator =
                nDotDirection * (1.0f - k) + k;

        return nDotDirection
                / Math.max(denominator, EPSILON);
    }

    private static Vec3 multiply(Vec3 a, Vec3 b) {
        return new Vec3(
                a.x * b.x,
                a.y * b.y,
                a.z * b.z
        );
    }

    private static Vec3 clamp01(Vec3 value) {
        return new Vec3(
                saturate(value.x),
                saturate(value.y),
                saturate(value.z)
        );
    }

    private static float saturate(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}