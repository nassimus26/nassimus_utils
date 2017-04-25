package org.thread;

public abstract class CallableAbs<V> implements Runnable {
    private ExecutorWithFlowControl<V> executorWithFlowControl3 = null;

    public void setExecutorWithFlowControl3(ExecutorWithFlowControl<V> executorWithFlowControl3) {
        this.executorWithFlowControl3 = executorWithFlowControl3;
    }

    @Override
    public void run() {
        try {
            executorWithFlowControl3.aggregator4(call());
        } catch (Throwable e) {
            executorWithFlowControl3.setThrowable(e);
        }
    }

    public abstract V call() throws Throwable;

}