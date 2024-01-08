plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.7.0")
}

rootProject.name = "ReallyFakePlayers"

buildCache {
    local {
        // gradle takes up so much space lmao
        removeUnusedEntriesAfterDays = 30
    }
}
