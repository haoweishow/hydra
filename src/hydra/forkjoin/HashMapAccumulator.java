package hydra.forkjoin;

import jsr166y.ForkJoinTask;

/**
 * User: E Begoli
 * Date: 9/28/12
 */
public class HashMapAccumulator extends ForkJoinTask<Integer> {

    @Override
    public Integer getRawResult() {
        return null;
    }

    @Override
    protected void setRawResult(Integer integer) {
    }

    @Override
    protected boolean exec() {
        return false;
    }
}

