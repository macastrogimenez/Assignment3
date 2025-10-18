package exercises06;

// JUnit testing imports

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.List;


public class TestHistograms {
    // The imports above are just for convenience, feel free add or remove imports

    // TODO: 5.1.3

    private static int countFactors(int p) {
        if (p < 2) return 0;
        int factorCount = 1, k = 2;
        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p = p / k;
            } else {
                k = k + 1;
            }
        }
        return factorCount;
    }

    @Test
    public void testParallelCasHistogram() throws InterruptedException {
        final int RANGE = 5000;
        final int SPAN = 30;

        // Sequential baseline
        Histogram1 seqHist = new Histogram1(SPAN);
        for (int i = 0; i < RANGE; i++) {
            int factors = countFactors(i);
            seqHist.increment(factors);
        }

        // Parallel version
        CasHistogram casHist = new CasHistogram();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < RANGE; i++) {
            final int num = i;
            Thread t = new Thread(() -> {
                int factors = countFactors(num);
                casHist.increment(factors);
            });
            threads.add(t);
            t.start();
        }

        // Join all threads
        for (Thread t : threads) {
            t.join();
        }

        // Compare results
        for (int bin = 0; bin < SPAN; bin++) {
            assertEquals(
                seqHist.getCount(bin),
                casHist.getCount(bin),
                "Mismatch at bin " + bin
            );
        }
    }
}

