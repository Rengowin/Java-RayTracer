package BennysRayTrayer.objects;

import BennysRayTrayer.core.Vec3;

/**
 * Color value object. Use instances when you want a typed color, or
 * call {@link #toVec3()} to get a {@link Vec3} compatible with the
 * rest of the codebase.
 */
public class Color {

    public final float r;
    public final float g;
    public final float b;

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Vec3 toVec3() {
        return new Vec3(r, g, b);
    }

    // Common color factories (return Color instances)
    public static Color red()     { return new Color(1f, 0f, 0f); }
    public static Color green()   { return new Color(0f, 1f, 0f); }
    public static Color blue()    { return new Color(0f, 0f, 1f); }
    public static Color white()   { return new Color(1f, 1f, 1f); }
    public static Color black()   { return new Color(0f, 0f, 0f); }
    public static Color yellow()  { return new Color(1f, 1f, 0f); }
    public static Color cyan()    { return new Color(0f, 1f, 1f); }
    public static Color magenta() { return new Color(1f, 0f, 1f); }
    public static Color orange()  { return new Color(1f, 0.5f, 0f); }
    public static Color gray()    { return new Color(0.5f, 0.5f, 0.5f); }

    public static Color of(float r, float g, float b) {
        return new Color(r, g, b);
    }

    public static Color ofRGB(int r, int g, int b) {
        float sr = Math.max(0, Math.min(255, r)) / 255.0f;
        float sg = Math.max(0, Math.min(255, g)) / 255.0f;
        float sb = Math.max(0, Math.min(255, b)) / 255.0f;

        // sRGB ungefähr in lineare Lichtenergie umwandeln
        return new Color(
                (float) Math.pow(sr, 2.2),
                (float) Math.pow(sg, 2.2),
                (float) Math.pow(sb, 2.2)
        );
    }

    @Override
    public String toString() {
        return "Color{" + r + "," + g + "," + b + "}";
    }

    public static Vec3 blendColors(Vec3 colorA, Vec3 colorB, double weight) {
        if (colorA == null && colorB == null) return null;
        if (colorA == null) return colorB;
        if (colorB == null) return colorA;

        weight = Math.max(0, Math.min(1, weight));
        return colorA.mul((float)weight).add(colorB.mul((float)(1.0 - weight)));
    }

    public Vec3 blendColors(Color colorA, Color colorB, double weight) {
        return blendColors(colorA.toVec3(), colorB.toVec3(), weight);
    }
}
