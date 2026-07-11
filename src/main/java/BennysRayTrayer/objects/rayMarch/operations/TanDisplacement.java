package BennysRayTrayer.objects.rayMarch.operations;

import BennysRayTrayer.core.Vec3;

public class TanDisplacement implements Displacement{
    private final double amplitude;
    private final double frequency;

    public TanDisplacement(double amplitude, double frequency) {
        this.amplitude = amplitude;
        this.frequency = frequency;
    }

    @Override
    public double apply(Vec3 point, double baseSdf) {
        double d = Math.tan(point.x * frequency)
                + Math.tan(point.y * frequency)
                + Math.tan(point.z * frequency);

        return baseSdf + d * amplitude;
    }
}
