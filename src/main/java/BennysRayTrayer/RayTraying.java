package BennysRayTrayer;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.input.Input;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.objects.Sphere;
import BennysRayTrayer.rendering.Light;
import BennysRayTrayer.rendering.Material;
import BennysRayTrayer.rendering.RayTracer;
import BennysRayTrayer.scene.Camera;
import BennysRayTrayer.scene.Scene;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class RayTraying {

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
        Camera cam = new Camera(new Vec3(0, 0, 3), 60, new Vec3(0, 0, -1));

        // Test-Szenario: Eine einfache Kugel
        Material testMat = new Material(
                new Vec3(0.8f, 0.2f, 0.2f),
                0.5,
                0.0,
                0.0,
                0.0,
                0.0
        );
        Sphere testSphere = new Sphere(new Vec3(0, 0, 0), 1.0, new Vec3(1, 1, 1));
        testSphere.setMaterial(testMat);

        Object3D[] objects = new Object3D[] { testSphere };

        Light[] lights = new Light[] {
                new Light(new Vec3(3, 4, 3), 1.0),
                new Light(new Vec3(-2, 2, 2), 0.5)
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
}
