package exercises06;

import java.util.concurrent.atomic.AtomicInteger;

public class CasHistogram implements Histogram{
    private int span = 30;
    private final AtomicInteger[] atomicArray;

    public CasHistogram() {
        atomicArray = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            atomicArray[i] = new AtomicInteger(0);
        }
    }

    public void increment(int bin) {
        int oldValue, newValue;
        do {
            oldValue = atomicArray[bin].get();
            newValue = oldValue + 1;
        } while (!atomicArray[bin].compareAndSet(oldValue, newValue));
    }

    public int getCount(int bin){
        return atomicArray[bin].get();
    }

    public int getSpan(){
        return span;
    }

    public int getAndClear(int bin){
        int oldValue, newValue;
        do {
            oldValue = atomicArray[bin].get();
            newValue = 0;
        } while (!atomicArray[bin].compareAndSet(oldValue, newValue));
        return oldValue;
    }
}
