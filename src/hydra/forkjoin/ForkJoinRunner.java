package hydra.forkjoin;

import hydra.Task;
import jsr166y.ForkJoinTask;

/** ForkJoin task implementation for compression of the byte content
 * User: E Begoli
 * Date: 9/28/12
 */
class ForkJoinRunner extends ForkJoinTask {
    final Task task;
    ForkJoinRunner(Task task) {
        this.task = task;
    }

    /** Executor method is required implementation for ForkJoinTask
     *
     * @return boolean
     */
    public boolean exec() {
        return task.execute();
    }

    @Override
    public Object getRawResult() { return null; }

    @Override
    protected void setRawResult(Object o) { }

}
