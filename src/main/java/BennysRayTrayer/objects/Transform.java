package BennysRayTrayer.objects;

import BennysRayTrayer.core.Matrix4;
import BennysRayTrayer.core.Vec3;

public class Transform
{
    private Matrix4 matrix;
    
    public Transform()
    {
        matrix = new Matrix4();
    }

    public Transform(Vec3 position, Vec3 rotation, Vec3 scale)
    {
        matrix = new Matrix4();
        setPosition(position);
        setRotation(rotation);
        setScale(scale);
    }
    
    public Transform(Matrix4 matrix)
    {
        this.matrix = new Matrix4(matrix);
    }
    
    public Matrix4 getMatrix() {
        return matrix;
    }
    
    public void setMatrix(Matrix4 matrix) {
        this.matrix = new Matrix4(matrix);
    }
    
    public void setPosition(Vec3 position) {
        matrix.m[3][0] = position.x;
        matrix.m[3][1] = position.y;
        matrix.m[3][2] = position.z;
    }
    
    public void setRotation(Vec3 rotation) {
        // Reset the rotation part of the matrix
        matrix.m[0][0] = 1; matrix.m[0][1] = 0; matrix.m[0][2] = 0;
        matrix.m[1][0] = 0; matrix.m[1][1] = 1; matrix.m[1][2] = 0;
        matrix.m[2][0] = 0; matrix.m[2][1] = 0; matrix.m[2][2] = 1;
        
        // Apply rotations
        double rx = Math.toRadians(rotation.x);
        double ry = Math.toRadians(rotation.y);
        double rz = Math.toRadians(rotation.z);
        
        // Rotation order: Z, Y, X (right to left application)
        double cosX = Math.cos(rx), sinX = Math.sin(rx);
        double cosY = Math.cos(ry), sinY = Math.sin(ry);
        double cosZ = Math.cos(rz), sinZ = Math.sin(rz);
        
        // Combined rotation matrix
        matrix.m[0][0] = (float)(cosY * cosZ);
        matrix.m[0][1] = (float)(cosY * sinZ);
        matrix.m[0][2] = (float)(-sinY);
        
        matrix.m[1][0] = (float)(sinX * sinY * cosZ - cosX * sinZ);
        matrix.m[1][1] = (float)(sinX * sinY * sinZ + cosX * cosZ);
        matrix.m[1][2] = (float)(sinX * cosY);
        
        matrix.m[2][0] = (float)(cosX * sinY * cosZ + sinX * sinZ);
        matrix.m[2][1] = (float)(cosX * sinY * sinZ - sinX * cosZ);
        matrix.m[2][2] = (float)(cosX * cosY);
    }
    
    public void setScale(Vec3 scale) {
        // Apply scaling to the matrix rotation part
        float scaleX = matrix.m[0][0] == 0 ? scale.x : (scale.x / Math.abs(matrix.m[0][0]));
        float scaleY = matrix.m[1][1] == 0 ? scale.y : (scale.y / Math.abs(matrix.m[1][1]));
        float scaleZ = matrix.m[2][2] == 0 ? scale.z : (scale.z / Math.abs(matrix.m[2][2]));
        
        matrix.m[0][0] *= scale.x;
        matrix.m[0][1] *= scale.x;
        matrix.m[0][2] *= scale.x;
        
        matrix.m[1][0] *= scale.y;
        matrix.m[1][1] *= scale.y;
        matrix.m[1][2] *= scale.y;
        
        matrix.m[2][0] *= scale.z;
        matrix.m[2][1] *= scale.z;
        matrix.m[2][2] *= scale.z;
    }
    
    public Vec3 getPosition() {
        return new Vec3(matrix.m[3][0], matrix.m[3][1], matrix.m[3][2]);
    }
    
    public Vec3 getScale() {
        float scaleX = (float) Math.sqrt(matrix.m[0][0] * matrix.m[0][0] + matrix.m[1][0] * matrix.m[1][0] + matrix.m[2][0] * matrix.m[2][0]);
        float scaleY = (float) Math.sqrt(matrix.m[0][1] * matrix.m[0][1] + matrix.m[1][1] * matrix.m[1][1] + matrix.m[2][1] * matrix.m[2][1]);
        float scaleZ = (float) Math.sqrt(matrix.m[0][2] * matrix.m[0][2] + matrix.m[1][2] * matrix.m[1][2] + matrix.m[2][2] * matrix.m[2][2]);
        return new Vec3(scaleX, scaleY, scaleZ);
    }
}
