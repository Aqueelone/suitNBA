package org.forsstudio.nbatestsuit.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SeasonTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Season getSeasonSample1() {
        return new Season().id(1L).seasonName("seasonName1");
    }

    public static Season getSeasonSample2() {
        return new Season().id(2L).seasonName("seasonName2");
    }

    public static Season getSeasonRandomSampleGenerator() {
        return new Season().id(longCount.incrementAndGet()).seasonName(UUID.randomUUID().toString());
    }
}
