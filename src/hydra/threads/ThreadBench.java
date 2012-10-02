package hydra.threads;

import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

public class ThreadBench {
    static Random rand = new Random(System.currentTimeMillis()); // Seed using current time. 
    ConcurrentSkipListMap<String, String> map; // Our values. 
    String[] keys;
    int numKeys;
    int numClients;
    double readRatio;
    TableClient[] clients;
    Thread[] threads;

    public ThreadBench(int numKeys, int numClients, double readRatio) {
        map = new ConcurrentSkipListMap<String, String>();
        keys = new String[numKeys];
        clients = new TableClient[numClients];
        threads = new Thread[numClients];

        this.numKeys = numKeys;
        this.numClients = numClients;
        this.readRatio = readRatio;

        genKeys(numKeys); // Generate the keys.
        genClients(numClients);
    }

    /**
     * Place something in the map.
     */
    public void put(String key, String data) {
        map.put(key, data);
    }

    /**
     * Read something from the map.
     */
    public String get(String key) {
        return map.get(key);
    }

    /**
     * Instantiate the clients.
     */
    private void genClients(int numClients) {
        int readClients = (int) Math.floor((double) numClients * readRatio);
        int writeClients = numClients - readClients;

        for (int i = 0; i < readClients; ++i) {
            TableClient c = new TableClient(i, this, true);
            Thread t = new Thread(c);
            clients[i] = c;
            threads[i] = t;
        }

        for (int i = 0; i < writeClients; ++i) {
            TableClient c = new TableClient(i + readClients, this, false);
            Thread t = new Thread(c);
            clients[i + readClients] = c;
            threads[i + readClients] = t;
        }
    }

    /**
     * Start the benchmarking.
     */
    public void start() {
        for (int i = 0; i < numClients; ++i) {
            threads[i].start();
        }
    }

    /**
     * Print out statistics.
     */
    public void statistics() {
        double avg = 0.00;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long minStart = Long.MAX_VALUE;
        long maxEnd = Long.MIN_VALUE;

        for (int i = 0; i < numClients; ++i) {
            try {
                threads[i].join();
                // System.out.printf("%d %d\n",
                // 		  clients[i].getID(),
                // 		  clients[i].getDuration());

                avg += clients[i].getDuration();

                if (clients[i].getDuration() < min) {
                    min = clients[i].getDuration();
                }

                if (clients[i].getDuration() > max) {
                    max = clients[i].getDuration();
                }

                if (clients[i].getStart() < minStart) {
                    minStart = clients[i].getStart();
                }

                if (clients[i].getEnd() > maxEnd) {
                    maxEnd = clients[i].getEnd();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        avg /= numClients;

        System.out.printf("%d %f %d %d %d\n", numClients, avg, min, max, maxEnd - minStart);
    }

    /**
     * Generate some random keys.
     */
    private void genKeys(int numKeys) {
        for (int i = 0; i < numKeys; ++i) {
            keys[i] = randomString(16);
        }
    }

    /**
     * Generate a random string of the specified length.
     */
    protected String randomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int j = 0; j < length; ++j) {
            char c = (char) ('a' + rand.nextInt(26)); // Select a random char.
            if (c > 'z') c = 'a';
            builder.append(c);
        }

        return builder.toString();
    }

    // Return a random key. 
    private String getKey() {
        return keys[rand.nextInt(numKeys)];
    }

    class TableClient implements Runnable {
        ThreadBench parent;
        boolean readMode;
        long start;
        long end;
        long time;
        int id;

        public TableClient(int id, ThreadBench p, boolean readMode) {
            parent = p;
            this.id = id;
            this.readMode = readMode;
            time = 0;
        }

        public void run() {
            start = System.currentTimeMillis();
            for (int i = 0; i < 10000; ++i) {
                // Choose a random key.
                String key = parent.getKey();

                // Read or write to that key.
                if (readMode) {
                    parent.get(key);
                } else {
                    parent.put(key, parent.randomString(32));
                }
            }
            end = System.currentTimeMillis();
            time = end - start;
        }

        /**
         * Get client ID.
         */
        public int getID() {
            return id;
        }

        /**
         * Get the duration of this client run.
         */
        public long getDuration() {
            return time;
        }

        /**
         * Get the start & end times.
         */
        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }
    }

    public static void main(String[] args) {
        ThreadBench bench;

        System.out.printf("clients | avg | min | max | duration\n");
        for (int i = 1; i < 100; ++i) {
            bench = new ThreadBench(1000, i, 0.00);
            bench.start(); // Start benchmark.
            bench.statistics(); // Print out data.
        }
    }
}