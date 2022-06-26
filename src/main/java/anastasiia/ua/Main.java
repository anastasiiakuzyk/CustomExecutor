package anastasiia.ua;

import anastasiia.ua.executor.CustomExecutor;
import anastasiia.ua.executor.FutureResult;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] ar) {
        List<Double> args = List.of(1., 2., 3., 4., 5., 6., 7., 8., 9., 10.);
        SomeFunction<Double> someFunction = x -> Math.pow(x, 2);
        CustomExecutor<Double, Double, Double> executor = new CustomExecutor<>(5);
        ArrayList<FutureResult<Double>> results = executor.map(someFunction, args);
        executor.shutdown();
        results.forEach(result -> System.out.println(result.getTime() + " - " + result.result()));
    }

}