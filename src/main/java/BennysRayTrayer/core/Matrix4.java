package BennysRayTrayer.core;

public class Matrix4 {
    public float[][] m;

    public Matrix4() {

        m = new float[][] {
                // x,y, im eindim sollten die per reihe dann stehen
                { 1, 0, 0, 0 },
                { 0, 1, 0, 0 },
                { 0, 0, 1, 0 },
                { 0, 0, 0, 1 }
        };
    }

    public Matrix4(Matrix4 copy) {
        m = new float[][] {
                { copy.m[0][0], copy.m[0][1], copy.m[0][2], copy.m[0][3] },
                { copy.m[1][0], copy.m[1][1], copy.m[1][2], copy.m[1][3] },
                { copy.m[2][0], copy.m[2][1], copy.m[2][2], copy.m[2][3] },
                { copy.m[3][0], copy.m[3][1], copy.m[3][2], copy.m[3][3] }
        };
    }

    public Matrix4(float near, float far) {
        m = new float[][] {
                { near, 0, 0, 0 },
                { 0, near, 0, 0 },
                { 0, 0, -(far + near) / (far - near), -2 * far * near / (far - near) },
                { 0, 0, -1, 0 }
        };
    }

    public Matrix4 multiply(Matrix4 other) {
        float[][] temp = new float[4][4];
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                temp[x][y] = 0; // Initialize the value to 0
                for (int i = 0; i < 4; i++) {
                    temp[x][y] += m[x][i] * other.m[i][y];
                }
            }
        }
        m = temp;
        // reihe mal spalte
        return this;
    }

    public Matrix4 translate(float x, float y, float z) {
        Matrix4 translateMatrix = new Matrix4();
        translateMatrix.m[3][0] += x;
        translateMatrix.m[3][1] += y;
        translateMatrix.m[3][2] += z;
        return this.multiply(translateMatrix);
    }

    public Matrix4 scale(float uniformFactor) {
        Matrix4 scalingMatrix = new Matrix4();
        scalingMatrix.m[0][0] = uniformFactor;
        scalingMatrix.m[1][1] = uniformFactor;
        scalingMatrix.m[2][2] = uniformFactor;
        return this.multiply(scalingMatrix);
    }

    public Matrix4 scale(float sx, float sy, float sz) {
        Matrix4 scalingMatrix = new Matrix4();
        scalingMatrix.m[0][0] = sx;
        scalingMatrix.m[1][1] = sy;
        scalingMatrix.m[2][2] = sz;
        return this.multiply(scalingMatrix);
    }

    public Matrix4 rotateX(float angle) {
        Matrix4 rotation = new Matrix4();
        rotation.m[1][1] = (float) Math.cos(angle);
        rotation.m[1][2] = (float) Math.sin(angle);
        rotation.m[2][1] = (float) -Math.sin(angle);
        rotation.m[2][2] = (float) Math.cos(angle);
        return this.multiply(rotation);
    }

    public Matrix4 rotateY(float angle) {
        Matrix4 rotation = new Matrix4();
        rotation.m[0][0] = (float) Math.cos(angle);
        rotation.m[0][2] = (float) Math.sin(angle);
        rotation.m[2][0] = (float) -Math.sin(angle);
        rotation.m[2][2] = (float) Math.cos(angle);
        return this.multiply(rotation);
    }

    public Matrix4 rotateZ(float angle) {
        Matrix4 rotation = new Matrix4();
        rotation.m[0][0] = (float) Math.cos(angle);
        rotation.m[0][1] = (float) Math.sin(angle);
        rotation.m[1][0] = (float) -Math.sin(angle);
        rotation.m[1][1] = (float) Math.cos(angle);
        return this.multiply(rotation);
    }

    public float[] getValuesAsArray() {
        float[] values = new float[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                values[i * 4 + j] = m[i][j]; // Spaltenweise füllen
            }
        }
        return values;
    }

    public Vec3 multiplyPoint(Vec3 point) {
        float x = point.x * m[0][0] + point.y * m[1][0] + point.z * m[2][0] + m[3][0];
        float y = point.x * m[0][1] + point.y * m[1][1] + point.z * m[2][1] + m[3][1];
        float z = point.x * m[0][2] + point.y * m[1][2] + point.z * m[2][2] + m[3][2];
        return new Vec3(x, y, z);
    }

    public Vec3 multiplyDirection(Vec3 dir){
        float x = dir.x * m[0][0] + dir.y * m[1][0] + dir.z * m[2][0];
        float y = dir.x * m[0][1] + dir.y * m[1][1] + dir.z * m[2][1];
        float z = dir.x * m[0][2] + dir.y * m[1][2] + dir.z * m[2][2];
        return new Vec3(x, y, z);
    }

}
