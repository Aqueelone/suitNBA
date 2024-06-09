package org.forsstudio.nbatestsuit.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PlayerInTeamTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PlayerInTeam getPlayerInTeamSample1() {
        return new PlayerInTeam().id(1L);
    }

    public static PlayerInTeam getPlayerInTeamSample2() {
        return new PlayerInTeam().id(2L);
    }

    public static PlayerInTeam getPlayerInTeamRandomSampleGenerator() {
        return new PlayerInTeam().id(longCount.incrementAndGet());
    }
}
