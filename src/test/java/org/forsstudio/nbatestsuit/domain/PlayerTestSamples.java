package org.forsstudio.nbatestsuit.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PlayerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Player getPlayerSample1() {
        return new Player().id(1L).name("name1");
    }

    public static Player getPlayerSample2() {
        return new Player().id(2L).name("name2");
    }

    public static Player getPlayerRandomSampleGenerator() {
        return new Player().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
