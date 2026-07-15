package BennysRayTrayer.objects;

import BennysRayTrayer.core.Matrix4;
import BennysRayTrayer.core.Vec3;

public class Transform {

    private Vec3 position = new Vec3(0, 0, 0);
    private Vec3 rotation = new Vec3(0, 0, 0); // Grad
    private Vec3 scale = new Vec3(1, 1, 1);

    private Matrix4 localToWorld = new Matrix4();
    private Matrix4 worldToLocal = new Matrix4();

    public void setPosition(Vec3 position) {
        this.position = position;
        rebuildMatrices();
    }

    public void setRotation(Vec3 rotation) {
        this.rotation = rotation;
        rebuildMatrices();
    }

    public void setScale(Vec3 scale) {
        this.scale = scale;
        rebuildMatrices();
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getRotation() {
        return rotation;
    }

    public Vec3 getScale() {
        return scale;
    }

    public Matrix4 getLocalToWorld() {
        return localToWorld;
    }

    public Matrix4 getWorldToLocal() {
        return worldToLocal;
    }

    public Vec3 localToWorldPoint(Vec3 p) {
        return localToWorld.multiplyPoint(p);
    }

    public Vec3 localToWorldDirection(Vec3 d) {
        return localToWorld.multiplyDirection(d);
    }

    public Vec3 worldToLocalPoint(Vec3 p) {
        return worldToLocal.multiplyPoint(p);
    }

    public Vec3 worldToLocalDirection(Vec3 d) {
        return worldToLocal.multiplyDirection(d);
    }

    private void rebuildMatrices() {
        localToWorld = new Matrix4()
                .translate(position.x, position.y, position.z)
                .rotateX((float)Math.toRadians(rotation.x))
                .rotateY((float)Math.toRadians(rotation.y))
                .rotateZ((float)Math.toRadians(rotation.z))
                .scale(scale.x, scale.y, scale.z);

        worldToLocal = new Matrix4()
                .scale(1f / scale.x, 1f / scale.y, 1f / scale.z)
                .rotateZ((float)Math.toRadians(-rotation.z))
                .rotateY((float)Math.toRadians(-rotation.y))
                .rotateX((float)Math.toRadians(-rotation.x))
                .translate(-position.x, -position.y, -position.z);
    }
}
