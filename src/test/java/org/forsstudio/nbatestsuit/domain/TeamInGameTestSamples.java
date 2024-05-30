package org.forsstudio.nbatestsuit.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TeamInGameTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TeamInGame getTeamInGameSample1() {
        return new TeamInGame().id(1L);
    }

    public static TeamInGame getTeamInGameSample2() {
        return new TeamInGame().id(2L);
    }

    public static TeamInGame getTeamInGameRandomSampleGenerator() {
        return new TeamInGame().id(longCount.incrementAndGet());
    }
}
