package BennysRayTrayer.objects.rayMarch.operations;

import BennysRayTrayer.core.Vec3;

public class CoseDisplacement implements Displacement {

    private final double amplitude;
    private final double frequency;

    public CoseDisplacement(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    @Override
    public double apply(Vec3 point, double baseSdf) {
        double d = Math.cos(point.x * frequency)
                + Math.cos(point.y * frequency)
                + Math.cos(point.z * frequency);

        return baseSdf + d * amplitude;
    }
}
