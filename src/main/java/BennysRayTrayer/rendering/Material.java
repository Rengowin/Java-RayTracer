package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;

public class Material {
    public Vec3 albedo;
    public double roughness;
    public double metallic;
    public double reflectionStrength;
    public double transparency;
    public double refractiveIndex; //ior

    public Material(Vec3 albedo, double roughness, double metallic, double reflectionStrength, double transparency, double refractiveIndex) {
        this.albedo = albedo;
        this.roughness = roughness;
        this.metallic = metallic;
        this.reflectionStrength = reflectionStrength;
        this.transparency = transparency;
        this.refractiveIndex = refractiveIndex;
    }
}
