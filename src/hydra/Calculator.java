package hydra;

import jsr166y.ForkJoinTask;

/** Calculator class for
 * User: E Begoli
 * Date: 9/28/12
 */
public class Calculator extends ForkJoinTask<Double>{
    @Override
    public Double getRawResult() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setRawResult(Double aDouble) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected boolean exec() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
