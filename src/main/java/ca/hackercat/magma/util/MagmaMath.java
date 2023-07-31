package ca.hackercat.magma.util;

import ca.hackercat.magma.io.Window;
import ca.hackercat.magma.object.Camera;
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
    private static final double DG_RDd = 0.017453292519943295;
    private static final double RD_DGd = 57.29577951308232;
    private static final float DG_RDf = 0.017453292f;
    private static final float RD_DGf = 57.29578f;

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

        float near = 0.01f;
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
        double v = Math.abs(value - targetValue) * (1 - clamp(0, 1, easingCoefficient));
        if (value - targetValue < 0) {
            return value + v;
        }
        if (value - targetValue > 0) {
            return value - v;
        }
        return value;
    }
    public static float ease(float value, float targetValue, float easingCoefficient) {
        float v = Math.abs(value - targetValue) * (1 - clamp(0, 1, easingCoefficient));
        if (value - targetValue < 0) {
            return value + v;
        }
        if (value - targetValue > 0) {
            return value - v;
        }
        return value;
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

    public static float log(float a) {
        return (float) java.lang.Math.log(a);
    }
    public static float log10(float a) {
        return (float) java.lang.Math.log10(a);
    }
    public static double log(double a) {
        return java.lang.Math.log(a);
    }
    public static double log10(double a) {
        return java.lang.Math.log10(a);
    }

    public static float toRadians(float a) {
        return a * DG_RDf;
    }
    public static float toDegrees(float a) {
        return a * RD_DGf;
    }
    public static double toRadians(double a) {
        return a * DG_RDd;
    }
    public static double toDegrees(double a) {
        return a * RD_DGd;
    }

    public static double abs(double a) {
        return (a < 0d? 0d - a : a);
    }
    public static float abs(float a) {
        return (a < 0f? 0f - a : a);
    }
    public static int abs(int a) {
        return (a < 0? -a : a);
    }
    public static long abs(long a) {
        return (a < 0L? -a : a);
    }

    public static float max(float a, float b) {
        return a > b? a : b;
    }
    public static double max(double a, double b) {
        return a > b? a : b;
    }
    public static int max(int a, int b) {
        return a > b? a : b;
    }
    public static long max(long a, long b) {
        return a > b? a : b;
    }
    public static float min(float a, float b) {
        return a < b? a : b;
    }
    public static double min(double a, double b) {
        return a < b? a : b;
    }
    public static int min(int a, int b) {
        return a < b? a : b;
    }
    public static long min(long a, long b) {
        return a < b? a : b;
    }
    public static float clamp(float lower, float upper, float value) {
        return min(upper, max(lower, value));
    }
    public static double clamp(double lower, double upper, double value) {
        return min(upper, max(lower, value));
    }
    public static int clamp(int lower, int upper, int value) {
        return min(upper, max(lower, value));
    }
    public static long clamp(long lower, long upper, long value) {
        return min(upper, max(lower, value));
    }

    private static final Random random = new Random();

    public static double randomd() {
        return random.nextDouble();
    }
    public static float randomf() {
        return random.nextFloat();
    }

    public static Vector2f intersection(Vector2f p1, Vector2f dir1, Vector2f p2, Vector2f dir2) {
        float m1 = dir1.y / dir1.x;
        float m2 = dir2.y / dir2.x;

        float b1 = p1.y;
        float b2 = p2.y;

        return intersection(m1, b1, m2, b2);
    }
    public static Vector2f intersection(float m1, float b1, float m2, float b2) {
        if (m1 == m2) {
            return new Vector2f(Float.NaN, Float.NaN);
        }

        float x = (b2 - b1) / (m1 - m2);
        float y = (m1 * x) + b1;

        return new Vector2f(x, y);
    }
}
