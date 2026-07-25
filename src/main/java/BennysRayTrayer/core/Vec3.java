package BennysRayTrayer.core;

public class Vec3 {
    public float x;
    public float y;
    public float z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //for fresnel metal trick :D
    public Vec3(float all) {
        this.x = all;
        this.y = all;
        this.z = all;
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vec3 sub(Vec3 other) {
        return new Vec3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vec3 mul(float scalar) {
        return new Vec3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vec3 mul(Vec3 other) {
        return new Vec3(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public Vec3 div(float scalar){
        return new Vec3(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    public Vec3 clamp(float min, float max){
        return new Vec3(Math.max(min, Math.min(max, this.x)), Math.max(min, Math.min(max, this.y)), Math.max(min, Math.min(max, this.z)));
    }

    public float dot(Vec3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public float length() {
        return (float) Math.sqrt(this.dot(this));
    }

    public Vec3 normalize() {
        float len = this.length();
        // Verhindert Division durch 0 beim Nullvektor.
        if (len == 0.0f) {
            return new Vec3(0.0f, 0.0f, 0.0f);
        }
        return this.mul(1.0f / len);
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    @Override
    public String toString() {
        return "Vec3(" + x + ", " + y + ", " + z + ")";
    }
}
