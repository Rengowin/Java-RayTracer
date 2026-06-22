package BennysRayTrayer.objects;

import BennysRayTrayer.core.Hit;
import BennysRayTrayer.core.HitInterval;
import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;
import BennysRayTrayer.rendering.Material;

import java.util.List;

public abstract class Object3D {

    protected Color color;

    protected Material material;

    protected Transform transform;

    //vlt in den contructor packen

    public Object3D(Color color, Material material, Transform transform) {
        this.color = color;
        this.material = material;
        this.transform = transform;
    }

    public Object3D(Color color, Material material) {
        this.color = color;
        this.material = material;
        this.transform = new Transform();
    }


    public Object3D(Color color) {
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

    public Color getColor() {
        return color;
    }

    // Backwards-compatible: return color as Vec3
    public Vec3 getColorVec3() {
        return color == null ? null : color.toVec3();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Backwards-compatible setter accepting Vec3
    public void setColor(Vec3 color) {
        this.color = (color == null) ? null : new Color(color.x, color.y, color.z);
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
            if (interval.tExit > EPS) {
                return new Hit(interval.tExit,
                        ray.origin.add(ray.direction.mul((float) interval.tExit)),
                        interval.normalExit,
                        interval.objectExit);
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

    public Object3D setRotation(Vec3 rotation) {
        this.transform.setRotation(rotation);
        return this;
    }
    
    public void setTransform(Transform transform) {
        this.transform = transform;
    }
    
    public Transform getTransform() {
        return this.transform;
    }

    protected Vec3 toWorldPoint(Vec3 p) {
        return transform.localToWorldPoint(p);
    }

    protected Vec3 toWorldDirection(Vec3 d) {
        return transform.localToWorldDirection(d);
    }

    protected Vec3 toLocalPoint(Vec3 p) {
        return transform.worldToLocalPoint(p);
    }

    protected Vec3 toLocalDirection(Vec3 d) {
        return transform.worldToLocalDirection(d);
    }
}
