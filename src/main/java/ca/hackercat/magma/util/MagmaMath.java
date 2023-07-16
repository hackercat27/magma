package ca.hackercat.magma.util;

import ca.hackercat.magma.io.Window;
import ca.hackercat.magma.object.Camera;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;

import java.util.Random;

public final class MagmaMath {
    private MagmaMath() {}

    public static final float PIf = 3.1415927f;
    public static final double PId = 3.141592653589793d;

    public static Matrix4f getTransformationMatrix(Vector3f position, Quaternionf rotation, float scale) {
        return new Matrix4f()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }
    public static Matrix4f getTransformationMatrix(Vector3f position, Quaternionf rotation, Vector3f scale) {
        return new Matrix4f()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }

    public static Matrix4f getOrthographicMatrix(Camera c, Window window) {
        Matrix4f matrix = new Matrix4f().identity();

        float near = 0.1f;
        float far = 1000f;

        float ratio = (float) window.getWidth() / window.getHeight();

        float scale = 3;

        return matrix.ortho(-ratio * scale, ratio * scale, -scale, scale, near, far);
    }
    public static Matrix4f getProjectionMatrix(Window window, float fov) {

        float ratio = (float) window.getWidth() / window.getHeight();

        float near = 0.1f;
        float far = 1000f;

        Matrix4f matrix = new Matrix4f().identity();
        return matrix.perspective(fov, ratio, near, far);
    }
    public static Matrix4f getCameraTransformationMatrix(Vector3f position, Quaternionf rotation) {
        return new Matrix4f()
                .rotate(rotation)
                .translate(-position.x, -position.y, -position.z);
    }
    public static Matrix4f getCameraTransformationMatrix(Camera camera) {
        return getCameraTransformationMatrix(camera.getPosition(), camera.getRotation());
    }

    public static double ease(double value, double targetValue, double easingCoefficient) {
        double v = Math.abs(value - targetValue) * easingCoefficient;
        if (value - targetValue < 0) {
            return value + v;
        }
        if (value - targetValue > 0) {
            return value - v;
        }
        return value;
    }
    public static float ease(float value, float targetValue, float easingCoefficient) {
        float v = Math.abs(value - targetValue) * easingCoefficient;
        if (value - targetValue < 0) {
            return value + v;
        }
        if (value - targetValue > 0) {
            return value - v;
        }
        return value;
    }

    public static int round(float f) {
        if (f <= 0)
            return Math.round(f) - 1;
        return Math.round(f);
    }
    public static float floor(float f) {
        return Math.floor(f);
    }

    public static double currentTimeSeconds() {
        return System.currentTimeMillis() / 1000d;
    }

    public static double lerp(double a1, double a2, double t) {
//        if (t > 1 || t < 0) throw new IllegalArgumentException("t must be >= 0 and <= 1!");
        t = Math.max(Math.min(t, 1), 0);
        double val = a2 - a1;
        double a = val * t;
        return a + a1;
    }

    public static double cyclicalLerp(double a1, double a2, double t, double lowerBound, double upperBound) {
        double range = Math.abs(lowerBound - upperBound);
        double a3;
        if (a2 < a1) {
            a3 = a2 + range;
        }
        else {
            a3 = a2 - range;
        }
        double dist12 = Math.abs(a2 - a1);
        double dist13 = Math.abs(a3 - a1);
        if (dist12 < dist13) {
            return lerp(a1, a2, t);
        }
        return lerp(a1, a3, t);
    }

    public static Vector2d lerp(Vector2d a1, Vector2d a2, float t) {
        return new Vector2d(lerp(a1.x, a2.x, t), lerp(a1.y, a2.y, t));
    }
    public static Vector3d lerp(Vector3d a1, Vector3d a2, float t) {
        return new Vector3d(lerp(a1.x, a2.x, t), lerp(a1.y, a2.y, t), lerp(a1.z, a2.z, t));
    }
    public static Vector4d lerp(Vector4d a1, Vector4d a2, float t) {
        return new Vector4d(lerp(a1.x, a2.x, t), lerp(a1.y, a2.y, t), lerp(a1.z, a2.z, t), lerp(a1.w, a2.w, t));
    }

    public static float lerp(float a1, float a2, float t) {
        t = Math.max(Math.min(t, 1), 0);
        float val = a2 - a1;
        float a = val * t;
        return a + a1;
    }
    public static float cyclicalLerp(float a1, float a2, float t, float lowerBound, float upperBound) {
        float range = Math.abs(lowerBound - upperBound);
        float a3;
        if (a2 < a1) {
            a3 = a2 + range;
        }
        else {
            a3 = a2 - range;
        }
        double dist12 = Math.abs(a2 - a1);
        double dist13 = Math.abs(a3 - a1);
        if (dist12 < dist13) {
            return lerp(a1, a2, t);
        }
        return lerp(a1, a3, t);
    }

    public static Vector2f lerp(Vector2f a1, Vector2f a2, float t) {
        return new Vector2f(lerp(a1.x, a2.x, t), lerp(a1.y, a2.y, t));
    }
    public static Vector3f lerp(Vector3f a1, Vector3f a2, float t) {
        return new Vector3f(lerp(a1.x, a2.x, t), lerp(a1.y, a2.y, t), lerp(a1.z, a2.z, t));
    }
    public static Vector4f lerp(Vector4f a1, Vector4f a2, float t) {
        return new Vector4f(lerp(a1.x, a2.x, t), lerp(a1.y, a2.y, t), lerp(a1.z, a2.z, t), lerp(a1.w, a2.w, t));
    }

    public static Quaternionf lerp(Quaternionf a1, Quaternionf a2, float t) {
        return a1.nlerp(a2, t, new Quaternionf());
    }

    public static Quaternionf rotate(Quaternionf p, float angle, float x, float y, float z) {
        // TODO: fixme
        Quaternionf q = new Quaternionf().fromAxisAngleRad(x, y, z, angle);
        return p.mul(q, new Quaternionf());
    }

    public static float sin(float a) {
        return org.joml.Math.sin(a);
    }
    public static float cos(float a) {
        return org.joml.Math.cos(a);
    }
    public static float tan(float a) {
        return org.joml.Math.tan(a);
    }
    public static float asin(float a) {
        return org.joml.Math.asin(a);
    }
    public static float acos(float a) {
        return org.joml.Math.acos(a);
    }
    public static float atan(float a) {
        return (float) java.lang.Math.atan(a);
    }
    public static float atan2(float y, float x) {
        return org.joml.Math.atan2(y, x);
    }

    public static double sin(double a) {
        return java.lang.Math.sin(a);
    }
    public static double cos(double a) {
        return java.lang.Math.cos(a);
    }

    public static float hypot(float x, float y) {
        return sqrt(pow(x, 2) + pow(y, 2));
    }
    public static double hypot(double x, double y) {
        return sqrt(pow(x, 2) + pow(y, 2));
    }

    public static float pow(float base, float exponent) {
        return (float) java.lang.Math.pow(base, exponent);
    }
    public static double pow(double base, double exponent) {
        return java.lang.Math.pow(base, exponent);
    }
    public static float sqrt(float a) {
        return pow(a, 0.5f);
    }
    public static double sqrt(double a) {
        return pow(a, 0.5d);
    }

    private static final Random random = new Random();

    public static double randomd() {
        return random.nextDouble();
    }
    public static float randomf() {
        return random.nextFloat();
    }
}
