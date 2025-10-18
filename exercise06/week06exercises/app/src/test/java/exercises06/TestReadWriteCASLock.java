package exercises06;

import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

public class TestReadWriteCASLock {  
    private ReadWriteCASLock lock;

    @BeforeEach
    void setUp() {
        lock = new ReadWriteCASLock();
    }

    // Sequential Correctness Tests 

    @Test
    void testCannotTakeReadLockWhileHoldingWriteLock() {
        assertTrue(lock.writerTryLock(), "Writer should acquire lock");

        assertFalse(lock.readerTryLock(),
                "Reader should not acquire lock while writer holds it");

        lock.writerUnlock();
    }

    @Test
    void testCannotTakeWriteLockWhileHoldingReadLock() {
        assertTrue(lock.readerTryLock(), "Reader should acquire lock");

        assertFalse(lock.writerTryLock(),
                "Writer should not acquire lock while reader holds it");

        lock.readerUnlock();
    }

    @Test
    void testCannotUnlockLocksNotHeld() {
        assertThrows(RuntimeException.class, lock::writerUnlock,
                "Unlocking unheld write lock should fail");
        assertThrows(RuntimeException.class, lock::readerUnlock,
                "Unlocking unheld read lock should fail");
    }

    @Test
    void testBasicLockAcquireAndRelease() {
        assertTrue(lock.writerTryLock(), "Writer should acquire lock");
        lock.writerUnlock();

        assertTrue(lock.readerTryLock(), "Reader should acquire lock");
        lock.readerUnlock();
    }

    @Test
    void testMultipleReadersAllowed() {
        assertTrue(lock.readerTryLock(), "First reader should acquire");
        assertTrue(lock.readerTryLock(), "Second reader should also acquire");

        lock.readerUnlock();
        lock.readerUnlock();
    }

    // Parallel Correctness Test 

    @RepeatedTest(10)
    void testTwoWritersCannotHoldLockSimultaneously() throws InterruptedException {
        final int numThreads = 2;
        final int iterations = 500;

        AtomicInteger activeWriters = new AtomicInteger(0);
        AtomicInteger maxConcurrent = new AtomicInteger(0);

        CyclicBarrier startBarrier = new CyclicBarrier(numThreads);
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    startBarrier.await();
                    for (int j = 0; j < iterations; j++) {
                        while (!lock.writerTryLock()) {
                            Thread.yield();
                        }

                        int current = activeWriters.incrementAndGet();
                        maxConcurrent.updateAndGet(prev -> Math.max(prev, current));

                        activeWriters.decrementAndGet();
                        lock.writerUnlock();
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    fail("Thread interrupted: " + e.getMessage());
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        assertEquals(1, maxConcurrent.get(),
                "At most one writer should hold the lock at any time");
    }
}