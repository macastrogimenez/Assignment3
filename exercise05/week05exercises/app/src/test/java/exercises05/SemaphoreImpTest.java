package exercises05;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SemaphoreImpTest {

    @Test
    public void testMultipleThreadsExceedCapacity() throws InterruptedException {
        final SemaphoreImp semaphore = new SemaphoreImp(1); // capacity = 1
        final AtomicInteger inside = new AtomicInteger(0);  // tracks threads in critical section
        final AtomicInteger maxInside = new AtomicInteger(0);

        Runnable critical = () -> {
            String name = Thread.currentThread().getName();
            try {
                System.out.println(name + " attempting to acquire...");
                //Thread.sleep((long) (Math.random() * 10));
                semaphore.acquire();
                int currentlyInside = inside.incrementAndGet();
                maxInside.updateAndGet(prev -> Math.max(prev, currentlyInside));
                System.out.println(name + " acquired semaphore! inside=" + currentlyInside);

                // Simulate some work inside critical section
                Thread.sleep(300);

                inside.decrementAndGet();
                semaphore.release();
                System.out.println(name + " released semaphore.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(critical, "T1");
        Thread t2 = new Thread(critical, "T2");
        Thread t3 = new Thread(critical, "T3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Max threads inside critical section simultaneously: " + maxInside.get());

        assertTrue(
            maxInside.get() <= 1, "More than 1 thread entered critical section simultaneously! maxInside=" + maxInside.get()
            
        );
    }

    @Test
    public void testCapacityViolationAfterOverRelease() throws InterruptedException {
        final SemaphoreImp semaphore = new SemaphoreImp(1); // capacity = 1
        final AtomicInteger inside = new AtomicInteger(0);
        final AtomicInteger maxInside = new AtomicInteger(0);

        // --- Intentionally violate internal invariant ---
        // Over-release twice without prior acquire
        semaphore.release();
        semaphore.release();
        System.out.println("Over-released semaphore twice before starting threads.");

        Runnable critical = () -> {
            String name = Thread.currentThread().getName();
            try {
                System.out.println(name + " attempting to acquire...");
                semaphore.acquire();
                int current = inside.incrementAndGet();
                maxInside.updateAndGet(prev -> Math.max(prev, current));
                System.out.println(name + " acquired semaphore! inside=" + current);

                Thread.sleep(200); // simulate work

                inside.decrementAndGet();
                semaphore.release();
                System.out.println(name + " released semaphore.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(critical, "T1");
        Thread t2 = new Thread(critical, "T2");
        Thread t3 = new Thread(critical, "T3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Max threads inside critical section simultaneously: " + maxInside.get());

        assertTrue(
            maxInside.get() <= 1,"More than 1 thread entered critical section simultaneously! maxInside=" + maxInside.get()
            
        );
    }
}