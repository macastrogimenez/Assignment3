package exercises05;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



public class ConcurrentSetTest {
    // Variable with set under test
    private ConcurrentIntegerSet set;
    private ConcurrentIntegerSet set1;
    private CyclicBarrier barrier;


    // Did not use @BeforeEach since it only runs for the first execution but not for the rest, therefore it does not work well for repeated tests

    @RepeatedTest(5000)  
    @DisplayName("Tests if the set would add the same number twice - this should not happen, but it is a possible interleaving") // TODO: make sure this description is correct
    public void AddBuggySetParallel() {
        set = new ConcurrentIntegerSetBuggy();
        int nrThreads = 20;
        // init barrier
        barrier = new CyclicBarrier(nrThreads + 1);

        // start threads
        for (int j = 0; j < nrThreads; j++) {
            new Thread(() -> {
                try {
                    barrier.await(1, TimeUnit.SECONDS); // Wait with timeout
                
                    set.add(42); // Example: add same number from all threads
                
                    barrier.await(1, TimeUnit.SECONDS); // Signal completion with timeout
                } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                    // Thread got stuck or timed out - this is expected with buggy implementation
                    System.err.println("Thread timed out or barrier broken: " + e.getMessage());
                }
            }).start();
        }
        
        // Main thread synchronization - AFTER all threads are started
        try {
            barrier.await(1, TimeUnit.SECONDS); // Main thread releases all worker threads
            barrier.await(1, TimeUnit.SECONDS); // Main thread waits for all workers to complete
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            // Timeout indicates threads got stuck - this is a failure we want to detect
            System.err.println("Barrier timeout in main thread: " + e.getMessage());
        }
        
