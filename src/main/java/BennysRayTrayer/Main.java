package BennysRayTrayer;

import BennysRayTrayer.core.*;
import BennysRayTrayer.objects.*;
import BennysRayTrayer.objects.Normal.AnalyticObject;
import BennysRayTrayer.objects.Normal.HalfSpace;
import BennysRayTrayer.objects.Normal.Quadric;
import BennysRayTrayer.objects.Normal.csg.Cut;
import BennysRayTrayer.objects.Normal.csg.Diff;
import BennysRayTrayer.objects.Normal.csg.Union;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG.*;
import BennysRayTrayer.objects.rayMarch.primitives.*;
import BennysRayTrayer.rendering.*;
import BennysRayTrayer.scene.*;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import static BennysRayTrayer.CSGMaterialBlendMode.*;

public class Main {


    //TODO: Anschauen warum Rotation bei CUT uns so "komisch" ist (also ration geht schon irgendwie aber der "inhalt" von den cut und diffs dreht sich nicht wie gewünscht

    public static void main(String[] args) {
        // === Fenster erstellen ===
        int resX = 1024;
        int resY = 768;

        int[] pixels = new int[resX * resY];

        MemoryImageSource mis = new MemoryImageSource(resX, resY, new DirectColorModel(24, 0xff0000, 0xff00, 0xff), pixels, 0, resX);
        mis.setAnimated(true);
        Image image = Toolkit.getDefaultToolkit().createImage(mis);

        JFrame frame = new JFrame("Benny's RayTrayer");
        frame.add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // === Scene erstellen ===
        Camera cam = new Camera(
                new Vec3(0, 5f, 10),
                60,
                new Vec3(0, -0.2f, -1)
        );
        cam.rotatePitch(-20);

        //topdown view
        Camera cam1 = new Camera(
                new Vec3(0, 10, 0),
                60,
                new Vec3(0, -1, 0)
        );
        cam1.rotatePitch(-180);


        float petalDistance = 2.0f;
        float smallPetalDistance = 2.1f;
        float smallPetalY = -0.75f;

        Object3D p0 = createCanonicalLargePetal();
        p0.setRotation(new Vec3(0, 0, 0));
        p0.setPosition(positionOnRing(0, petalDistance, 0));

        Object3D p45 = createSmallPetal();
        p45.setRotation(new Vec3(0, -45, 0));
        p45.setPosition(positionOnRing(45, smallPetalDistance, smallPetalY));

        Object3D p90 = createCanonicalLargePetal();
        p90.setRotation(new Vec3(0, 90, 0));
        p90.setPosition(positionOnRing(90, petalDistance, 0));

        Object3D p135 = createSmallPetal();
        p135.setRotation(new Vec3(0, 45, 0));
        p135.setPosition(positionOnRing(135, smallPetalDistance, smallPetalY));

        Object3D p180 = createCanonicalLargePetal();
        p180.setRotation(new Vec3(0, 180, 0));
        p180.setPosition(positionOnRing(180, petalDistance, 0));

        Object3D p225 = createSmallPetal();
        p225.setRotation(new Vec3(0, -225, 0));
        p225.setPosition(positionOnRing(225, smallPetalDistance, smallPetalY));

        Object3D p270 = createCanonicalLargePetal();
        p270.setRotation(new Vec3(0, 270, 0));
        p270.setPosition(positionOnRing(270, petalDistance, 0));

        Object3D p315 = createSmallPetal();
        p315.setRotation(new Vec3(0, 225, 0));
        p315.setPosition(positionOnRing(315, smallPetalDistance, smallPetalY));

        Object3D innerStuff = createMidPart();
        innerStuff.setPosition(new Vec3(0, -0.6f, 0));

        Object3D cannonShootingThink = createFlyingShere();
        cannonShootingThink.setPosition(new Vec3(0, 1.25f, 0));

        RayMarchObject ground = new RayMarchBox(
                new Vec3(20f, 0.1f, 20f),
                new Material(
                        Color.ofRGB(120, 110, 95).toVec3(),
                        0.95,
                        0.05,
                        0.00,
                        1.0
                )
        );

        /*new Material(
                Color.ofRGB(120, 110, 95).toVec3(),
                0.95,
                0.05,
                0.00,
                1.0
        )*/

        /*new Material(
                Color.ofRGB(100, 105, 115).toVec3(),
                0.7,
                0.0,
                0.05,
                1.0
        )*/

        ground.setPosition(new Vec3(0, -1.3f, 0));

        // Objects-Array mit Raymarching erweitern
        Object3D[] objects = new Object3D[] {
                cannonShootingThink,
                p0, p90, p180, p270,
                p45, p135, p225, p315,
                innerStuff,
                ground,
                };

        Light[] lights = new Light[] {
                new Light(new Vec3(-6, 8, 8),
                        2.2,
                        Color.ofRGB(255, 245, 220),
                        0.7,
                        12),
                new Light(new Vec3(0, 8, -15), 1.2, Color.ofRGB(255, 250, 200),0.7,0),
                new Light(new Vec3(3, 5, 4),
                        0.8,
                        Color.white(),
                        0.0,
                        0),
                new Light(new Vec3(-3, 3, 3), 0.4)
        };

        Scene scene = new Scene(cam, objects, lights);
        scene.setBackgroundColor(Color.ofRGB(28, 30, 42));

        // === Initiales Rendern ===
        RayTracer.render(resX, resY, scene, pixels);
        mis.newPixels();

        // === Input verbinden ===

        frame.setFocusable(true);
        frame.requestFocus();
    }

