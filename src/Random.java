
public class Random
{
    // All lower bounds are inclusive, all upper bounds are exclusive

    private static java.util.Random random = new java.util.Random();
    private static MersenneTwister MT = new MersenneTwister();

    public static int nextMersenneTwister(int bound) { return nextMersenneTwister(0, bound); }
    public static int nextMersenneTwister(int lowerBound, int upperBound)
    {
        if (lowerBound == upperBound) return lowerBound;
        int difference = upperBound - lowerBound;
        int mt = MT.nextInt();
        double d = (double)difference / Integer.MAX_VALUE;
        return (int)(mt * d) + lowerBound;
    }

    private static int next_p_m = (int)(System.currentTimeMillis() / 1000);
    public static int nextParkAndMiller(int bound) { return nextParkAndMiller(0, bound); }
    public static int nextParkAndMiller(int lowerBound, int upperBound)
    {
        if (lowerBound == upperBound) return lowerBound;
        do next_p_m = (int)((long)next_p_m * 48271 % Integer.MAX_VALUE);
        while (next_p_m < lowerBound || next_p_m >= upperBound + 1);
        return next_p_m - 1;
    }

    public static int nextInt(int bound) { return random.nextInt(bound); }
    public static int nextInt(int lowerBound, int upperBound) { return random.nextInt(upperBound - lowerBound) + lowerBound; }

    public static int nextGaussian(int bound, double deviation, double mean) { return nextGaussian(0, bound, deviation, mean); }
    public static int nextGaussian(int lowerBound, int upperBound, double deviation, double mean)
    {
        if (lowerBound == upperBound) return lowerBound;
        double result;
        do result = Math.abs(random.nextGaussian() * deviation + mean);
        while (result >= upperBound || result < lowerBound);
        return (int)(result);
    }

    public static int high(int bound) { return high(0, bound); }
    public static int high(int lowerBound, int upperBound)
    {
        double deviation = (upperBound - lowerBound) * 0.3;
        return nextGaussian(lowerBound, upperBound, deviation, upperBound);
    }

    public static int low(int bound) { return low(0, bound); }
    public static int low(int lowerBound, int upperBound)
    {
        double deviation = (upperBound - lowerBound) * 0.3;
        return nextGaussian(lowerBound, upperBound, deviation, lowerBound);
    }

    public static int mid(int bound) { return mid(0, bound); }
    public static int mid(int lowerBound, int upperBound)
    {
        int difference = upperBound - lowerBound;
        double mean = difference / 2d;
        double deviation = difference * 0.2;
        return nextGaussian(lowerBound, upperBound, deviation, mean + lowerBound);
    }

    private static class MersenneTwister
    {
        private final static int w = 32;
        private final static int n = 624;
        private final static int m = 397;
        private final static int r = 31;
        private final static long a = 0x9908B0DF;
        private final static int u = 11;
        private final static long d = 0xFFFFFFFF;
        private final static int s = 7;
        private final static long b = 0x9D2C5680;
        private final static int t = 15;
        private final static long c = 0xEFC60000;
        private final static int l = 18;
        private final static int f = 0x6C078965;
        private final static int lower_mask = (1 << r) - 1;
        private final static int upper_mask = ~lower_mask;

        private int[] MT = new int[n];
        private int index = n + 1;

        public void setSeed(int seed)
        {
            index = n;
            MT[0] = seed;
            for (int i = 1; i < n; i++)
                MT[i] = f * (MT[i - 1] ^ (MT[i - 1] >> (w - 2))) + i;
        }

        public int nextInt()
        {
            if (index >= n)
            {
                if (index > n)
                    setSeed((int)(System.currentTimeMillis() / 1000));
                twist();
            }

            int y = MT[index];
            y ^= (y >> u) & d;
            y ^= (y << s) & b;
            y ^= (y << t) & c;
            y ^= (y >> l);

            index++;
            return y;
        }

        private void twist()
        {
            for (int i = 0; i < n; i++)
            {
                int x = MT[i] & upper_mask + (MT[(i + 1) % n] & lower_mask);
                int xA = x >> 1;
                if (x % 2 != 0)
                    xA ^= a;
                MT[i] = MT[(i + m) % n] ^ xA;
            }
            index = 0;
        }
    }
}
