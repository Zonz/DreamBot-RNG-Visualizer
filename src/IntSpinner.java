import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

public class IntSpinner extends JSpinner
{
    public IntSpinner(int minimum, int maximum, int initialValue, boolean wrap)
    {
        super(new ClampedSpinnerModel(minimum, maximum, initialValue, wrap));
        ((DefaultEditor)getEditor()).getTextField().setEditable(true);
    }

    public int getNumber()
    {
        return (int)getModel().getValue();
    }

    private static class ClampedSpinnerModel implements SpinnerModel
    {
        private int current;
        private final int minimum;
        private final int maximum;
        private final boolean wrap;

        private List<ChangeListener> listeners;

        public ClampedSpinnerModel(int minimum, int maximum, int initialValue, boolean wrap)
        {
            current = initialValue;
            this.minimum = minimum;
            this.maximum = maximum;
            this.wrap = wrap;
            listeners = new ArrayList<>();
        }

        @Override
        public Object getValue()
        {
            return current;
        }

        @Override
        public void setValue(Object value)
        {
            if (!(value instanceof Integer))
                return;
            int n = (int)value;
            if (n < minimum)
                n = minimum;
            else if (n > maximum)
                n = maximum;
            current = n;
            for (ChangeListener listener : listeners)
                listener.stateChanged(new ChangeEvent(this));
        }

        @Override
        public Object getNextValue()
        {
            if (current == maximum)
                return wrap ? minimum : maximum;
            return current + 1;
        }

        @Override
        public Object getPreviousValue()
        {
            if (current == minimum)
                return wrap ? maximum : minimum;
            return current - 1;
        }

        @Override
        public void addChangeListener(ChangeListener l)
        {
            listeners.add(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l)
        {
            listeners.remove(l);
        }
    }
}
