package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;

public class Light {

    private final Vec3 position;
    private final double intensity;
    private final Color color;

    private final double radius;
    private final int shadowSamples;

    public Light(Vec3 position, double intensity, Color color, double radius, int shadowSamples) {
        this.position = position;
        this.intensity = intensity;
        this.color = color;
        this.radius = radius;
        this.shadowSamples = shadowSamples;
    }

    public Light(Vec3 position, double intensity, Color color) {
        this(position, intensity, color, 0.0, 0);
    }

    public Light(Vec3 position, double intensity, double radius, int shadowSamples) {
        this(position, intensity, Color.white(), radius, shadowSamples);
    }

    public Light(Vec3 position, double intensity) {
        this(position, intensity, Color.white(), 0.0, 0);
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
    public double getRadius() {
        return radius;
    }
    public int getShadowSamples() {
        return shadowSamples;
    }
    public boolean castsShadow() {
        return shadowSamples > 0;
    }
    public boolean isAreaLight() {
        return radius > 0.0;
    }

}
