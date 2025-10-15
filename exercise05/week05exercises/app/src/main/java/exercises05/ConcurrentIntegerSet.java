// NOTE: In this file, you should only modify the class ConcurrentIntegerSetSync
package exercises05;

import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public interface ConcurrentIntegerSet {
    public boolean add(Integer element);
    public boolean remove(Integer element);
    public int size();
}
class ConcurrentIntegerSetSync implements ConcurrentIntegerSet {
    final private Set<Integer> set;
    private ReentrantLock lock;

    public ConcurrentIntegerSetSync() {
        this.set = new HashSet<Integer>();
        this.lock = new ReentrantLock();  // Initialize the lock!
    }

    public boolean add(Integer element) {
        lock.lock();
        boolean addedElement = set.add(element);
        lock.unlock();
        return addedElement;
        
    }

    public boolean remove(Integer element) {
        lock.lock();
        boolean removedElement = set.remove(element);
        lock.unlock();
        return removedElement;
    }

    public int size() {
        lock.lock();
        int currentSize = set.size();
        lock.unlock();
        return currentSize;
    }
}


class ConcurrentIntegerSetBuggy implements ConcurrentIntegerSet {
    final private Set<Integer> set;
    private ReentrantLock lock;

    public ConcurrentIntegerSetBuggy() {
        this.set = new HashSet<Integer>();
        this.lock = new ReentrantLock();
    }

    public boolean add(Integer element) {
        return set.add(element);
    }

    public boolean remove(Integer element) {
        return set.remove(element);
    }

    public int size(){
        int size;
        try{
            lock.lock();
            size = set.size();
        }
        finally{
            lock.unlock();
        }
        return size;
    }

}

class ConcurrentIntegerSetLibrary implements ConcurrentIntegerSet {
    final private Set<Integer> set;

    public ConcurrentIntegerSetLibrary() {
        this.set = new ConcurrentSkipListSet<Integer>();
    }

    public boolean add(Integer element) {
        return set.add(element);
    }

    public boolean remove(Integer element) {
        return set.remove(element);
    }

    public int size() {
        return set.size();
    }
}
