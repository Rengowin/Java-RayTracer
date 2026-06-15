package BennysRayTrayer.objects;

import BennysRayTrayer.core.Hit;
import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.rendering.Material;

import java.util.List;

public abstract class Object3D {

    protected Vec3 color;

    protected Material material;

    protected Transform transform;

    //vlt in den contructor packen

    public Object3D(Vec3 color, Material material, Transform transform) {
        this.color = color;
        this.material = material;
        this.transform = transform;
    }

    public Object3D(Vec3 color, Material material) {
        this.color = color;
        this.material = material;
        this.transform = new Transform();
    }

    public Object3D(Vec3 color) {
        this.color = color;
        this.transform = new Transform();
    }

    public Object3D(Material material) {
        this.material = material;
        this.transform = new Transform();
    }

    public Object3D() {
        this.transform = new Transform();
    }

    public Vec3 getColor() {
        return color;
    }

    public void setColor(Vec3 color) {
        this.color = color;
    }

    public void setMaterial(Material material) { this.material = material;}

    public Material getMaterial() {
        return material;
    }

    public abstract List<HitInterval> intersectIntervals(Ray ray);

    // Konkrete Implementierung: findet den nächsten sichtbaren Hit aus den Intervallen
    public Hit intersect(Ray ray) {
        final double EPS = 1e-6;
        List<HitInterval> intervals = intersectIntervals(ray);
        
        if (intervals == null || intervals.isEmpty()) {
            return null;
        }

        for (HitInterval interval : intervals) {
            if (interval.tEnter > EPS) {
                return new Hit(interval.tEnter, 
                        ray.origin.add(ray.direction.mul((float) interval.tEnter)),
                        interval.normalEnter, 
                        interval.objectEnter);
            }
        }
        return null;
    }

    //translation, rotation, scaling
    public void setPosition(Vec3 position) {
        this.transform.setPosition(position);
    }

    public void setScale(Vec3 scale) {
        this.transform.setScale(scale);
    }

    public void setRotation(Vec3 rotation) {
        this.transform.setRotation(rotation);
    }
    
    public void setTransform(Transform transform) {
        this.transform = transform;
    }
    
    public Transform getTransform() {
        return this.transform;
    }

    protected Vec3 transformPoint(Vec3 p) {
        return this.transform.getMatrix().multiplyPoint(p);
    }

    protected Vec3 transformDirection(Vec3 d) {
        return this.transform.getMatrix().multiplyDirection(d);
    }
    
    // Legacy method - for backward compatibility
    protected Vec3 rotate(Vec3 v) {
        return this.transform.getMatrix().multiplyDirection(v);
    }

    // Legacy method - would need matrix inversion for proper inverse
    protected Vec3 inverseRotate(Vec3 v) {
        // This is a simplified version - proper implementation would require matrix inversion
        return v;
    }
}
