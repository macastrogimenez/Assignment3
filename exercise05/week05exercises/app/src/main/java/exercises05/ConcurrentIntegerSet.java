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

class ConcurrentIntegerSetBuggy implements ConcurrentIntegerSet {
    final private Set<Integer> set;
    private ReentrantLock lock;

    public ConcurrentIntegerSetBuggy() {
        try {
            lock.lock();
            this.set = new HashSet<Integer>();    
        } catch (Exception e) {
            // TODO: handle exception
        }
        finally{
            lock.unlock();
        }
    }

    public boolean add(Integer element) {
        lock.lock();
        return set.add(element);
        
    }

    public boolean remove(Integer element) {
        return set.remove(element);
    }

    public int size() {
        return set.size();
    }
}

// TODO: Fix this class to pass your tests
class ConcurrentIntegerSetSync implements ConcurrentIntegerSet {
    final private Set<Integer> set;

    public ConcurrentIntegerSetSync() {
        this.set = new HashSet<Integer>();
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
