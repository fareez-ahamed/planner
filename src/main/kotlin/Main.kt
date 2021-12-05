import info.fareez.planner.project
import java.time.*

fun main() {
    configProject().print()
}

fun configProject() =
    project("Project", LocalDate.now()) {
        description = "A Project for X requirement"
        milestone("Phase 1") {
            description = "Initial Development for building the platform"
            val t1 = task("P1-T001") {
                description = "Implement Authentication"
                days = 10
            }
            task("P1-T002") {
                description = "Implement Roles and Admin accounts"
                startsAfter(t1)
                days = 5
            }
        }
        milestone("Phase 2") {
            description = "MVP Completion"
            startsAfter("Phase 1")
            task("P2-T001") {
                description = "Feature 1"
                days = 3
            } dependsOn task("P2-T002") {
                description = "Feature 2"
                days = 2
            } followedBy task("P2-T003") {
                description = "Feature 3"
                days = 8
            }
        }
    }
