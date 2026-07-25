package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;

public class Material {
    public Vec3 albedo;
    public double roughness;
    public double metallic;
    public double transparency;
    public double refractiveIndex;

    public Material(
            Vec3 albedo,
            double roughness,
            double metallic,
            double transparency,
            double refractiveIndex
    ) {
        this.albedo = albedo;
        this.roughness = roughness;
        this.metallic = metallic;
        this.transparency = transparency;
        this.refractiveIndex = refractiveIndex;
    }

    public static Material blend(Material matA, Material matB, double weight){
        if(matA == null && matB == null) return null;
        if(matA == null) return matB;
        if(matB == null) return matA;

        weight = Math.max(0, Math.min(1, weight));
        double w = 1 - weight;

        return new Material(
                matA.albedo.mul((float) weight)
                        .add(matB.albedo.mul((float) w)),
                matA.roughness * weight + matB.roughness * w,
                matA.metallic * weight + matB.metallic * w,
                matA.transparency * weight + matB.transparency * w,
                matA.refractiveIndex * weight + matB.refractiveIndex * w
        );
    }

}
