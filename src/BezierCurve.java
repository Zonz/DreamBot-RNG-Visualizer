
import java.awt.*;

public class BezierCurve
{
    public static Point[] calculatePath(int resolution, Point[] controlPoints)
    {
        assert resolution > 0 && controlPoints != null && controlPoints.length > 1;
        Point[] path = new Point[resolution + 1];
        for (int i = 0; i <= resolution; i++)
            path[i] = calculate(controlPoints, (double)i / resolution);
        return path;
    }

    private static Point calculate(Point[] points, double t)
    {
        assert t >= 0 && t <= 1;
        int x = 0;
        int y = 0;
        int n = points.length - 1;
        for (int i = 0; i <= n; i++)
        {
            double m = choose(n, i) * Math.pow(1 - t, n - i) * Math.pow(t, i);
            x += m * points[i].x;
            y += m * points[i].y;
        }
        return new Point(x, y);
    }

    private static long choose(int n, int k)
    {
        long result = 1;
        for (int i = 1; i <= k; i++)
        {
            result *= n - (k - i);
            result /= i;
        }
        return result;
    }
}