    private static AnalyticObject createCanonicalLargePetal() {

        Material gold = new Material(
                Color.ofRGB(212, 171, 62).toVec3(),
                0.28,
                0.95,
                0.0,
                1.0
        );

        Material silver = new Material(
                Color.ofRGB(175, 180, 190).toVec3(),
                0.28,
                0.90,
                0.0,
                1.0
        );

        AnalyticObject petal = Quadric.paraboloidY();
        petal.setMaterial(gold);

        petal = new Cut(
                new Cut(
                        petal,
                        HalfSpace.yLess(0.0f)
                ),
                HalfSpace.yGreater(-1.2f)
        );

        // Breite entlang Z begrenzen
        float halfWidth = 0.5f;
        petal = clampZ(
                petal,
                halfWidth
        );

        // Runde Aussparung an der Innenseite des Petals
        AnalyticObject innerCut = Quadric.cylinderZ();

        innerCut.setScale(new Vec3(
                0.75f,
                0.75f,
                1.5f
        ));

        innerCut.setPosition(new Vec3(
                -0.55f,
                -0.35f,
                0.0f
        ));


        petal = new Diff(
                petal,
                innerCut
        );

        petal = new Cut(
                petal,
                HalfSpace.xGreater(-0.75f)
        );

        AnalyticObject silverBase = Quadric.paraboloidY();
        silverBase.setMaterial(silver);

        silverBase.setScale(new Vec3(
                2f,
                0.1f,
                2f
        ));

        silverBase = new Cut(
                new Cut(
                        silverBase,
                        HalfSpace.yLess(-1.2f)
                ),
                HalfSpace.yGreater(-1.3f)
        );

        silverBase = clampZ(
                silverBase,
                halfWidth+0.1f
        );

        silverBase = clampX(
                silverBase,
                1.2f
        );


        petal = new Union(
                petal,
                silverBase
        );

        return petal;
    }

