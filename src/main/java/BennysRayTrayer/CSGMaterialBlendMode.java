package BennysRayTrayer;

public enum CSGMaterialBlendMode {
    USE_A,              // Nur A
    USE_B,              // Nur B
    BLEND,              // 50/50 Mix
    NEAREST,            // Je nachdem welche SDF näher (basierend auf d1, d2)
    HARD_NEAREST,       // Rasant wechsel zu näherem
    SMOOTH_BLEND,       // Smooth Interpolation basierend auf Blend-Faktor
    PRESERVE_MATERIALS
}