		assertTrue(set.size() == 1, "set.size() == "+set.size()+", but we expected "+1);
    }

    @RepeatedTest(5000)
    // TODO: add display name
    public void RemoveBuggySetParallel() {
        set = new ConcurrentIntegerSetBuggy();
        set.add(2);
        int nrThreads = 20;
        // init barrier
        barrier = new CyclicBarrier(nrThreads + 1);

        // start threads
        for (int j = 0; j < nrThreads; j++) {
            new Thread(() -> {
                try {
                    barrier.await(1, TimeUnit.SECONDS); // Wait with timeout
                
                    set.remove(2); // remove same number from all threads
                
                    barrier.await(1, TimeUnit.SECONDS); // Signal completion with timeout
                } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                    // Thread got stuck or timed out - this is expected with buggy implementation
                    System.err.println("Thread timed out or barrier broken: " + e.getMessage());
                }
            }).start();
        }
        
        // Main thread synchronization - AFTER all threads are started
        try {
            barrier.await(1, TimeUnit.SECONDS); // Main thread releases all worker threads
            barrier.await(1, TimeUnit.SECONDS); // Main thread waits for all workers to complete
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            // Timeout indicates threads got stuck - this is a failure we want to detect
            System.err.println("Barrier timeout in main thread: " + e.getMessage());
        }
        
		assertTrue(set.size() == 0, "set.size() == "+set.size()+", but we expected "+0);
    }

    @RepeatedTest(5000)
    @DisplayName("Tests if the set would add the same number twice - this should not happen, as this set is thread safe") // TODO: make sure this description is correct
    public void AddFixedSetParallel() {
        // Reinitialize for each test iteration
        set = new ConcurrentIntegerSetSync();
        
        int nrThreads = 20;

		System.out.printf("Parallel tests with %d threads",
						nrThreads);

		// init barrier
		barrier = new CyclicBarrier(nrThreads + 1);
        

		// start threads
		for (int i = 0; i < nrThreads; i++) {
			new Thread(() -> {
                try {
                    barrier.await(); // Wait for all threads to be ready
                
                    set.add(3); // Example: add same number from all threads
                
                    barrier.await(); // Signal completion
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        
        try {
            barrier.await(); // Main thread releases all worker threads
            barrier.await(); // Main thread waits for all workers to complete
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        
		assertTrue(set.size() == 1, "set.size() == "+set.size()+", but we expected "+1);
        
    }

    @RepeatedTest(5000)
    // TODO: add display name
    public void RemoveFixedSetParallel() {
        set = new ConcurrentIntegerSetSync();
        set.add(2);
        int nrThreads = 20;
        // init barrier
        barrier = new CyclicBarrier(nrThreads + 1);

        // start threads
        for (int j = 0; j < nrThreads; j++) {
            new Thread(() -> {
                try {
                    barrier.await(1, TimeUnit.SECONDS); // Wait with timeout
                
                    set.remove(2); // remove same number from all threads
                
                    barrier.await(1, TimeUnit.SECONDS); // Signal completion with timeout
                } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                    // Thread got stuck or timed out - this is expected with buggy implementation
                    System.err.println("Thread timed out or barrier broken: " + e.getMessage());
                }
            }).start();
        }
        
        // Main thread synchronization - AFTER all threads are started
        try {
            barrier.await(1, TimeUnit.SECONDS); // Main thread releases all worker threads
            barrier.await(1, TimeUnit.SECONDS); // Main thread waits for all workers to complete
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            // Timeout indicates threads got stuck - this is a failure we want to detect
            System.err.println("Barrier timeout in main thread: " + e.getMessage());
        }
        
		assertTrue(set.size() == 0, "set.size() == "+set.size()+", but we expected "+0);
    }

    @RepeatedTest(5000)
    // TODO: add display name
    public void RemoveLibrarySetParallel() {
        set = new ConcurrentIntegerSetLibrary();
        set.add(2);
        int nrThreads = 20;
        // init barrier
        barrier = new CyclicBarrier(nrThreads + 1);

        // start threads
        for (int j = 0; j < nrThreads; j++) {
            new Thread(() -> {
                try {
                    barrier.await(1, TimeUnit.SECONDS); // Wait with timeout
                
                    set.remove(2); // remove same number from all threads
                
                    barrier.await(1, TimeUnit.SECONDS); // Signal completion with timeout
                } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                    // Thread got stuck or timed out - this is expected with buggy implementation
                    System.err.println("Thread timed out or barrier broken: " + e.getMessage());
                }
            }).start();
        }
        
        // Main thread synchronization - AFTER all threads are started
        try {
            barrier.await(1, TimeUnit.SECONDS); // Main thread releases all worker threads
            barrier.await(1, TimeUnit.SECONDS); // Main thread waits for all workers to complete
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            // Timeout indicates threads got stuck - this is a failure we want to detect
            System.err.println("Barrier timeout in main thread: " + e.getMessage());
        }
        
		assertTrue(set.size() == 0, "set.size() == "+set.size()+", but we expected "+0);
    }

    @RepeatedTest(5000)  
    @DisplayName("Tests if the set would add the same number twice - this should not happen, but it is a possible interleaving") // TODO: make sure this description is correct
    public void AddLibrarySetParallel() {
        set = new ConcurrentIntegerSetLibrary();
        int nrThreads = 20;
        // init barrier
        barrier = new CyclicBarrier(nrThreads + 1);

        // start threads
        for (int j = 0; j < nrThreads; j++) {
            new Thread(() -> {
                try {
                    barrier.await(1, TimeUnit.SECONDS); // Wait with timeout
                
                    set.add(3); // Example: add same number from all threads
                
                    barrier.await(1, TimeUnit.SECONDS); // Signal completion with timeout
                } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                    // Thread got stuck or timed out - this is expected with buggy implementation
                    System.err.println("Thread timed out or barrier broken: " + e.getMessage());
                }
            }).start();
        }
        
        // Main thread synchronization - AFTER all threads are started
        try {
            barrier.await(1, TimeUnit.SECONDS); // Main thread releases all worker threads
            barrier.await(1, TimeUnit.SECONDS); // Main thread waits for all workers to complete
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            // Timeout indicates threads got stuck - this is a failure we want to detect
            System.err.println("Barrier timeout in main thread: " + e.getMessage());
        }
        
		assertTrue(set.size() == 1, "set.size() == "+set.size()+", but we expected "+1);
    }
}
