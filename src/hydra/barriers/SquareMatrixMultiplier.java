package hydra.barriers;

import java.util.concurrent.CountDownLatch;

/**
 * @author E Begoli
 *         Date: 9/28/12
 *         Released to the public domain under Creative Commons 1.0
 *         http://creativecommons.org/publicdomain/zero/1.0/
 */

class SquareMatrixMultiplier {
    final int N;
    final double[][] data;
    volatile double[][] result;
    final CountDownLatch barrier;

    public SquareMatrixMultiplier(int size) {
        N = size;
        data = new double[N][N];
        result = new double[N][N];
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                data[i][j] = Math.round(Math.random() * 100);
            }
        }
        barrier = new CountDownLatch(N * N);
    }

    /**
     *
     */
    class Multiplier implements Runnable {
        int myRow;
        int myColumn;

        Multiplier(int row, int column) {
            myRow = row;
            myColumn = column;
        }

        public void run() {
            multiply();
            barrier.countDown();
        }

        /**
         */
        private void multiply() {
            for (int i = 0; i < N; i++) {
                result[myRow][myColumn] += data[myRow][i] * data[i][myColumn];
            }
        }
    }

    public void solve() {

        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < N; ++j) {
                //TODO: review this
                new Thread(new Multiplier(i, j)).start();
            }
        }
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void print(double array[][]) {
        StringBuffer output = new StringBuffer(N);
        for (int i = 0; i < N; ++i) {
            output.append("|");
            for (int j = 0; j < N; ++j) {
                output.append(" " + array[i][j] + " ");
            }
            output.append("|\n");
        }
        System.out.print(output.toString());
    }

    public static void main(String args[]) {
        int N = 10;
        SquareMatrixMultiplier multiplier = new SquareMatrixMultiplier(N);
        multiplier.solve();
        System.out.println("Data");
        multiplier.print(multiplier.data);
        System.out.println("Result");
        multiplier.print(multiplier.result);
    }
}