    private static Object3D createCanonicalPetal() {
        Material gold = new Material(
                Color.ofRGB(212, 171, 62).toVec3(),
                0.28,
                0.95,
                0.0,
                1.0
        );

        RayMarchObject petal = new RayMarchEllipsoid(
                new Vec3(0.7f, 1.11f, 0.7f),
                gold
        );

        float sideSlope = 0.85f;
        float sideDistance = 0.8f;
        float smoothness = 0.12f;

        petal = new SmoothIntersect(
                petal,
                new RayMarchHalfSpace(
                        new Vec3(-1.0f, sideSlope, 0.0f).normalize(),
                        sideDistance
                ),
                smoothness
        );

        petal = new SmoothIntersect(
                petal,
                new RayMarchHalfSpace(
                        new Vec3(1.0f, sideSlope, 0.0f).normalize(),
                        sideDistance
                ),
                smoothness
        );

        RayMarchObject cutHalfSpace = new RayMarchHalfSpace(
                new Vec3(0, 1, 0),
                -0.5f
        );

        petal = new SmoothCut(
                petal,
                cutHalfSpace,
                0.1f
        );

        RayMarchObject leftBlueStrip = createBlueShape();
        RayMarchObject rightBlueStrip = createBlueShape();

        leftBlueStrip.setScale(new Vec3(
                0.35f,
                0.55f,
                0.35f
        ));

        leftBlueStrip.setRotation(new Vec3(
                -65f, //-40 sieht gut aus
                25,
                25
        ));

        leftBlueStrip.setPosition(new Vec3(
                -0.48f-1,
                0.15f,
                0.42f
        ));

        petal = new RayMarchCut(
                petal,
                leftBlueStrip,
                USE_A
        );

        petal = new RayMarchUnion(
                petal,
                leftBlueStrip,
                PRESERVE_MATERIALS
        );

        return petal;
    }

    private static Object3D createSmallPetal() {
        Material gold = new Material(
                Color.ofRGB(212, 171, 62).toVec3(),
                0.28,
                0.95,
                0.0,
                1.0
        );

        Material silver = new Material(
                Color.ofRGB(175, 180, 190).toVec3(),
                0.28,
                0.90,
                0.0,
                1.0
        );

        Material blueDark = new Material(
                Color.ofRGB(35, 150, 235).toVec3(),
                0.08,
                0.05,
                0.05,
                1.0
        );

        float sideSlope = 1.0f;
        float sideDistance = 0.2f;

        float frontSlope = 1.0f;
        float frontDistance = 0.2f;

        RayMarchObject top = new RayMarchBox(
                new Vec3(1f, 1f, 1f),
                gold
        );



        top = new RayMarchIntersect(
                top,
                new RayMarchHalfSpace(
                        new Vec3(-1.0f, sideSlope, 0.0f),
                        sideDistance
                ),
                gold
        );

        top = new RayMarchIntersect(
                top,
                new RayMarchHalfSpace(
                        new Vec3(1.0f, sideSlope, 0.0f),
                        sideDistance
                ),
                gold
        );

        top = new RayMarchIntersect(
                top,
                new RayMarchHalfSpace(
                        new Vec3(-1.0f, frontSlope, -1.0f).normalize(),
                        frontDistance
                ),
                gold
        );

        top = new RayMarchIntersect(
                top,
                new RayMarchHalfSpace(
                        new Vec3(1.0f, frontSlope, -1.0f).normalize(),
                        frontDistance
                ),
                gold
        );

        top.setRotation(new Vec3(0, 180, 0));



        top.setPosition(new Vec3(
                0.0f,
                2.0f,
                0.60f
        ));

        RayMarchObject baseWall = new RayMarchBox(
                new Vec3(1f, 1f, 1f),
                gold
        );

        baseWall.setScale(new Vec3(
                1.0f,
                1.0f,
                0.5f
        ));

        baseWall.setPosition(new Vec3(
                0.0f,
                0.0f,
                0.0f
        ));



        RayMarchObject triangleFoot = new RayMarchBox(
                new Vec3(1f, 1f, 1f),
                gold
        );

        triangleFoot.setScale(new Vec3(
                1.0f,
                0.5f,
                1.0f
        ));


        triangleFoot = new RayMarchIntersect(
                triangleFoot,
                new RayMarchHalfSpace(
                        new Vec3(0.0f, 2.0f, -1.0f).normalize(),
                        0.0f
                ),
                gold
        );



        RayMarchObject cutBox = new RayMarchBox(
                new Vec3(0.70f, 1.0f, 1.0f)
        );

        cutBox.setPosition(new Vec3(
                0.0f,
                0.0f,
                -0.25f
        ));

        triangleFoot = new SmoothCut(
                triangleFoot,
                cutBox,
                0.1f
        );

        triangleFoot.setRotation(new Vec3(0, 180, 0));
        triangleFoot.setPosition(new Vec3(
                0.0f,
                -0.5f,
                1.25f
        ));

        RayMarchObject petal = new SmoothUnion(
                triangleFoot,
                baseWall,
                0.1f
        );

        petal = new SmoothUnion(
                petal,
                top,
                0.1f
        );

        RayMarchObject cylinder = new RayMarchCylinder(
                1.0f,
                1.5f,
                blueDark
        );

        cylinder = new RayMarchIntersect(
                cylinder,
                new RayMarchHalfSpace(
                        new Vec3(-1.0f, 0.0f, -1.0f).normalize(),
                        frontDistance
                ),
                blueDark
        );

        cylinder = new RayMarchIntersect(
                cylinder,
                new RayMarchHalfSpace(
                        new Vec3(1.0f, 0.0f, -1.0f).normalize(),
                        frontDistance
                ),
                blueDark
        );

        cylinder.setScale(new Vec3(
                0.8f,
                1.5f,
                1.10f
        ));

        cylinder.setPosition(new Vec3(
                0.0f,
                0.0f,
                0.30f
        ));

        RayMarchObject silverBase = new RayMarchBox(
                new Vec3(2.0f, 0.18f, 1.4f),
                silver
        );

        silverBase = new RayMarchIntersect(
                silverBase,
                new RayMarchHalfSpace(
                        new Vec3(1.0f, 0.0f, 1.0f).normalize(),
                        1.3f
                ),
                silver
        );

        silverBase = new RayMarchIntersect(
                silverBase,
                new RayMarchHalfSpace(
                        new Vec3(-1.0f, 0.0f, 1.0f).normalize(),
                        1.3f
                ),
                silver
        );

        silverBase = clampXRayMarch(
                silverBase,
                1.25f
        );

        silverBase.setPosition(new Vec3(
                0.0f,
                -1.20f,
                1.45f
        ));

        petal = new SmoothUnion(
                petal,
                cylinder,
                0.1f,
                PRESERVE_MATERIALS
        );

        petal = new SmoothUnion(
                petal,
                silverBase,
                0.1f,
                PRESERVE_MATERIALS
        );

        petal.setScale(new Vec3(
                0.25f,
                0.25f,
                0.25f
        ));

        return petal;
    }

