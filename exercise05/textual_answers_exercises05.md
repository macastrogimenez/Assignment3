# Exercise battery 5

## Exercise 5.1

### Exercise 5.1.3

Below the original ConcurrentIntegerSetBuggy: 
```java
class ConcurrentIntegerSetBuggy implements ConcurrentIntegerSet {
    final private Set<Integer> set;
    private ReentrantLock lock;

    public ConcurrentIntegerSetBuggy() {
        this.set = new HashSet<Integer>();
        this.lock = new ReentrantLock();
    }

    public boolean add(Integer element) {
        return set.add(element); // t(1)
    }

    public boolean remove(Integer element) {
        return set.remove(element); // t(2)
    }

    public int size(){
        return set.size(); // t(3)
    }
}
```

The fixed original ConcurrentIntegerSetBuggy: 

```java
class ConcurrentIntegerSetSync implements ConcurrentIntegerSet {
    final private Set<Integer> set;
    private ReentrantLock lock; // Declare the lock

    public ConcurrentIntegerSetSync() {
        this.set = new HashSet<Integer>();
        this.lock = new ReentrantLock();  // Initialize the lock!
    }

    public boolean add(Integer element) {
        lock.lock();
        boolean addedElement = set.add(element); //t(1) now locked
        lock.unlock();
        return addedElement;
        
    }

    public boolean remove(Integer element) {
        lock.lock();
        boolean removedElement = set.remove(element); //t(2) now locked
        lock.unlock();
        return removedElement;
    }

    public int size() {
        lock.lock();
        int currentSize = set.size(); //t(3) now locked
        lock.unlock();
        return currentSize;
    }
}
```

Thanks to the changes to the `add()`, `remove()`, and `size()` methods which ensure the class is thread-safe, we can trust that the set will behave correctly under concurrent access.

Let us remember that the methods `add()`, `remove()`, and `size()` from the class Set / HashSet are not atomic operations, hence it was possible for a thread to be in the middle of either:

- Adding an element to the set, and not having finished, while a second thread would finish adding the exact same element before the addition was visible, leaving the set with the same element twice.
- Removing an element from the set, and not having finished, while a second thread would finish removing the exact same element before the removal was visible, leaving the set with the less than zero elements in some cases - which would originally seem impossible.

Also, while it might seem that `size()` is just a read operation and doesn't modify the set, it **absolutely must be synchronized**. Without synchronization, the `size()` method may read **stale data** from CPU cache instead of the actual current state. Example: Thread T1 reads the `size` field before T2 finishes incrementing it, seeing an inconsistent snapshot where the table has been modified but the size hasn't been updated yet.

### Exercise 5.1.4

The test were performed with a Macbook Pro M1 Pro 32Gb RAM, 1Tb SSD (10 CPU). Every test was ran 5000 times, with the main thread plus 20 other threads trying to either add or remove an element from the set.
No failures were found which is a good sign that the `ConcurrentIntegerSetLibrary` is thread-safe, however, these do not guarantee the that this is the case, this could only be demonstrated through formal proof.

### Exercise 5.1.5

When a test **fails**, it provides definitive evidence that the implementation is **not thread-safe**:

- A failed assertion (`set.size() == 2` when we expected 1) demonstrates that a race condition occurred
- The failure shows a concrete execution trace where threads interleaved incorrectly
- This is a **proof by counterexample** - we found at least one scenario where thread safety was violated

### Exercise 5.1.6

Test passes do not prove correctness. Even with 5000 repetitions and 20 threads, we only explore a tiny fraction of all possible thread interleavings. The number of possible execution orders grows with thread count.
Moreover, a bug might only manifest under specific timing conditions (CPU load, scheduler decisions, hardware) that didn't occur during testing. Therefore, testing can find bugs but cannot prove their absence in concurrent programs.

## Exercise 5.2
### Exercise 5.2.1
The property doesn't hold because if we release more times than we acquired, there could be more threads in the critical section than the capacity would suggest:
```java
        SemaphoreImp semaphore = new SemaphoreImp(1);
        
        // First acquire to reach capacity
        semaphore.acquire();  // Thread 1 enters
        
        semaphore.release();  //Thread 1 leaves
        semaphore.release();  // State becomes -1
        
        // Now multiple threads can enter even though capacity is 1
        semaphore.acquire();  // Thread 2 enters
        semaphore.acquire();  // Thread 3 enters
        //semaphore.acquire();  // Thread 4 enters
```
There is no condition that would check if the state is greater than 0, so it could happen that semaphore is released more times than it's acquired. And in that case the state would be negative and capacity would still be the same, so in that case c+(-state) threads could enter. So if T1, T2, T3 are threads, A is acquire and R is release, then the interleaving would be: T1(A)->T1(R)->T1(R)->T2(A)->T3(A).

### Exercise 5.2.2
Second test in test file SemaphoreImpTest.java - it fails, because there is more than one thread inside the critical section.