package anastasiia.ua.executor;

import anastasiia.ua.SomeFunction;
import lombok.Getter;

@Getter
public class WorkItem<TFunc, TArg, TResult> {

    private SomeFunction<TFunc> func;
    private TArg arg;
    private FutureResult<TResult> futureResult = new FutureResult<>();

    public WorkItem(SomeFunction<TFunc> func, TArg arg) {
        this.func = func;
        this.arg = arg;
    }

}