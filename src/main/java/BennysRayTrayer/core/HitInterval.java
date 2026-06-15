package BennysRayTrayer.core;

import BennysRayTrayer.objects.Object3D;

public class HitInterval {
    public double tEnter;
    public double tExit;

    public Vec3 normalEnter;
    public Vec3 normalExit;

    public Object3D objectEnter;
    public Object3D objectExit;

    public HitInterval(double tEnter, double tExit, Vec3 normalEnter, Vec3 normalExit, Object3D object) {
        this.tEnter = tEnter;
        this.tExit = tExit;
        this.normalEnter = normalEnter;
        this.normalExit = normalExit;
        this.objectEnter = object;
        this.objectExit = object;
    }
}
