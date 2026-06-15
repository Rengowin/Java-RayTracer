package BennysRayTrayer.core;

import BennysRayTrayer.objects.Object3D;

public class Hit {
    public double t;

    public Vec3 position;
    public Vec3 normal;

    public Object3D object;

    public Hit(double t, Vec3 position, Vec3 normal, Object3D object) {
        this.t = t;
        this.position = position;
        this.normal = normal;
        this.object = object;
    }
}