    private static Object3D createMidPart(){
        Material gold = new Material(
                Color.ofRGB(212, 171, 62).toVec3(),
                0.28,
                0.95,
                0.0,
                1.0
        );

        Material silver = new Material(
                Color.ofRGB(175, 180, 190).toVec3(),
                0.28,
                0.90,
                0.0,
                1.0
        );

        Material energyBlue = new Material(
                Color.ofRGB(30, 175, 255).toVec3(),
                0.08,
                0.05,
                0.06,
                1.0
        );

        RayMarchObject goldBase = new RayMarchCylinder(
                2.0f,
                0.55f,
                gold
        );

        RayMarchObject bowlCut = new RayMarchSphere(
                2.5f,
                gold
        );

        bowlCut.setPosition(new Vec3(
                0.0f,
                1.75f,
                0.0f
        ));

        goldBase = new SmoothCut(
                goldBase,
                bowlCut,
                0.08f,
                PRESERVE_MATERIALS
        );

        RayMarchObject fixMiddleGoldOverBaseSilver = new RayMarchCylinder(
                2f,
                0.01f,
                gold
        );
        fixMiddleGoldOverBaseSilver.setPosition(new Vec3(0, -0.4f, 0));

        goldBase = new RayMarchUnion(
                goldBase,
                fixMiddleGoldOverBaseSilver,
                PRESERVE_MATERIALS
        );

        RayMarchObject underBaseSilver = new RayMarchCylinder(
                2.1f,
                0.01f,
                silver
        );
        underBaseSilver.setPosition(new Vec3(0, -0.4f, 0));
        RayMarchObject base = new SmoothUnion(
                goldBase,
                underBaseSilver,
                0.1f,
                PRESERVE_MATERIALS
        );

        RayMarchObject blueStuffMidle = new RayMarchCylinder(
                0.85f,
                0.1f,
                energyBlue
        );
        blueStuffMidle.setPosition(new Vec3(0, -0.4f, 0));

        base = new SmoothUnion(
                base,
                blueStuffMidle,
                0.1f,
                PRESERVE_MATERIALS
        );

        RayMarchObject goldRingForBuleStuff = new RayMarchCylinder(
                0.95f,
                0.2,
                gold
        );
        RayMarchObject cutForTheRing = new RayMarchCylinder(
                0.8f,
                0.2f,
                gold
        );

        goldRingForBuleStuff.setPosition(new Vec3(0, -0.4f, 0));
        cutForTheRing.setPosition(new Vec3(0, -0.4f, 0));

        goldRingForBuleStuff = new SmoothCut(
                goldRingForBuleStuff,
                cutForTheRing,
                0.1f
        );

        base = new SmoothUnion(
                base,
                goldRingForBuleStuff,
                0.1f,
                PRESERVE_MATERIALS
        );
        return base;
    }

