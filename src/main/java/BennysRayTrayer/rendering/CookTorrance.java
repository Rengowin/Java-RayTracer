package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;

public class CookTorrance {

    public static Vec3 shade(
            Vec3 albedo,
            double roughness,
            double metallic,
            Vec3 N,
            Vec3 V,
            Vec3 L,
            Vec3 lightColor,
            double lightIntensity
    ) {
        float r = clamp01((float) roughness);
        float m = clamp01((float) metallic);

        // Verhindert extreme Spitzen bei roughness = 0
        r = Math.max(r, 0.04f);

        N = N.normalize();
        V = V.normalize();
        L = L.normalize();

        float NdotL = Math.max(0.0f, N.dot(L));
        float NdotV = Math.max(0.0f, N.dot(V));

        if (NdotL <= 0.0f || NdotV <= 0.0f) {
            return new Vec3(0, 0, 0);
        }

        Vec3 H = V.add(L).normalize();

        float NdotH = Math.max(0.0f, N.dot(H));

        // D: GGX
        double r2 = r * r;

        double denominator =
                Math.PI
                        * Math.pow(
                        NdotH * NdotH * (r2 - 1.0) + 1.0,
                        2.0
                );

        double D = r2 / Math.max(denominator, 1e-6);

        // F: Fresnel-Schlick + Metall-Trick
        Vec3 dielectricF0 = new Vec3(0.04f);
        Vec3 F0 = lerp(dielectricF0, albedo, m);
        Vec3 F = fresnelSchlick(NdotV, F0);

        // G: Schlick-GGX nach Vorlesungsvariante
        float k = r / 2.0f;

        float gV = NdotV /
                Math.max(NdotV * (1.0f - k) + k, 1e-6f);

        float gL = NdotL /
                Math.max(NdotL * (1.0f - k) + k, 1e-6f);

        float G = gV * gL;

        // ks = D * F * G
        Vec3 ks = F.mul((float) (D * G));
        ks = clampVec01(ks);

        // kd = (1 - ks) * (1 - metallic)
        Vec3 kd = new Vec3(
                (1.0f - ks.x) * (1.0f - m),
                (1.0f - ks.y) * (1.0f - m),
                (1.0f - ks.z) * (1.0f - m)
        );

        Vec3 diffuse = hadamard(kd, albedo);

        Vec3 brdf = diffuse.add(ks);

        Vec3 radiance = lightColor.mul((float) lightIntensity);

        return hadamard(brdf, radiance).mul(NdotL);
    }

    private static Vec3 fresnelSchlick(float cosTheta, Vec3 F0) {
        float value = (float) Math.pow(
                Math.max(0.0f, 1.0f - cosTheta),
                5.0
        );

        return F0.add(
                new Vec3(1, 1, 1)
                        .sub(F0)
                        .mul(value)
        );
    }

    private static Vec3 lerp(Vec3 a, Vec3 b, float t) {
        return a.mul(1.0f - t).add(b.mul(t));
    }

    private static Vec3 hadamard(Vec3 a, Vec3 b) {
        return new Vec3(
                a.x * b.x,
                a.y * b.y,
                a.z * b.z
        );
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private static Vec3 clampVec01(Vec3 value) {
        return new Vec3(
                clamp01(value.x),
                clamp01(value.y),
                clamp01(value.z)
        );
    }
}