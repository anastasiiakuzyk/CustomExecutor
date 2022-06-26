package anastasiia.ua.executor;

import lombok.Getter;

import java.time.LocalTime;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class FutureResult<TResult> {

    private TResult result;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private LocalTime time;

    public void setResult(TResult result) {
        if (result != null) {
            lock.lock();
            this.result = result;
            time = LocalTime.now();
            condition.signal();
            lock.unlock();
        }
    }

    public TResult result() {
        if (result == null) {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                System.err.println("Error while queue is waiting!\n" + e.getMessage());
            }
            lock.unlock();
        }
        return result;
    }

}
