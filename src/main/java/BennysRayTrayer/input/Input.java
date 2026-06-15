package BennysRayTrayer.input;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.rendering.RayTracer;
import BennysRayTrayer.scene.Camera;
import BennysRayTrayer.scene.Scene;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.MemoryImageSource;

import javax.swing.JFrame;

public class Input {

    public static void bind(JFrame frame, Camera cam, Scene scene, int resX, int resY, int[] pixels, MemoryImageSource mis) {
        final float step = 0.5f;

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                boolean shouldRender = false;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        cam.setPosition(cam.getPosition().add(new Vec3(0, 0, -step)));
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_S:
                        cam.setPosition(cam.getPosition().add(new Vec3(0, 0, step)));
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_A:
                        cam.setPosition(cam.getPosition().add(new Vec3(-step, 0, 0)));
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_D:
                        cam.setPosition(cam.getPosition().add(new Vec3(step, 0, 0)));
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_SPACE:
                        cam.setPosition(cam.getPosition().add(new Vec3(0, step, 0)));
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_SHIFT:
                        cam.setPosition(cam.getPosition().add(new Vec3(0, -step, 0)));
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_Q:
                        cam.rotateYaw(45, -1);
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_E:
                        cam.rotateYaw(45, 1);
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_X:
                        cam.rotatePitch(45, 1);
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_Y:
                        cam.rotatePitch(45, -1);
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_R:
                        //increase depth by 1 in RayTracer
                        RayTracer.setDepth(RayTracer.getDepth()+1);
                        shouldRender = true;
                        break;
                    case KeyEvent.VK_T:
                        //decrease depth by 1 in RayTracer
                        RayTracer.setDepth(RayTracer.getDepth()-1);
                        shouldRender = true;
                        break;
                    default:
                        break;
                }

                if (shouldRender) {
                    RayTracer.render(resX, resY, scene, pixels);
                    mis.newPixels();
                }
            }
        });
    }

    // movement of the cammera, rotation from cammera via q,e (left/right), x,y (up/down) in 45 GRAD


    // increase/decrease anz refection
    

    // Path Tracing (anz Ray) Settings for maybe the 4th abgabe (also für jetzt ignorieren)


    // fiter on/off
    // Entrauschungsfilter von BV


}