    private static Object3D createFlyingShere(){
        Material gold = new Material(
                Color.ofRGB(212, 171, 62).toVec3(),
                0.28,
                0.95,
                0.0,
                1.0
        );

        Material darkMetal = new Material(
                Color.ofRGB(42, 45, 48).toVec3(),
                0.48,
                0.65,
                0.0,
                1.0
        );

        Material blue = new Material(
                Color.ofRGB(20, 190, 255).toVec3(),
                0.08,
                0.2,
                0.15,
                1.0
        );

        RayMarchObject base = new RayMarchSphere(
                1.45f,
                darkMetal
        );


        for (int i = 0; i < 4; i++) {

            RayMarchObject plate = new RayMarchSpherePlate(
                    1.51,
                    0.055,
                    new Vec3(0.75f, 0.36f, 0.9f),
                    new Vec3(0, 1.30f, 0),
                    gold
            );

            plate.setRotation(new Vec3(
                    0,
                    0,
                    90f * i
            ));

            base = new SmoothUnion(
                    base,
                    plate,
                    0.03f,
                    PRESERVE_MATERIALS
            );
        }

        RayMarchObject frontBase = new RayMarchCylinder(
                0.78f,
                0.16f,
                darkMetal
        );

        frontBase.setRotation(new Vec3(90, 0, 0));
        frontBase.setPosition(new Vec3(0, 0, 1.35f));

        RayMarchObject outer = new RayMarchCylinder(
                0.62f,
                0.12f,
                gold
        );

        RayMarchObject inner = new RayMarchCylinder(
                0.43f,
                0.16f,
                darkMetal
        );

        RayMarchObject goldRing = new SmoothDiff(
                outer,
                inner,
                0.02f,
                PRESERVE_MATERIALS
        );

        goldRing.setRotation(new Vec3(90, 0, 0));
        goldRing.setPosition(new Vec3(0, 0, 1.48f));



        RayMarchObject blueCore = new RayMarchCylinder(
                0.40f,
                0.10f,
                blue
        );

        blueCore.setRotation(new Vec3(90, 0, 0));
        blueCore.setPosition(new Vec3(0, 0, 1.56f));

        base = new SmoothUnion(base, frontBase, 0.04f, PRESERVE_MATERIALS);
        base = new SmoothUnion(base, goldRing, 0.02f, PRESERVE_MATERIALS);
        base = new SmoothUnion(base, blueCore, 0.02f, PRESERVE_MATERIALS);

        base.setScale(new Vec3(
                0.5f,
                0.5f,
                0.5f
        ));
        return base;
    }

