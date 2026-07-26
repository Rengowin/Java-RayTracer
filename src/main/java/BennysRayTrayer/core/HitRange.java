package BennysRayTrayer.core;

public class HitRange {

    public final double tEnter;
    public final double tExit;

    public HitRange(
            double tEnter,
            double tExit
    ) {
        this.tEnter = tEnter;
        this.tExit = tExit;
    }

    public boolean isValid() {
        return Double.isFinite(tEnter)
                && Double.isFinite(tExit)
                && tExit >= tEnter;
    }
}
