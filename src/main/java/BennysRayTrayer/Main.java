package BennysRayTrayer;

import BennysRayTrayer.core.*;
import BennysRayTrayer.input.Input;
import BennysRayTrayer.objects.*;
import BennysRayTrayer.rendering.*;
import BennysRayTrayer.scene.*;


import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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

        /*Camera cam = new Camera(
                new Vec3(0, 0, 5),
                60,
                new Vec3(0, 0, -1)
        );*/

        //Materials
        Material gold = new Material(
                Color.ofRGB(230, 180, 55).toVec3(),
                0.55,
                0,
                0.05,
                0.0,
                1.0
        );

        Material blueGlass = new Material(
                Color.ofRGB(40,180,255).toVec3(),
                0.05,
                0,
                0.2,
                0.8,
                1.45
        );

        Material silver = new Material(
                Color.ofRGB(190, 195, 205).toVec3(),
                0.28,
                0.4,
                0.18,
                0.0,
                1.0
        );
        Material softSilver = new Material(
                Color.ofRGB(170, 175, 185).toVec3(),
                0.45,
                0.15,
                0.08,
                0.0,
                1.0
        );


        //für demo/abgabe
        Material mirror = new Material(
                Color.ofRGB(20, 20, 25).toVec3(),
                0.0,
                0.0,
                0.9,
                0.0,
                1.0
        );
        Material glass = new Material(
                Color.white().toVec3(),   // oder ganz leicht blau
                0.01,
                0.0,
                0.05,
                0.95,
                3
        );
        Material goldDemo = new Material(Color.ofRGB(245,205,85).toVec3(), 0.25, 0.3, 0.15, 0.0, 1.0);

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

        Object3D petalRight = createLargePetal(1.9f, -0.2f, 0f, LargePetalSide.RIGHT);
        Object3D petalLeft  = createLargePetal(-1.9f, -0.2f, 0f, LargePetalSide.LEFT);

        Object3D petalFront = createLargePetal(0f, -0.2f, 1.9f, LargePetalSide.FRONT);
        Object3D petalBack  = createLargePetal(0f, -0.2f, -1.9f, LargePetalSide.BACK);

        Object3D smallPetalRightFront = createLargePetal( 1.5f, -0.2f,   1.5f, LargePetalSide.RIGHT);
        Object3D smallPetalRightBack  = createLargePetal(-1.5f, -0.2f,    1.5f, LargePetalSide.RIGHT);
        Object3D smallPetalLeftFront  = createLargePetal( 1.5f, -0.2f,   -1.5f, LargePetalSide.RIGHT);
        Object3D smallPetalLeftBack   = createLargePetal(-1.5f, -0.2f,   -1.5f, LargePetalSide.RIGHT);

        smallPetalRightFront.setRotation(new Vec3(0, 45, 0));
        smallPetalRightBack.setRotation(new Vec3(0, 125, 0));
        smallPetalLeftFront.setRotation(new Vec3(0, 225, 0));
        smallPetalLeftBack.setRotation(new Vec3(0, 315, 0));

        Object3D[] objects = new Object3D[] {
                floor, emblem, outerRing, middleBall,
                petalRight, petalLeft, petalFront, petalBack,
                smallPetalRightFront, smallPetalRightBack, smallPetalLeftFront, smallPetalLeftBack,
                mirrorSphere, glassSphere, goldSphere, test, redSphere, behindGlass
        };

        /*Object3D[] objects = new Object3D[] {
                mirrorSphere, glassSphere, goldSphere, test, redSphere
        };*/

        /*Object3D[] objects = new Object3D[] {
                floor, emblem,// outerRing, middleBall,
                petalRight, petalLeft
        };*/

        //dumme test um zuscahuen ob es an den csg operatoren liegt weil die richtung kammer schneide

        Object3D testShere = Quadric.sphere(Color.red());

        Object3D testCylinder = Quadric.cylinderZ(Color.red());
        testCylinder.setScale(new Vec3(0.5f, 0.5f, 0.5f));

        Object3D diff = new Diff(testShere, testCylinder);

        /*Object3D[] objects = new Object3D[]{
                diff
        };*/


        Light[] lights = new Light[] {
                new Light(new Vec3(0, -5, -4), 2.0),
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

    private enum LargePetalSide {
        LEFT, RIGHT, FRONT, BACK
    }

    private enum DiagonalPetalSide {
        RIGHT_FRONT, RIGHT_BACK, LEFT_FRONT, LEFT_BACK
    }

    private static Object3D createLargePetal(float x, float y, float z, LargePetalSide side) {

        Material gold = new Material(
                Color.ofRGB(230, 180, 55).toVec3(),
                0.55,
                0.0,
                0.05,
                0.0,
                1.0
        );

        Object3D petalBase = Quadric.paraboloidY(Color.gray());
        petalBase.setMaterial(gold);
        Object3D cutBack = new Cut(
                new Cut(petalBase, HalfSpace.yLess(-0f, Color.gray())),
                HalfSpace.yGreater(-1.2f, Color.gray())
        );


        float widthOfPadle = 0.5f;
        float cylinderXScale = 0.75f;
        float cylinderYScale = 0.75f;
        float cylinderZScale = 1.5f;
        Object3D innerRoundDiff;

         switch (side) {
             case RIGHT:
                 cutBack = clampZ(cutBack, widthOfPadle, Color.gray());
                 innerRoundDiff = Quadric.cylinderZ(Color.gray());
                 innerRoundDiff.setScale(new Vec3(cylinderXScale, cylinderYScale, cylinderZScale));
                 innerRoundDiff.setPosition(new Vec3(-0.55f, -0.35f, 0.0f));
                 cutBack = new Diff(cutBack, innerRoundDiff);
                 break;
             case LEFT:
                 cutBack = clampZ(cutBack, widthOfPadle, Color.gray());
                 innerRoundDiff = Quadric.cylinderZ(Color.gray());
                 innerRoundDiff.setScale(new Vec3(cylinderXScale, cylinderYScale, cylinderZScale));
                 innerRoundDiff.setPosition(new Vec3(0.55f, -0.35f, 0.0f));
                 cutBack = new Diff(cutBack, innerRoundDiff);
                 break;
             case FRONT:
                 cutBack = clampX(cutBack, widthOfPadle, Color.gray());
                 innerRoundDiff = Quadric.cylinderX(Color.gray());
                 innerRoundDiff.setScale(new Vec3(cylinderZScale, cylinderYScale, cylinderXScale));
                 innerRoundDiff.setPosition(new Vec3(0.0f, -0.5f, -0.75f));
                 cutBack = new Diff(cutBack, innerRoundDiff);
                 break;
             case BACK:
                 cutBack = clampX(cutBack, widthOfPadle, Color.gray());
                 innerRoundDiff = Quadric.cylinderX(Color.gray());
                 innerRoundDiff.setScale(new Vec3(cylinderZScale, cylinderYScale, cylinderXScale));
                 innerRoundDiff.setPosition(new Vec3(0.0f, -0.5f, 0.75f));
                 cutBack = new Diff(cutBack, innerRoundDiff);
                 break;
         }


        cutBack.setPosition(new Vec3(x, y, z));
        return cutBack;
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
