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
                        interval.normalExit.mul(-1),
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

    public void multiplyScale(Vec3 factor) {
        this.transform.multiplyScale(factor);
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

    public Material getMaterialAt(Vec3 worldPoint) {
        return getMaterial(); // Default: einfach das globale Material
    }

    public Object3D setLocalPosition(Vec3 position) {
        transform.setPosition(position);
        return this;
    }

    public Object3D setLocalRotation(Vec3 rotation) {
        transform.setRotation(rotation);
        return this;
    }

    public Object3D setLocalScale(Vec3 scale) {
        transform.setScale(scale);
        return this;
    }

    public double intersectDistance(
            Ray ray,
            double maxDistance
    ) {
        Hit hit = intersect(ray);

        if (hit == null
                || hit.t <= 0.0
                || hit.t >= maxDistance) {
            return Double.POSITIVE_INFINITY;
        }

        return hit.t;
    }

    public Hit createHit(
            Ray ray,
            double distance
    ) {
        return intersect(ray);
    }
}
