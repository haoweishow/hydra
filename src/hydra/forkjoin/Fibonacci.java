package hydra.forkjoin;

import jsr166y.RecursiveTask;

/**
 * Adopted from Java 1.7. Each task recuresivly calculates
 * n-1 and n-2 by forking child calculator tasks for n-1 and n-2.
 * User: E Begoli
 * Date: 9/28/12
 */
class Fibonacci extends RecursiveTask<Integer> {
    final int n;

    Fibonacci(int n) {
        this.n = n;
    }

    public Integer compute() {
        if (n <= 1)
            return n;
        Fibonacci f1 = new Fibonacci(n - 1);
        f1.fork();
        Fibonacci f2 = new Fibonacci(n - 2);
        return f2.compute() + f1.join();
    }
}
