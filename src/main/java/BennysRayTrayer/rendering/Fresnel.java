package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;

public class Fresnel {
    private static final Vec3 DIELECTRIC_F0 = new Vec3(0.04f);

    private Fresnel() {}

    public static Vec3 baseReflectance(Material material) {
        float metallic = clamp01((float) material.metallic);

        return lerp(
                DIELECTRIC_F0,
                material.albedo,
                metallic
        );
    }

    public static Vec3 schlick(float cosTheta, Vec3 f0) {
        float cos = clamp01(cosTheta);
        float factor = (float) Math.pow(1.0f - cos, 5.0);

        return f0.add(
                new Vec3(1.0f)
                        .sub(f0)
                        .mul(factor)
        );
    }

    public static Vec3 schlick(float cosTheta, Material material) {
        return schlick(cosTheta, baseReflectance(material));
    }

    private static Vec3 lerp(Vec3 a, Vec3 b, float t) {
        return a.mul(1.0f - t).add(b.mul(t));
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}
