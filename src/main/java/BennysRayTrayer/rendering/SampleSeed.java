package BennysRayTrayer.rendering;

public final class SampleSeed {

    private static final long BASE_SEED = 25072026; //datum from today

    public static long createSeed(int x, int y, int sample) {

        long seed = BASE_SEED;

        seed ^= (long) x * 0x9E3779B97F4A7C15L;
        seed ^= (long) y * 0xC2B2AE3D27D4EB4FL;
        seed ^= (long) sample * 0x165667B19E3779F9L;

        return mix(seed);
    }

    private static long mix(long value){
        value ^= (value >>> 33);
        value *= 0xff51afd7ed558ccdL;
        value ^= (value >>> 33);
        value *= 0xc4ceb9fe1a85ec53L;
        value ^= (value >>> 33);
        return value;
    }

}
