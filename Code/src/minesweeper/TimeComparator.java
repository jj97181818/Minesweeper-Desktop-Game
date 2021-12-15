package minesweeper;

import java.util.Comparator;

public class TimeComparator implements Comparator<Time>
{
    @Override
    public int compare(Time a, Time b) {
        if (a.getTimeValue() > b.getTimeValue())
            return 1;
        else if (a.getTimeValue() < b.getTimeValue())
            return -1;
        else
            return 0;
    }                        
}
