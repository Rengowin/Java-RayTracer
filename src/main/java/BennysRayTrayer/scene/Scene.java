package BennysRayTrayer.scene;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.rendering.Light;

public class Scene {

    Object3D[] objects;
    Light[] lights;
    Camera camera;
    Vec3 backgroundColor;

    public Scene(Camera camera, Object3D[] objects, Light[] lights) {
        this.camera = camera;
        this.objects = objects;
        this.lights = lights;
        this.backgroundColor = new Vec3(0.125f, 0.125f, 0.25f); // Standard Hintergrundfarbe
    }

    public Object3D[] getObjects() {
        return objects;
    }

    public Light[] getLights() {
        return lights;
    }

    public Camera getCamera() {
        return camera;
    }

    public Vec3 getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Vec3 color) {
        this.backgroundColor = color;
    }
}
