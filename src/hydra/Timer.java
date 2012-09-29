package hydra;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

/**
 * Simple stopwatch class for measuring execution time
 *
 * @author E Begoli
 */
public class Timer {

    public Timer(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public Timer() {
    }

    String id = "";
    String description = "";
    long start = 0;
    long stop = 0;
    long elapsedTime = 0;

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void start() {
        start = System.nanoTime();
    }

    public void start(String id, String description) {
        this.id = (id != null ? id : "");
        this.description = (description != null ? description : "");
        start = System.nanoTime();
    }

    public void stop() {
        stop = System.nanoTime();
        elapsedTime = stop - start;
    }

    public void reset() {
        stop = start = elapsedTime = 0;
    }

    /**
     * Prints the id, description and
     *
     * @param out
     */
    public void display(PrintStream out) {
        out.printf("%s | %s | %d | %d | %d %n", id, description, TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS),
                TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS), TimeUnit.MINUTES.convert(elapsedTime, TimeUnit.NANOSECONDS));
    }

    public String toString() {
        return "" + elapsedTime;
    }

    /**
     * Serves as a test and a demonstrator for the Timer class. It shows the accuracy of one minute time keeping task.
     *
     * @param args
     */
    public static void main(String args[]) {

        Timer testTimer = new Timer("test", "test");
        testTimer.start();
        try {
            Thread.sleep(65000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testTimer.stop();
        testTimer.display(System.out);
    }
}
