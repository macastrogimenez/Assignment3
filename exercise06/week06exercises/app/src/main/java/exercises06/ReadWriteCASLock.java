// For week 6
// raup@itu.dk * 2024-09-22

package exercises06;

import java.util.concurrent.atomic.AtomicReference;

class ReadWriteCASLock implements SimpleRWTryLockInterface {
    private final AtomicReference<Holders> holders = new AtomicReference<>(null);
    
    public boolean readerTryLock() {
        Thread current = Thread.currentThread();
        while (true) {
            Holders h = holders.get();
            
            if (h instanceof Writer) {
                return false;
            }
            
            ReaderList newReaders = new ReaderList(current, (ReaderList) h);
            if (holders.compareAndSet(h, newReaders)) {
                return true;
            }
        }
    }
    
    public void readerUnlock() {
        Thread current = Thread.currentThread();
        while (true) {
            Holders h = holders.get();
            
            if (!(h instanceof ReaderList)) {
                throw new RuntimeException("Current thread doesnt hold a read lock");
            }
            
            ReaderList readers = (ReaderList) h;
            if (!readers.contains(current)) {
                throw new RuntimeException("Current thread doesnt hold a read lock");
            }
            
            ReaderList newReaders = readers.remove(current);
            
            if (holders.compareAndSet(h, newReaders)) {
                return;
            }
        }
    }
    
    public boolean writerTryLock() {
        Thread current = Thread.currentThread();
        Holders h = holders.get();
        
        if (h != null) {
            return false;
        }
        
        Writer writer = new Writer(current);
        return holders.compareAndSet(null, writer);
    }
    
    public void writerUnlock() {
        Thread current = Thread.currentThread();
        Holders h = holders.get();
        
        if (!(h instanceof Writer)) {
            throw new RuntimeException("Current thread does not hold the write lock");
        }
        
        Writer writer = (Writer) h;
        if (writer.thread != current) {
            throw new RuntimeException("Current thread does not hold the write lock");
        }
        
        if (!holders.compareAndSet(h, null)) {
            throw new RuntimeException("Failed to release write lock");
        }
    }
    
    private static abstract class Holders { }
    
    private static class ReaderList extends Holders {
        private final Thread thread;
        private final ReaderList next;
        
        public ReaderList(Thread thread, ReaderList next) {
            this.thread = thread;
            this.next = next;
        }
        
        public boolean contains(Thread t) {
            ReaderList current = this;
            while (current != null) {
                if (current.thread == t) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }
        
        public ReaderList remove(Thread t) {
            if (this.thread == t) {
                return this.next;
            }
            
            ReaderList newNext = (next != null) ? next.remove(t) : null;
            return new ReaderList(this.thread, newNext);
        }
    }
    
    private static class Writer extends Holders {
        public final Thread thread;
        
        public Writer(Thread thread) {
            this.thread = thread;
        }
    }
}