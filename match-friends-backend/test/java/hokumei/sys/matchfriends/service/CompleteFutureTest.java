package hokumei.sys.matchfriends.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.Thread.sleep;

@SpringBootTest
public class CompleteFutureTest {

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        // 固定サイズのスレッドプール
        ExecutorService service = Executors.newFixedThreadPool(10);
        //スレッドプールを指定しない場合、CompletableFutureデフォルトスレッドプールを使用
        CompletableFuture<String> task = CompletableFuture.supplyAsync(
                ()-> {
                    System.out.println(Thread.currentThread().getName());
                    return "hello";
                },service);
        System.out.println(task.get());
    }

    @Test
    public void test2() throws ExecutionException, InterruptedException {
        // 固定サイズのスレッドプール
        ExecutorService service = Executors.newFixedThreadPool(10);
        Supplier<String> taskA = () -> {
            System.out.println(1 + " Supplier " + Thread.currentThread().getName());
            return "hello";};

        Function<String,String> taskB = s -> {
            System.out.println(2 + " Function " + Thread.currentThread().getName());
            return "hello";};
        //スレッドプールを指定しない場合、CompletableFutureデフォルトスレッドプールを使用
        CompletableFuture<String> task = CompletableFuture.supplyAsync(taskA,service);
        sleep(1);
        task.thenApply(taskB);
        System.out.println(task.get());
    }

    @Test
    public void test3() throws ExecutionException, InterruptedException {
        // 固定サイズのスレッドプール
        ExecutorService service = Executors.newFixedThreadPool(10);
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(
                ()-> {
                    System.out.println(Thread.currentThread().getName());
                    return "hello A";
                },service);
        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(
                ()-> {
                    System.out.println(Thread.currentThread().getName());
                    return "hello B";
                },service);

        //等待任务A和任务B都执行完 之后将结果相加返回
        CompletableFuture<String> result = futureA.thenCombine(futureB, (s1, s2) -> s1 + s2);
        System.out.println(result.get());
    }


    @Test
    public void test4() throws ExecutionException, InterruptedException {
        // 固定サイズのスレッドプール
        ExecutorService service = Executors.newFixedThreadPool(10);
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(
                ()-> {
                    System.out.println(Thread.currentThread().getName());
                    return "hello A";
                },service);
        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(
                ()-> {
                    System.out.println("bbb" + Thread.currentThread().getName());
                    return "hello B";
                },service);

        //等待任务A和任务B任意一个执行完 执行下一个任务
        futureA.runAfterEither(futureB, ()->{
            System.out.println("!!!");
        });
    }

    @Test
    public void test5() throws ExecutionException, InterruptedException {
        // 固定サイズのスレッドプール
        ExecutorService service = Executors.newFixedThreadPool(10);
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(
                ()-> {
                    System.out.println(Thread.currentThread().getName());
                    return "hello A";
                },service);
        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(
                ()-> {
                    System.out.println("bbb" + Thread.currentThread().getName());
                    return "hello B";
                },service);

        //等待所有任务执行完 才往下走
        CompletableFuture.allOf(futureA,futureB).join();
    }

    @Test
    public void test6() throws ExecutionException, InterruptedException, TimeoutException {
        // 固定サイズのスレッドプール
        ExecutorService service = Executors.newFixedThreadPool(10);
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(
                ()-> {
                    System.out.println(Thread.currentThread().getName());
                    return "hello A";
                },service);
        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(
                ()-> {
                    System.out.println("bbb" + Thread.currentThread().getName());
                    return "hello B";
                },service);
        futureA.get(5,TimeUnit.SECONDS);
        futureB.whenComplete((r,s)->{
            System.out.println(r);
            if(s!=null){
                System.out.println(s);
            }
        });
    }
}
