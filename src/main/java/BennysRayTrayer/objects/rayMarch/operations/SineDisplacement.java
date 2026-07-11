package BennysRayTrayer.objects.rayMarch.operations;

import BennysRayTrayer.core.Vec3;

public class SineDisplacement implements Displacement{

    private final double amplitude;
    private final double frequency;

    public SineDisplacement(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    @Override
    public double apply(Vec3 point, double baseSdf) {
        double d = Math.sin(point.x * frequency)
                + Math.sin(point.y * frequency)
                + Math.sin(point.z * frequency);

        return baseSdf + d * amplitude;
    }
}
