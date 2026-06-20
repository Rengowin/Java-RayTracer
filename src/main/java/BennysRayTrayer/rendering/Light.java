package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;

public class Light {

    Vec3 position;
    double intensity;
    Color color;

    public Light(Vec3 position, double intensity, Color color) {
        this.position = position;
        this.intensity = intensity;
        this.color = color;
    }

    public Light(Vec3 position, double intensity) {
        this.position = position;
        this.intensity = intensity;
        this.color = Color.white(); // weiß als Standard
    }

    public Vec3 getPosition() {
        return position;
    }
    public double getIntensity() {
        return intensity;
    }
    public Color getColor() {
        return color;
    }

    // Backwards-compatible accessor
    public Vec3 getColorVec3() {
        return color == null ? null : color.toVec3();
    }

}
