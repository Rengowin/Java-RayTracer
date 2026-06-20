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
                new Vec3(0, 1.5f, 9),
                60,
                new Vec3(0, -0.2f, -1)
        );

        //Materials
        Material gold = new Material(
                Color.ofRGB(255, 200, 40).toVec3(),
                0.6,
                0.0,   // kein Metall
                0.0,   // keine Reflection
                0.0,
                1.0
        );

        //Tests
        Object3D floorBase = Quadric.cylinderY(Color.gray());

        floorBase.setScale(new Vec3(2f, 1f, 2f));

        Object3D floor = new Cut(
                new Cut(floorBase, HalfSpace.yLess(-1.1f, Color.gray())),
                HalfSpace.yGreater(-1.3f, Color.gray())
        );

        Object3D emblemBeforCut = Quadric.cylinderY(Color.cyan());
        emblemBeforCut.setScale(new Vec3(0.750f, 1, 0.750f));

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

        Object3D sphereForCut = Quadric.sphere(Color.gray());
        sphereForCut.setScale(new Vec3(1.8f, 2.5f, 1.8f));

        Object3D outerRing = new Diff(outerRingBeforDiffWithSpehere,sphereForCut);

        Object3D petalRight = createPetalWithBiggerY(1.9f, -0.2f, 0f, 0f);
        Object3D petalLeft  = createPetalWithBiggerY(-1.9f, -0.2f, 0f, 0f);

        Object3D petalFront = createPetalWithBiggerY(0f, -0.2f, 1.9f, 90f);
        Object3D petalBack  = createPetalWithBiggerY(0f, -0.2f, -1.9f, 270f);

        Object3D[] objects = new Object3D[] {floor, emblem, outerRing, petalRight, petalLeft, petalFront, petalBack};


        Light[] lights = new Light[] {
                new Light(new Vec3(3, 5, 5), 1.2),
                new Light(new Vec3(-4, 3, 2), 0.5),
                new Light(new Vec3(0, -15,0), 1.0)
        };

        Scene scene = new Scene(cam, objects, lights);

        // === Initiales Rendern ===
        RayTracer.render(resX, resY, scene, pixels);
        mis.newPixels();

        // === Input verbinden ===
        Input.bind(frame, cam, scene, resX, resY, pixels, mis);

        frame.setFocusable(true);
        frame.requestFocus();
    }

    private static Object3D createPetalWithBiggerY(float x, float y, float z, float rotY) {
        Object3D petalBase = Quadric.cylinderY(Color.gray());
        petalBase.setScale(new Vec3(1.2f, 1f, 0.75f));

        Object3D cutY = new Cut(
                new Cut(petalBase, HalfSpace.yLess(-0.25f, Color.gray())),
                HalfSpace.yGreater(-1.2f, Color.gray())
        );

        Object3D cutZ = new Cut(
                new Cut(cutY, HalfSpace.zLess(0.25f, Color.gray())),
                HalfSpace.zGreater(-0.25f, Color.gray())
        );

        Object3D cutBack = new Cut(
                cutZ,
                HalfSpace.xGreater(-0.8f, Color.gray())
        );

        Object3D cutSlope = new Cut(
                cutBack,
                slopeLeft(0.75f, -0f, Color.gray())
        );

        Object3D innerRoundCut = Quadric.cylinderZ(Color.gray());
        innerRoundCut.setScale(new Vec3(0.95f, 1.25f, 1f));
        innerRoundCut.setPosition(new Vec3(-0.95f, -0.2f, 0f));

        Object3D diffInnerSide = new Diff(
                cutSlope,
                innerRoundCut
        );

        Object3D cutRightSide = new Cut(
                diffInnerSide,
                HalfSpace.xLess(0.55f, Color.gray())
        );


        cutRightSide.setRotation(new Vec3(0, rotY, 0));
        cutRightSide.setPosition(new Vec3(x, y, z));

        return cutRightSide;
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
