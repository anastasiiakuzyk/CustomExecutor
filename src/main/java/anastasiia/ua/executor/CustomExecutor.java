package anastasiia.ua.executor;

import anastasiia.ua.SomeFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomExecutor<TFunc, TArg, TResult> {

    private final LinkedBlockingQueue<WorkItem<TFunc, TArg, TResult>> queue = new LinkedBlockingQueue<>();

    public CustomExecutor(int nThreads) {
        List<WorkerThread> workerThreadList = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            workerThreadList.add(new WorkerThread());
            workerThreadList.get(i).start();
        }
    }

    private volatile boolean isExecuting = true;

    public FutureResult<TResult> execute(SomeFunction<TFunc> func, TArg arg) {
        WorkItem<TFunc, TArg, TResult> item = new WorkItem<>(func, arg);
        if (isExecuting) {
            synchronized (queue) {
                queue.add(item);
                queue.notify();
            }
        }
        return item.getFutureResult();
    }

    public ArrayList<FutureResult<TResult>> map(SomeFunction<TFunc> function, List<TArg> argList) {
        ArrayList<FutureResult<TResult>> futureResults = new ArrayList<>();
        for (TArg arg : argList) {
            futureResults.add(execute(function, arg));
        }
        return futureResults;
    }

    public void shutdown() {
        if (!queue.isEmpty()) {
            synchronized (queue) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    System.err.println("Error while queue is waiting!\n" + e.getMessage());
                }
            }
        }
        isExecuting = false;
    }

    public class WorkerThread extends Thread {
        public void run() {
            WorkItem<TFunc, TArg, TResult> item;
            while (isExecuting) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.notify();
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.err.println("Error while queue is waiting!\n" + e.getMessage());
                        }
                    }
                    item = queue.poll();
                }
                try {
                    Thread.sleep(2000);
                    item.getFutureResult().setResult((TResult) item.getFunc().func((TFunc) item.getArg()));
                } catch (RuntimeException | InterruptedException e) {
                    System.err.println("Thread pool interrupted!\n" + e.getMessage());
                }
            }
        }
    }

}

