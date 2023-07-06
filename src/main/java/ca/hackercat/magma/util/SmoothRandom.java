package ca.hackercat.magma.util;

import java.util.Random;

public class SmoothRandom {

    private Random random;

    private double value, lastValue;

    double period;

    public SmoothRandom(int period) {
        this.period = period;
        random = new Random();
        init();
    }
    public SmoothRandom(int period, long seed) {
        this.period = period;
        random = new Random(seed);
        init();
    }
    private void init() {
        lastValue = random.nextDouble();
    }

    double counter;
    public double nextDouble(double deltaTime) {
        counter += deltaTime;
        if (counter > period) {
            lastValue = value;
            value = random.nextDouble();
            counter = 0;
        }


        return quadraticLerp(lastValue, value, counter / period);
    }
    public float nextFloat(float deltaTime) {
        return (float) nextDouble(deltaTime);
    }

    private static double quadraticLerp(double a1, double a2, double t) {
        double y;
        if (t < 0.5) {
            y = 2 * Math.pow(t, 2);
        }
        else {
            y = 1 - (2 * Math.pow(t - 1, 2));
        }
        return MagmaMath.lerp(a1, a2, y);
    }



}
