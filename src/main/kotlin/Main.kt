import info.fareez.planner.project
import java.time.*

fun main() {
    configProject().print()
}

fun configProject() =
    project("Hornet", LocalDate.now()) {
        milestone("Phase 1") {
            task("Task One") {
                days = 10
            }
            task("Task Two") {
                startsAfter("Task One")
                days = 5
            }
        }
        milestone("Phase 2") {
            startsAfter("Phase 1")
            task("P2 T1") {
                days = 3
            } dependsOn task("P2 T2") {
                days = 2
            } followedBy task("P2 T3") {
                days = 8
            }
        }
    }
