import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

import java.awt.*;

@ScriptManifest(category = Category.MISC, name = "RNG Visualizer", description = "Displays a graph of various random number distributions.", author = "Zonz", version = 1.0)

public class Main extends AbstractScript
{
    private double pointDistance, pointDiameter;
    private int curveSteps;
    private DataPoint highestPoint, lowestPoint;

    private DataPoint[] points;
    private Settings settings;

    @Override
    public void onStart()
    {
        settings = new Settings(this::uiUpdated);
        settings.openWindow();
    }

    private void uiUpdated(boolean reset)
    {
        pointDistance = (double)settings.graphWidth() / (settings.numPoints() + 1);
        pointDiameter = pointDistance / 2 + 3;
        curveSteps = Math.max(settings.numPoints() / 30, 1);

        if (reset)
        {
            highestPoint = null;
            lowestPoint = null;
            points = new DataPoint[settings.numPoints()];
            for (int i = 0; i < points.length; i++)
                points[i] = new DataPoint(i, (int)((i + 1) * pointDistance - pointDiameter / 2), (int)(settings.graphHeight() - pointDistance));
        }
        else
        {
            for (int i = 0; i < points.length; i++)
            {
                points[i].x = (int)((i + 1) * pointDistance - pointDiameter / 2);
                if (points[i].numHits == 0)
                    points[i].y = (int)(settings.graphHeight() - pointDistance);
            }
        }
    }

    @Override
    public void onPause() { settings.openWindow(); }

    @Override
    public void onExit() { settings.closeWindow(); }

    @Override
    public int onLoop()
    {
        if (points == null)
            return 1;

        int n = getRandomNumber();
        if (n < 0 || n >= points.length)
            return 1;
        DataPoint point = points[n];
        point.numHits++;

        for (DataPoint p : points)
        {
            if (highestPoint == null || p.numHits > highestPoint.numHits)
                highestPoint = p;
            if (lowestPoint == null || (p.numHits > 0 && p.numHits < lowestPoint.numHits))
                lowestPoint = p;
            if (highestPoint != null && p.numHits > 0)
                p.y = settings.graphHeight() - (int)pointDistance - (int)(((double)p.numHits / highestPoint.numHits) * (settings.graphHeight() - pointDistance));
        }

        return 1;
    }

    @Override
    public void onPaint(Graphics g)
    {
        if (points == null)
            return;

        g.setColor(Color.white);
        g.drawRect(0, 0, settings.graphWidth(), settings.graphHeight()); // Border

        g.setColor(Color.black);
        g.fillRect(1, 1, settings.graphWidth() - 1, settings.graphHeight() - 1); // Background

        if (settings.drawLines())
        {
            g.setColor(Color.darkGray);
            for (int i = 1; i <= settings.numPoints(); i++) // Vertical grid lines
            {
                int x = (int)(i * pointDistance);
                g.drawLine(x, 1, x, settings.graphHeight() - 1);
            }
        }

        if (settings.drawAverageCurve())
        {
            g.setColor(Color.yellow);
            Point[] path = getAveragePoints();
            for (int i = 1; i < path.length; i++)
                g.drawLine(path[i - 1].x, path[i - 1].y, path[i].x, path[i].y);

            if (settings.drawBezier())
            {
                g.setColor(Color.blue);
                path = BezierCurve.calculatePath(settings.bezierResolution(), trimPath(path));
                for (int i = 1; i < path.length; i++)
                    g.drawLine(path[i - 1].x, path[i - 1].y, path[i].x, path[i].y);
            }
        }
        else if (settings.drawBezier())
        {
            g.setColor(Color.blue);
            Point[] path = BezierCurve.calculatePath(settings.bezierResolution(), trimPath(getAveragePoints()));
            for (int i = 1; i < path.length; i++)
                g.drawLine(path[i - 1].x, path[i - 1].y, path[i].x, path[i].y);
        }

        if (settings.drawPoints())
        {
            if (highestPoint != null)
            {
                g.setColor(Color.green);
                g.drawString("Highest: " + highestPoint.index, settings.graphWidth() + 20, highestPoint.y + 10);
            }
            if (lowestPoint != null)
            {
                g.setColor(Color.orange);
                g.drawString("Lowest: " + lowestPoint.index, settings.graphWidth() + 20, lowestPoint.y + 10);
            }

            for (DataPoint p : points)
            {
                if (highestPoint != null && p.numHits == highestPoint.numHits)
                    g.setColor(Color.green);
                else if (p.numHits == 0)
                    g.setColor(Color.red);
                else if (lowestPoint != null && p.numHits == lowestPoint.numHits)
                    g.setColor(Color.orange);
                else
                    g.setColor(Color.white);

                g.fillOval(p.x, p.y, (int)pointDiameter, (int)pointDiameter);
            }
        }
    }

    private Point[] trimPath(Point[] path)
    {
        if (path.length <= 30)
            return path;

        double d = path.length * 0.05;
        int length = (int)(path.length / d);

        Point[] points = new Point[length + 1];
        for (int i = 0; i < length; i++)
            points[i] = path[(int)(i * d)];
        points[length] = path[path.length - 1];
        return points;
    }

    private Point[] getAveragePoints()
    {
        Point[] p = new Point[points.length];
        for (int i = 0; i < p.length; i++)
        {
            int average = findAverage(i - curveSteps, i + curveSteps);
            if (average < 0)
                p[i] = points[i];
            else
                p[i] = new Point((int)((i + 1) * pointDistance), (int)(average + pointDiameter / 2));
        }
        return p;
    }

    private int findAverage(int start, int end)
    {
        int dividend = 0;
        int divisor = 0;
        for (int i = start; i <= end; i++)
        {
            if (i < 0 || i >= points.length)
                continue;
            dividend += points[i].y;
            divisor++;
        }
        if (divisor == 0)
            return -1;
        return dividend / divisor;
    }

    private int getRandomNumber()
    {
        switch (settings.numberGenerator())
        {
            default:
            case Standard:
                return Random.nextInt(points.length);
            case Low:
                return Random.low(points.length);
            case Mid:
                return Random.mid(points.length);
            case High:
                return Random.high(points.length);
            case ParkAndMiller:
                return Random.nextParkAndMiller(points.length);
            case MersenneTwister:
                return Random.nextMersenneTwister(points.length);
            case W:
                return generateW();
            case Humps:
                return generateHumps();
            case Valley:
                return generateValley();
        }
    }

    private int generateW()
    {
        switch (Random.nextInt(3))
        {
            case 0:
                return Random.low(points.length / 2);
            case 1:
                return Random.mid(points.length / 3, points.length / 3 * 2);
            default:
                return Random.high(points.length / 2, points.length);
        }
    }

    private int generateHumps()
    {
        if (Random.nextInt(2) == 0)
            return Random.mid(points.length / 2);
        return Random.mid(points.length / 2, points.length);
    }

    private int generateValley()
    {
        switch (Random.nextInt(4))
        {
            case 0:
                return Random.low(points.length / 3);
            case 1:
                return Random.mid(points.length / 5 * 2);
            case 2:
                return Random.mid(points.length / 5 * 3, points.length);
            default:
                return Random.high(points.length / 3 * 2, points.length);
        }
    }

    private static class DataPoint extends Point
    {
        private final int index;
        private int numHits;

        private DataPoint(int index, int x, int y)
        {
            super(x, y);
            this.index = index;
            numHits = 0;
        }
    }
}
