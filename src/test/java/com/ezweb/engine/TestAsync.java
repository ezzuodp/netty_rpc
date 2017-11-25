package com.ezweb.engine;

import com.google.common.util.concurrent.*;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.*;

public class TestAsync {
    private static ExecutorService th = Executors.newFixedThreadPool(3, new DefaultThreadFactory("async_w_"));
    private static ListeningExecutorService lstth = MoreExecutors.listeningDecorator(th);

    public static class A {
        public String get() throws Exception {
            System.out.println("_a = " + Thread.currentThread().getName());

            // 花时长的操作
            TimeUnit.MILLISECONDS.sleep(10000L);
            StringBuilder msg = new StringBuilder(128);
            msg.append("A{").append(System.currentTimeMillis()).append('-');
            msg.append(System.currentTimeMillis()).append("}");
            return msg.toString();
        }
    }

    public static class B {
        private A _a = new A();

        public ListenableFuture<String> get() {
            return lstth.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    System.out.println("_b = " + Thread.currentThread().getName());
                    return _a.get() + "|B{" + System.currentTimeMillis() + "}";
                }
            });
        }
    }

    public static class C {
        private B _b = new B();

        public ListenableFuture<String> get() {
            ListenableFuture<String> c = _b.get();
            Futures.addCallback(c, new FutureCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("onSuccess(result := '" + result + "')");
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace(System.out);
                }
            });
            return c;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        C cc = new C();
        ListenableFuture<String> f = cc.get();
        try {
            String fv = f.get(1, TimeUnit.MILLISECONDS);
            System.out.println("fv = " + fv);
        } catch (TimeoutException e) {
            f.cancel(true);
        }
        lstth.shutdown();
    }
}
