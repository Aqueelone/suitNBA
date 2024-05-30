package org.forsstudio.nbatestsuit.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TeamInSeasonTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TeamInSeason getTeamInSeasonSample1() {
        return new TeamInSeason().id(1L);
    }

    public static TeamInSeason getTeamInSeasonSample2() {
        return new TeamInSeason().id(2L);
    }

    public static TeamInSeason getTeamInSeasonRandomSampleGenerator() {
        return new TeamInSeason().id(longCount.incrementAndGet());
    }
}
