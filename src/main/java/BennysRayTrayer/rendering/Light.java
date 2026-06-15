package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;

public class Light {

    Vec3 position;
    double intensity;
    Vec3 color;

    public Light(Vec3 position, double intensity, Vec3 color) {
        this.position = position;
        this.intensity = intensity;
        this.color = color;
    }

    public Light(Vec3 position, double intensity) {
        this.position = position;
        this.intensity = intensity;
        this.color = new Vec3(1, 1, 1); // weiß als Standard
    }

    public Vec3 getPosition() {
        return position;
    }
    public double getIntensity() {
        return intensity;
    }
    public Vec3 getColor() {
        return color;
    }

}
