import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Jack
 */
public class Timer {
    private long start;
    private long end;

    private final DateFormat dateFormat;
    private Date startDate;
    private Date endDate;

    public Timer() {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    public final void startTimer() {
        start = System.nanoTime();
        startDate = new Date();
    }

    public final void stopTimer() {
        end = System.nanoTime();
        endDate = new Date();
    }

    public final String getStartDate() {
        return dateFormat.format(startDate);
    }

    public final String getEndDate() {
        return dateFormat.format(endDate);
    }

    public final long getElapsedTime() {
        return (long) ((end - start) / 1E9);
    }

}
