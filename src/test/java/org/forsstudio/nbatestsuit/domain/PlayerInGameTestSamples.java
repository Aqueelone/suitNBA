package org.forsstudio.nbatestsuit.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PlayerInGameTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PlayerInGame getPlayerInGameSample1() {
        return new PlayerInGame().id(1L).points(1).rebounds(1).assists(1).steals(1).blocks(1).fouls(1).turnovers(1);
    }

    public static PlayerInGame getPlayerInGameSample2() {
        return new PlayerInGame().id(2L).points(2).rebounds(2).assists(2).steals(2).blocks(2).fouls(2).turnovers(2);
    }

    public static PlayerInGame getPlayerInGameRandomSampleGenerator() {
        return new PlayerInGame()
            .id(longCount.incrementAndGet())
            .points(intCount.incrementAndGet())
            .rebounds(intCount.incrementAndGet())
            .assists(intCount.incrementAndGet())
            .steals(intCount.incrementAndGet())
            .blocks(intCount.incrementAndGet())
            .fouls(intCount.incrementAndGet())
            .turnovers(intCount.incrementAndGet());
    }
}
