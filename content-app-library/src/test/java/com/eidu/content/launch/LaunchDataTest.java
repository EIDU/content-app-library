package com.eidu.content.launch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LaunchDataTest {
    String contentId = "content id";
    String contentUnitRunId = "content unit run id";
    String learnerId = "";
    String schoolId = "";
    String stage = "";
    Long remainingForegroundTimeInMs = 1L;
    Long inactivityTimeoutInMs = 2L;

    @Test
    public void fromPlainDataReturnsLaunchData() {
        LaunchData launchData =
                LaunchData.fromPlainData(
                        contentId,
                        contentUnitRunId,
                        learnerId,
                        schoolId,
                        stage,
                        remainingForegroundTimeInMs,
                        inactivityTimeoutInMs);

        assertEquals(contentId, launchData.contentId);
        assertEquals(contentUnitRunId, launchData.contentUnitRunId);
        assertEquals(learnerId, launchData.learnerId);
        assertEquals(schoolId, launchData.schoolId);
        assertEquals(stage, launchData.stage);
        assertEquals(remainingForegroundTimeInMs, launchData.remainingForegroundTimeInMs);
        assertEquals(inactivityTimeoutInMs, launchData.inactivityTimeoutInMs);
    }
}
