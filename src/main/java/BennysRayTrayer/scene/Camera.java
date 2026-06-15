package BennysRayTrayer.scene;

import BennysRayTrayer.core.Ray;
import BennysRayTrayer.core.Vec3;

public class Camera {
    Vec3 position;
    double fov;
    Vec3 lookDirection;

    //for rotation
    private float rightLeft;
    private float upDown;

    public Camera(Vec3 position, double fov, Vec3 lookDirection) {
        this.position = position;
        this.fov = fov;
        setLookDirection(lookDirection);
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getLookDirection() {
        return lookDirection;
    }

    public void setLookDirection(Vec3 lookDirection) {
        Vec3 dir = lookDirection.normalize();
        this.lookDirection = dir;

        float clampedY = Math.max(-1.0f, Math.min(1.0f, dir.y));
        this.upDown = (float) Math.toDegrees(Math.asin(clampedY));
        this.rightLeft = (float) Math.toDegrees(Math.atan2(dir.x, -dir.z));
    }

    // Positiver Wert = nach rechts drehen, negativer = nach links.
    public void rotateYaw(int degrees) {
        this.rightLeft += degrees;
        updateLookDirectionFromAngles();
    }

    // direction: +1 (rechts) / -1 (links)
    public void rotateYaw(int degrees, int direction) {
        int dir = direction >= 0 ? 1 : -1;
        rotateYaw(degrees * dir);
    }

    // Positiver Wert = nach oben schauen, negativer = nach unten.
    public void rotatePitch(int degrees) {
        this.upDown += degrees;
        this.upDown = Math.max(-89.0f, Math.min(89.0f, this.upDown));
        updateLookDirectionFromAngles();
    }

    // direction: +1 (hoch) / -1 (runter)
    public void rotatePitch(int degrees, int direction) {
        int dir = direction >= 0 ? 1 : -1;
        rotatePitch(degrees * dir);
    }

    public Ray generateRay(int x, int y, int width, int height) {
        double aspectRatio = (double) width / height;
        double px = (2 * ((x + 0.5) / width) - 1) * Math.tan(Math.toRadians(fov / 2)) * aspectRatio;
        double py = (1 - 2 * ((y + 0.5) / height)) * Math.tan(Math.toRadians(fov / 2));

        Vec3 forward = lookDirection.normalize();
        Vec3 worldUp = new Vec3(0, 1, 0);

        // Falls forward fast parallel zu worldUp ist, alternative Up-Achse nehmen.
        if (Math.abs(forward.dot(worldUp)) > 0.999f) {
            worldUp = new Vec3(0, 0, 1);
        }

        Vec3 right = forward.cross(worldUp).normalize();
        Vec3 up = right.cross(forward).normalize();

        Vec3 rayDirection = forward
                .add(right.mul((float) px))
                .add(up.mul((float) py))
                .normalize();

        return new Ray(position, rayDirection);
    }

    private void updateLookDirectionFromAngles() {
        double yawRad = Math.toRadians(this.rightLeft);
        double pitchRad = Math.toRadians(this.upDown);

        float x = (float) (Math.sin(yawRad) * Math.cos(pitchRad));
        float y = (float) Math.sin(pitchRad);
        float z = (float) (-Math.cos(yawRad) * Math.cos(pitchRad));

        this.lookDirection = new Vec3(x, y, z).normalize();
    }
}
