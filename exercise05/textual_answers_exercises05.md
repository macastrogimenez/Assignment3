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

