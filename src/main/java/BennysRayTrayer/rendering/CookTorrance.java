package BennysRayTrayer.rendering;

import BennysRayTrayer.core.Vec3;

//Achtung diese klasse würde mit ki gemacht weil zeit unterschätzt, will ich nochmal neu machen/selbst
// was daraus gelernt immer die schauen das keine folienen datei in downloads ist :D bzw in chache des browers ist weil sonnst die alten folienen gezeigt werden

/**
 * Cook-Torrance Microfacet BRDF
 *
 *   f_r = (D * F * G) / (4 * NdotV * NdotL)
 *
 *   D  = GGX/Trowbridge-Reitz  (Normalverteilung der Microfacets)
 *   F  = Fresnel-Schlick        (Reflektionsanteil je Winkel)
 *   G  = Smith GGX              (Abschattung der Microfacets)
 *
 * Eingaben:
 *   albedo     – Basisfarbe des Materials
 *   roughness  – Rauheit 0 (glatt) … 1 (maximal rau)
 *   metallic   – Metallgrad 0 (Dielektrikum) … 1 (Metall)
 *   N          – normalisierte Oberflächennormale
 *   V          – normalisierter Blickvektor (Hitpunkt → Kamera)
 *   L          – normalisierter Lichtvektor  (Hitpunkt → Licht)
 *   lightColor – RGB-Farbe der Lichtquelle
 *   lightIntensity – Stärke der Lichtquelle
 */
public class CookTorrance {

    public static Vec3 shade(
            Vec3 albedo, double roughness, double metallic,
            Vec3 N, Vec3 V, Vec3 L,
            Vec3 lightColor, double lightIntensity
    ) {
        float NdotL = Math.max(0.0f, N.dot(L));
        if (NdotL <= 0.0f) return new Vec3(0, 0, 0);

        float NdotV = Math.max(1e-4f, N.dot(V));

        Vec3 H = L.add(V).normalize(); // Halbvektor
        float NdotH = Math.max(0.0f, N.dot(H));
        float VdotH = Math.max(0.0f, V.dot(H));

        double alpha  = roughness * roughness;
        double alpha2 = alpha * alpha;

        // ── D: GGX Normal Distribution ─────────────────────────────────────
        double denom = (NdotH * NdotH) * (alpha2 - 1.0) + 1.0;
        double D = alpha2 / (Math.PI * denom * denom);

        // ── F: Fresnel-Schlick ─────────────────────────────────────────────
        // Dielectrics: F0 ≈ 0.04; Metals: F0 = albedo
        Vec3 F0 = lerp(new Vec3(0.04f, 0.04f, 0.04f), albedo, (float) metallic);
        Vec3 F  = fresnelSchlick(VdotH, F0);

        // ── G: Smith GGX (direct lighting) ────────────────────────────────
        double k   = (roughness + 1.0) * (roughness + 1.0) / 8.0;
        double G_V = NdotV / (NdotV * (1.0 - k) + k);
        double G_L = NdotL / (NdotL * (1.0 - k) + k);
        double G   = G_V * G_L;

        // ── Specular lobe ──────────────────────────────────────────────────
        float specFactor = (float) (D * G / Math.max(4.0 * NdotV * NdotL, 1e-6));
        Vec3 specular = mul(F, specFactor);

        // ── Diffuse lobe (Lambertian) ───────────────────────────────────────
        // kD = (1 - F) * (1 - metallic)   →  metals have no diffuse
        Vec3 kD = mul(sub(new Vec3(1, 1, 1), F), (float) (1.0 - metallic));
        Vec3 diffuse = mul(hadamard(kD, albedo), (float) (1.0 / Math.PI));

        // ── Combine ────────────────────────────────────────────────────────
        Vec3 radiance = mul(lightColor, (float) lightIntensity);
        Vec3 BRDF = diffuse.add(specular);

        // Multiply by NdotL and radiance (element-wise for colored light)
        return mul(hadamard(BRDF, radiance), NdotL);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static Vec3 fresnelSchlick(float cosTheta, Vec3 F0) {
        float pow5 = (float) Math.pow(Math.max(0.0f, 1.0f - cosTheta), 5.0);
        return F0.add(sub(new Vec3(1, 1, 1), F0).mul(pow5));
    }

    private static Vec3 lerp(Vec3 a, Vec3 b, float t) {
        return a.mul(1.0f - t).add(b.mul(t));
    }

    /** Component-wise Farb-Multiplikation (Hadamard-Produkt) */
    public static Vec3 hadamard(Vec3 a, Vec3 b) {
        return new Vec3(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    private static Vec3 sub(Vec3 a, Vec3 b) { return a.sub(b); }
    private static Vec3 mul(Vec3 v, float s) { return v.mul(s); }
}

