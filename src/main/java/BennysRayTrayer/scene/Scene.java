package BennysRayTrayer.scene;

import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.objects.Color;
import BennysRayTrayer.objects.Object3D;
import BennysRayTrayer.rendering.Light;

public class Scene {

    Object3D[] objects;
    Light[] lights;
    Camera camera;
    Color backgroundColor;

    public Scene(Camera camera, Object3D[] objects, Light[] lights) {
        this.camera = camera;
        this.objects = objects;
        this.lights = lights;
        this.backgroundColor = Color.of(0.125f, 0.125f, 0.25f); // Standard Hintergrundfarbe
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


    public Color getBackgroundColor() {
        return backgroundColor;
    }

    // Backwards-compatible Vec3 accessor
    public Vec3 getBackgroundColorVec3() {
        return backgroundColor == null ? null : backgroundColor.toVec3();
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    // Backwards-compatible setter accepting Vec3
    public void setBackgroundColor(Vec3 color) {
        this.backgroundColor = (color == null) ? null : Color.of(color.x, color.y, color.z);
    }

    public void addObject(Object3D object) {
        Object3D[] newObjects = new Object3D[objects.length + 1];
        System.arraycopy(objects, 0, newObjects, 0, objects.length);
        newObjects[objects.length] = object;
        objects = newObjects;
    }
}
