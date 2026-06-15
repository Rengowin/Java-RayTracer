package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;

public class Material {
    public Vec3 albedo;
    public double roughness;
    public double metallic;
    public double reflectionStrength;
    public double transparency;
    public double ior;

    public Material(Vec3 albedo, double roughness, double metallic, double reflectionStrength, double transparency, double ior) {
        this.albedo = albedo;
        this.roughness = roughness;
        this.metallic = metallic;
        this.reflectionStrength = reflectionStrength;
        this.transparency = transparency;
        this.ior = ior;
    }
}
