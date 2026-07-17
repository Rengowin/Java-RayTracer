package BennysRayTrayer;

import BennysRayTrayer.core.*;
import BennysRayTrayer.input.Input;
import BennysRayTrayer.objects.*;
import BennysRayTrayer.objects.Normal.HalfSpace;
import BennysRayTrayer.objects.Normal.Quadric;
import BennysRayTrayer.objects.Normal.csg.Cut;
import BennysRayTrayer.objects.Normal.csg.Diff;
import BennysRayTrayer.objects.rayMarch.RayMarchObject;
import BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG.RayMarchIntersect;
import BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG.SmoothCut;
import BennysRayTrayer.objects.rayMarch.operations.RayMarchCSG.SmoothUnion;
import BennysRayTrayer.objects.rayMarch.primitives.RayMarchBox;
import BennysRayTrayer.objects.rayMarch.primitives.RayMarchCylinder;
import BennysRayTrayer.objects.rayMarch.primitives.RayMarchHalfSpace;
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


        //Camera toptown View
        Camera cam1 = new Camera(
                new Vec3(0, 3, 0),
                60,
                new Vec3(0, -1, 0)
        );
        cam1.rotatePitch(-180);

        // ============ VERBESSERTE MATERIALIEN ============
        Material gold = new Material(
                Color.ofRGB(245, 205, 85).toVec3(),
                0.35,
                0.0,
                0.0,
                0.0,
                1.0
        );

        Material blueGlass = new Material(
                Color.ofRGB(40, 180, 255).toVec3(),
                0.02,   // sehr glatt
                0.0,    // nicht metallisch
                0.1,    // wenig Reflexion
                0.85,   // sehr transparent
                1.45
        );

        Material silver = new Material(
                Color.ofRGB(190, 195, 205).toVec3(),
                0.2,    // sehr glatt
                0.8,    // sehr metallisch
                0.35,   // starke Reflexion
                0.0,
                1.0
        );

        Material softSilver = new Material(
                Color.ofRGB(170, 175, 185).toVec3(),
                0.35,   // moderater Glanz
                0.5,    // moderater Metallic
                0.12,   // schwache Reflexion
                0.0,
                1.0
        );

        // Mirror: Ultra-glatt und reflektiv
        Material mirror = new Material(
                Color.ofRGB(15, 15, 20).toVec3(),
                0.02,   // ultra glatt
                0.95,   // maximaler Metallic
                0.95,   // sehr starke Reflexion
                0.0,
                1.0
        );

        // High-quality Glass: klar und transparent
        Material glass = new Material(
                Color.white().toVec3(),
                0.005,  // ultra glatt
                0.0,    // nicht metallisch
                0.08,   // schwache Reflexion (Fresnel-Effekt)
                0.96,   // fast vollständig transparent
                1.5     // höherer Brechungsindex = stärkere Brechung
        );

        Material goldDemo = new Material(
                Color.ofRGB(245, 205, 85).toVec3(),
                0.2,    // glatt
                0.3,    // metallisch
                0.25,   // Reflexion
                0.0,
                1.0
        );

        Object3D mirrorSphere = Quadric.sphere(Color.white());
        mirrorSphere.setPosition(new Vec3(-3.2f, -0.4f, 1.5f));
        mirrorSphere.setMaterial(mirror);

        Object3D glassSphere = Quadric.sphere(Color.cyan());
        glassSphere.setPosition(new Vec3(3.2f, -0.4f, 1.5f));
        glassSphere.setMaterial(glass);

        Object3D goldSphere = Quadric.sphere(Color.yellow());
        goldSphere.setPosition(new Vec3(0f, -0.4f, 2.7f));
        goldSphere.setMaterial(goldDemo);

        mirrorSphere.setScale(new Vec3(0.5f, 0.5f, 0.5f));
        glassSphere.setScale(new Vec3(0.5f, 0.5f, 0.5f));
        goldSphere.setScale(new Vec3(0.5f, 0.5f, 0.5f));

        Object3D test = Quadric.cylinderY(Color.red());
        test.setScale(new Vec3(0.12f, 1.0f, 0.12f));
        test.setPosition(new Vec3(3.2f, -0.3f, -1.2f));

        Object3D redSphere = Quadric.sphere(Color.red());
        redSphere.setScale(new Vec3(0.8f, 0.8f, 0.8f));
        redSphere.setPosition(new Vec3(-6f, -0.5f, 1.0f));

        Object3D behindGlass = Quadric.sphere(Color.red());
        behindGlass.setScale(new Vec3(0.7f, 0.7f, 0.7f));
        behindGlass.setPosition(new Vec3(3.2f, -0.4f, 0.3f));

        Object3D floorDemo = HalfSpace.yGreater(-1.2f, Color.gray());

        //photon cannon die irgendwie nicht will ich
        Object3D floorBase = Quadric.cylinderY(Color.gray());

        floorBase.setScale(new Vec3(2f, 1f, 2f));
        floorBase.setMaterial(gold);

        Object3D floor = new Cut(
                new Cut(floorBase, HalfSpace.yLess(-1.1f, Color.gray())),
                HalfSpace.yGreater(-1.3f, Color.gray())
        );

        Object3D emblemBeforCut = Quadric.cylinderY(Color.cyan());
        emblemBeforCut.setScale(new Vec3(0.750f, 1, 0.750f));
        emblemBeforCut.setMaterial(blueGlass);

        Object3D emblem = new Cut(
                new Cut(emblemBeforCut, HalfSpace.yLess(-1.0f, Color.blue())),
                HalfSpace.yGreater(-1.2f, Color.blue())
        );

        Object3D outer = Quadric.cylinderY(Color.yellow());
        outer.setScale(new Vec3(2f, 1f, 2f));
        outer.setMaterial(gold);

        Object3D inner = Quadric.cylinderY(Color.yellow());
        inner.setScale(new Vec3(1.0f, 1f, 1.0f));
        inner.setMaterial(gold);

        Object3D outerRingBeforCut = new Diff(outer, inner);

        Object3D outerRingBeforDiffWithSpehere = new Cut(
                new Cut(outerRingBeforCut, HalfSpace.yLess(-0.75f, Color.gray())),
                HalfSpace.yGreater(-1.2f, Color.gray())
        );

        Object3D middleBall = Quadric.sphere(Color.gray());
        middleBall.setScale(new Vec3(0.75f, 0.75f, 0.75f));
        middleBall.setPosition(new Vec3(0f, 0.75f, 0f));
        middleBall.setMaterial(softSilver);

        Object3D sphereForCut = Quadric.sphere(Color.gray());
        sphereForCut.setScale(new Vec3(1.8f, 2.5f, 1.8f));

        Object3D outerRing = new Diff(outerRingBeforDiffWithSpehere,sphereForCut);

        float petaldistance = 2.0f;


        Object3D p0 = createCanonicalPetal();
        p0.setPosition(new Vec3(petaldistance, 0, 0));

        Object3D p45 = createSmallPetal();
        p45.setPosition(new Vec3(petaldistance * (float) Math.cos(Math.toRadians(45)), 0, petaldistance * (float) Math.sin(Math.toRadians(45))));
        p45.setRotation(new Vec3(0, 45, 0));

        Object3D p90 = createCanonicalPetal();
        p90.setRotation(new Vec3(0, 90, 0));
        p90.setPosition(new Vec3(petaldistance, 0, 0));

        Object3D p135 = createSmallPetal();
        p135.setPosition(new Vec3(
                (float)(petaldistance * Math.cos(Math.toRadians(135))),
                0,
                (float)(petaldistance * Math.sin(Math.toRadians(135)))
        ));
        p135.setRotation(new Vec3(0, 135, 0));

        Object3D p180 = createCanonicalPetal();
        p180.setRotation(new Vec3(0, 180, 0));
        p180.setPosition(new Vec3(petaldistance, 0, 0));

        Object3D p225 = createSmallPetal();
        p225.setPosition(new Vec3(
                (float)(petaldistance * Math.cos(Math.toRadians(255))),
                0,
                (float)(petaldistance * Math.sin(Math.toRadians(255)))
        ));
        p225.setRotation(new Vec3(0, 225, 0));

        Object3D p270 = createCanonicalPetal();
        p270.setRotation(new Vec3(0, 270, 0));
        p270.setPosition(new Vec3(petaldistance, 0, 0));

        Object3D p315 = createSmallPetal();
        p315.setPosition(new Vec3(
                (float)(petaldistance * Math.cos(Math.toRadians(315))),
                0,
                (float)(petaldistance * Math.sin(Math.toRadians(315)))
        ));
        p315.setRotation(new Vec3(0, 315, 0));

        // ============ RAYMARCHING OBJEKTE ============

        RayMarchObject ground = new RayMarchBox(
                new Vec3(20f, 0.1f, 20f),
                new Material(
                        Color.ofRGB(100, 105, 115).toVec3(),
                        0.7,
                        0.0,
                        0.05,
                        0.0,
                        1.0
                )
        );

        ground.setPosition(new Vec3(0, -1.3f, 0));

        p45.setScale(new Vec3(0.5f, 0.5f, 0.5f));

        // Objects-Array mit Raymarching erweitern
        Object3D[] objects = new Object3D[] {
                //floor, emblem, outerRing, middleBall,
                //p0, p90, p180, p270,
                p45, p135, //p225, p315,
                //ground
        };

        Light[] lights = new Light[] {
                new Light(new Vec3(-6,8,8), 1.4, Color.ofRGB(255, 200, 140)),
                new Light(new Vec3(0, -5, -4), 0.5),
                new Light(new Vec3(3, 5, 4), 0.8),
                new Light(new Vec3(-3, 3, 3), 0.6)
        };

        Scene scene = new Scene(cam, objects, lights);
        scene.setBackgroundColor(Color.ofRGB(28, 30, 42));

        // === Initiales Rendern ===
        RayTracer.render(resX, resY, scene, pixels);
        mis.newPixels();

        // === Input verbinden ===
        Input.bind(frame, cam, scene, resX, resY, pixels, mis);

        frame.setFocusable(true);
        frame.requestFocus();
    }

    private static Object3D createCanonicalPetal() {
        Material gold = new Material(
                Color.ofRGB(245, 205, 85).toVec3(),
                0.35,
                0.0,
                0.0,
                0.0,
                1.0
        );

        Object3D base = Quadric.paraboloidY(Color.gray());
        base.setMaterial(gold);

        Object3D petal = new Cut(
                new Cut(
                        base,
                        HalfSpace.yLess(0.0f, Color.gray())
                ),
                HalfSpace.yGreater(-1.2f, Color.gray())
        );

        petal = clampZ(petal, 0.5f, Color.gray());

        Object3D hole = Quadric.cylinderZ(Color.gray());
        hole.setMaterial(gold);
        hole.setScale(new Vec3(0.75f, 0.75f, 1.5f));
        hole.setPosition(new Vec3(-0.55f, -0.35f, 0.0f));

        return new Diff(petal, hole);
    }

    private static Object3D createSmallPetal() {
        Material gold = new Material(
                Color.ofRGB(212, 171, 62).toVec3(),
                0.28,   // roughness
                0.95,   // metallic
                0.18,   // reflection
                0.0,
                1.0
        );

        Material silver = new Material(
                Color.ofRGB(175,180,190).toVec3(),
                0.28,
                0.90,
                0.10,
                0.0,
                1.0
        );

        Material blueDark = new Material(
                Color.ofRGB(35, 150, 235).toVec3(),
                0.08,
                0.05,
                0.05,
                0.0,
                1.0
        );

        float sideSlope = 1.0f;
        float sideDistance = 0.2f;

        float frontSlope = 1.00f;
        float frontDistance = 0.2f;

        RayMarchObject smallPetalTop = new RayMarchBox(
                new Vec3(1f, 1f, 1f),
                gold
        );

        smallPetalTop = new RayMarchIntersect(
                smallPetalTop,
                new RayMarchHalfSpace(
                        new Vec3(-1.0f, sideSlope, 0.0f),
                        sideDistance
                ),
                gold
        );

        smallPetalTop = new RayMarchIntersect(
                smallPetalTop,
                new RayMarchHalfSpace(
                        new Vec3(1.0f, sideSlope, 0.0f),
                        sideDistance
                ),
                gold
        );

        //frontSlope
        smallPetalTop = new RayMarchIntersect(
                smallPetalTop,
                new RayMarchHalfSpace(
                        new Vec3(-1.0f, frontSlope, -1.0f).normalize(),
                        frontDistance
                ),
                gold
        );

        smallPetalTop = new RayMarchIntersect(
                smallPetalTop,
                new RayMarchHalfSpace(
                        new Vec3(1.0f, frontSlope, -1.0f).normalize(),
                        frontDistance
                ),
                gold
        );

        RayMarchObject baseWall = new RayMarchBox(
                new Vec3(1f, 1f, 1f),
                gold
        );
        baseWall.setScale(new Vec3(1f,1f,0.5f));

        RayMarchObject trianglefoot = new RayMarchBox(
                new Vec3(1f, 1f, 1f),
                gold
        );

        trianglefoot.setScale(new Vec3(1f,0.5f,1f));

        trianglefoot = new RayMarchIntersect(
                trianglefoot,
                new RayMarchHalfSpace(
                        new Vec3(0.0f, 2f, -1.0f),
                        0.0f
                ),
                gold
        );

        RayMarchObject cutBox = new RayMarchBox(
                new Vec3(0.70f,1,1)
        );
        cutBox.setPosition(new Vec3(0f,0,-0.25f));

        trianglefoot = new SmoothCut(
                trianglefoot,
                cutBox,
                0.1f
        );

        baseWall.setPosition(new Vec3(0.0f, 0.0f, 0f));
        trianglefoot.setPosition(new Vec3(0.0f, -0.5f, -0.75f));
        trianglefoot.setRotation(new Vec3(0, 180, 0));

        smallPetalTop.setPosition(new Vec3(0.0f, 2.0f, -0.60f));

        smallPetalTop.setRotation(new Vec3(0, 180, 0));

        RayMarchObject smallPetal = new SmoothUnion(
                        trianglefoot,
                        baseWall,
                        0.1f
        );

        smallPetal = new SmoothUnion(
                smallPetal,
                smallPetalTop,
                0.1f
        );

        RayMarchObject cylinder = new RayMarchCylinder(
                1f,
                1.5f,
                blueDark
        );

        //denn cylinder wie fronttop slopen
        cylinder = new RayMarchIntersect(
                cylinder,
                new RayMarchHalfSpace(
                        new Vec3(-1.0f, 0, -1.0f).normalize(),
                        frontDistance
                ),
                blueDark
        );

        cylinder = new RayMarchIntersect(
                cylinder,
                new RayMarchHalfSpace(
                        new Vec3(1.0f, 0, -1.0f).normalize(),
                        frontDistance
                ),
                blueDark
        );

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

        silverBase = clampXRayMarch(silverBase, 1.25f);
        silverBase.setPosition(new Vec3(0.0f, -1.5f, 0.25f));

        cylinder.setPosition(new Vec3(0.0f, 0.0f, 0.30f));
        cylinder.setScale(new Vec3(0.8f,1.5f,1.10f));

        smallPetal = new SmoothUnion(
                smallPetal,
                cylinder,
                0.1f,
                PRESERVE_MATERIALS
        );

        smallPetal = new SmoothUnion(
                smallPetal,
                silverBase,
                0.1f,
                PRESERVE_MATERIALS
        );

        return smallPetal;
    }

    private static Object3D clampX(Object3D object, float halfWidth, Color color) {
        return new Cut(
                new Cut(object, HalfSpace.xGreater(-halfWidth, color)),
                HalfSpace.xLess(halfWidth, color)
        );
    }

    private static Object3D clampZ(Object3D object, float halfWidth, Color color) {
        return new Cut(
                new Cut(object, HalfSpace.zGreater(-halfWidth, color)),
                HalfSpace.zLess(halfWidth, color)
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

    public static HalfSpace slopeLeft(float slope, float distance, Color color) {
        return HalfSpace.withNormal(new Vec3(1, slope, 0), distance, color);
    }

    public static HalfSpace slopeRight(float slope, float distance, Color color) {
        return HalfSpace.withNormal(new Vec3(-1, slope, 0), distance, color);
    }

    public static HalfSpace slopeFront(float slope, float distance, Color color) {
        return HalfSpace.withNormal(new Vec3(0, slope, 1), distance, color);
    }

    public static HalfSpace slopeBack(float slope, float distance, Color color) {
        return HalfSpace.withNormal(new Vec3(0, slope, -1), distance, color);
    }
}