    private static RayMarchObject createBlueShape(){
        Material blueGlass = new Material(
                Color.ofRGB(20, 190, 255).toVec3(),
                0.08,
                0.2,
                0.15,
                1.0
        );

        RayMarchObject blue = new RayMarchBox(
                new Vec3(0.32f, 0.85f, 0.12f),
                blueGlass
        );

        RayMarchObject triangleTop = new RayMarchBox(
                new Vec3(1f, 1f, 1f),
                blueGlass
        );

        //slopes that meet at the top

        float frontSlope = 1.0f;
        float frontDistance = 0.2f;

        RayMarchObject slopeCutTop1 = new RayMarchHalfSpace(
                new Vec3(-1.0f, frontSlope, 0.0f),
                frontDistance
        );

        RayMarchObject slopeCutTop2 = new RayMarchHalfSpace(
                new Vec3(1.0f, frontSlope, 0.0f),
                frontDistance
        );

        triangleTop = new RayMarchIntersect(
                triangleTop,
                slopeCutTop1,
                blueGlass
        );

        triangleTop = new RayMarchIntersect(
                triangleTop,
                slopeCutTop2,
                blueGlass
        );

        RayMarchObject triangleBottom = new RayMarchBox(
                new Vec3(1f, 1f, 1f),
                blueGlass
        );

        RayMarchObject slopeCutBottom1 = new RayMarchHalfSpace(
                new Vec3(-1.0f, -frontSlope, 0.0f),
                frontDistance
        );

        RayMarchObject slopeCutBottom2 = new RayMarchHalfSpace(
                new Vec3(1.0f, -frontSlope, 0.0f),
                frontDistance
        );

        triangleBottom = new RayMarchIntersect(
                triangleBottom,
                slopeCutBottom1,
                blueGlass
        );

        triangleBottom = new RayMarchIntersect(
                triangleBottom,
                slopeCutBottom2,
                blueGlass
        );

        triangleTop.setScale(new Vec3(0.32f, 0.20f, 0.12f));
        triangleBottom.setScale(new Vec3(0.32f, 0.20f, 0.12f));

        triangleTop.setPosition(new Vec3(0, 1.0f, 0));
        triangleBottom.setPosition(new Vec3(0, -1.0f, 0));

        RayMarchObject blueShape = new RayMarchUnion(
                blue,
                triangleTop
        );

        blueShape = new RayMarchUnion(
                blueShape,
                triangleBottom
        );

        return blueShape;
    }

    private static AnalyticObject clampX(AnalyticObject object, float halfWidth) {
        return new Cut(
                new Cut(object, HalfSpace.xGreater(-halfWidth)),
                HalfSpace.xLess(halfWidth)
        );
    }

    private static AnalyticObject clampZ(AnalyticObject object, float halfWidth) {
        return new Cut(
                new Cut(object, HalfSpace.zGreater(-halfWidth)),
                HalfSpace.zLess(halfWidth)
        );
    }

    private static RayMarchObject clampXRayMarch(RayMarchObject object, float halfWidth){
        return new RayMarchIntersect(
                new RayMarchIntersect(object,
                        new RayMarchHalfSpace(new Vec3(1,0,0), halfWidth)),
                new RayMarchHalfSpace(new Vec3(-1,0,0), halfWidth)
        );
    }

    private static RayMarchObject clampZRayMarch(RayMarchObject object, float halfWidth){
        return new RayMarchIntersect(
                new RayMarchIntersect(object,
                        new RayMarchHalfSpace(new Vec3(0,0,1), halfWidth)),  // Z-Achse!
                new RayMarchHalfSpace(new Vec3(0,0,-1), halfWidth)
        );
    }

    public static HalfSpace slopeLeft(float slope, float distance) {
        return HalfSpace.withNormal(new Vec3(1, slope, 0), distance);
    }

    public static HalfSpace slopeRight(float slope, float distance) {
        return HalfSpace.withNormal(new Vec3(-1, slope, 0), distance);
    }

    public static HalfSpace slopeFront(float slope, float distance) {
        return HalfSpace.withNormal(new Vec3(0, slope, 1), distance);
    }

    public static HalfSpace slopeBack(float slope, float distance) {
        return HalfSpace.withNormal(new Vec3(0, slope, -1), distance);
    }

    private static Vec3 positionOnRing(float angleDeg, float distance, float y) {
        double angle = Math.toRadians(angleDeg);

        return new Vec3(
                (float) (Math.cos(angle) * distance),
                y,
                (float) (Math.sin(angle) * distance)
        );
    }
}
