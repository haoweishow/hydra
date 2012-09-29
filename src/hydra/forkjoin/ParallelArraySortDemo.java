package hydra.forkjoin;

import extra166y.ParallelArray;
import hydra.Timer;
import jsr166y.ForkJoinPool;

import java.util.Random;

import static extra166y.ParallelArray.createUsingHandoff;

/**
 * This class was adopted from Doug Lea's concurrent test framework
 * and used for paramtrized benchmarking of Fork Join framework based parallel programming
 * Released to public domain under Creative Commons 1.0
 *
 * @author E Begoli
 */
class ParallelArraySort {

    static int array_size = 1 << 20;
    static int repetitions = 50;
    static final Random random = new Random();
    Long[] parray = new Long[array_size];
    ForkJoinPool fjpool = new ForkJoinPool();
    ParallelArray<Long> pa = createUsingHandoff(parray, fjpool);

    public void prepareArray() {
        randomFill(parray);
        pa = createUsingHandoff(parray, fjpool);
    }

    public void resetArray() {
        shuffle(parray);
    }

    public boolean checkSorted() {
        int n = parray.length;
        for (int i = 0; i < n - 1; i++) {
            if (parray[i].compareTo(parray[i + 1]) > 0) {
                System.out.println("Unsorted at " + i + ": " + parray[i] + " / " + parray[i + 1]);
                return false;
            }
        }
        return true;
    }

    /**
     * TODO: refactor this to do only sort on already prepared data structure
     */
    public void sort() {
        pa.sort();
        //            System.gc();
    }


    public static void main(String[] args) throws Exception {
        Timer timer = new Timer("parallel array sort", String.format("array size %d sort repetitions %d", array_size, repetitions));
        Long[] parray = new Long[array_size];
        randomFill(parray);
        ForkJoinPool fjpool = new ForkJoinPool();
        ParallelArray<Long> pa = createUsingHandoff(parray, fjpool);

        for (int i = 0; i < repetitions; ++i) {
            long last = System.nanoTime();
            timer.start();
            pa.sort();
            timer.stop();
            timer.display(System.out);
            checkSorted(parray);
            shuffle(parray);
            //            System.gc();
        }
        fjpool.shutdown();
    }

    static void checkSorted(Long[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            if (a[i].compareTo(a[i + 1]) > 0) {
                throw new Error("Unsorted at " + i + ": " + a[i] + " / " + a[i + 1]);
            }
        }
    }

    static void randomFill(Long[] a) {
        for (int i = 0; i < a.length; ++i)
            a[i] = new Long(random.nextLong());
    }

    static void shuffle(Long[] a) {
        int n = a.length;
        for (int i = n; i > 1; --i) {
            int r = random.nextInt(i);
            Long t = a[i - 1];
            a[i - 1] = a[r];
            a[r] = t;
        }
    }


}//end class