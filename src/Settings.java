
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.function.Consumer;

public class Settings
{
    private JFrame frame;
    private IntSpinner numPointsSpinner;
    private JSlider graphWidthSlider, graphHeightSlider, bezierResolutionSlider;
    private JCheckBox drawLinesChkBx, drawPointsChkBx, drawAverageChkBx, drawBezierChkBx;
    private JComboBox<RNG> rngComboBox;

    private Consumer<Boolean> uiUpdated;

    public Settings(Consumer<Boolean> uiUpdated) { this.uiUpdated = uiUpdated; }

    public void openWindow()
    {
        if (frame != null)
        {
            frame.setVisible(true);
            return;
        }

        frame = new JFrame();
        frame.setTitle("Generator Settings");
        frame.setBounds(100, 100, 465, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));

        frame.add(new JLabel("Num Points:"));
        numPointsSpinner = new IntSpinner(1, 1000, 300, true);
        numPointsSpinner.addChangeListener(e -> updateUI(e, true));
        frame.add(numPointsSpinner);

        frame.add(new JLabel("Number Generator:"));
        rngComboBox = new JComboBox<>(RNG.values());
        rngComboBox.addActionListener(e -> updateUI(new ChangeEvent(rngComboBox), true));
        frame.add(rngComboBox);

        frame.add(new JLabel("Graph Width:"));
        graphWidthSlider = new JSlider(1, 764, 764);
        graphWidthSlider.setToolTipText("Value: 764");
        graphWidthSlider.addChangeListener(e -> updateUI(e, false));
        frame.add(graphWidthSlider);

        frame.add(new JLabel("Graph Height:"));
        graphHeightSlider = new JSlider(1, 500, 500);
        graphHeightSlider.setToolTipText("Value: 500");
        graphHeightSlider.addChangeListener(e -> updateUI(e, false));
        frame.add(graphHeightSlider);

        drawLinesChkBx = new JCheckBox("Draw Lines");
        drawLinesChkBx.addChangeListener(e -> updateUI(e, false));
        frame.add(drawLinesChkBx);

        drawPointsChkBx = new JCheckBox("Draw Points");
        drawPointsChkBx.setSelected(true);
        drawPointsChkBx.addChangeListener(e -> updateUI(e, false));
        frame.add(drawPointsChkBx);

        drawAverageChkBx = new JCheckBox("Draw Average");
        drawAverageChkBx.setSelected(true);
        drawAverageChkBx.addChangeListener(e -> updateUI(e, false));
        frame.add(drawAverageChkBx);

        drawBezierChkBx = new JCheckBox("Draw Bezier");
        drawBezierChkBx.setSelected(true);
        drawBezierChkBx.addChangeListener(e ->
        {
            bezierResolutionSlider.setEnabled(drawBezierChkBx.isSelected());
            updateUI(e, false);
        });
        frame.add(drawBezierChkBx);

        frame.add(new JLabel("Bezier Resolution:"));
        bezierResolutionSlider = new JSlider(1, 60, 60);
        bezierResolutionSlider.setToolTipText("Value: 60");
        bezierResolutionSlider.addChangeListener(e -> updateUI(e, false));
        frame.add(bezierResolutionSlider);

        frame.pack();
        frame.setVisible(true);
        updateUI(new ChangeEvent(this), true);
    }

    public void closeWindow()
    {
        if (frame != null)
            frame.dispose();
    }

    private void updateUI(ChangeEvent e, boolean reset)
    {
        Object o = e.getSource();
        if (o instanceof JSlider)
        {
            JSlider slider = (JSlider)o;
            slider.setToolTipText("Value: " + slider.getValue());
        }
        if (uiUpdated != null)
            uiUpdated.accept(reset);
    }

    public int numPoints() { return numPointsSpinner.getNumber(); }
    public int graphWidth() { return graphWidthSlider.getValue(); }
    public int graphHeight() { return graphHeightSlider.getValue(); }
    public boolean drawLines() { return drawLinesChkBx.isSelected(); }
    public boolean drawPoints() { return drawPointsChkBx.isSelected(); }
    public boolean drawAverageCurve() { return drawAverageChkBx.isSelected(); }
    public boolean drawBezier() { return drawBezierChkBx.isSelected(); }
    public int bezierResolution() { return bezierResolutionSlider.getValue(); }
    public RNG numberGenerator() { return (RNG)rngComboBox.getSelectedItem(); }
}
