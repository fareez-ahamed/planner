package info.fareez.planner

import java.time.Duration
import java.time.LocalDate

class Task(
    val name: String,
    private val milestone: Milestone
) {

    var startsAt: LocalDate = milestone.startsAt
    private var _endsAt: LocalDate? = null

    var days: Int = 0
        get() {
            if (_endsAt != null)
                return Duration.between(startsAt, _endsAt).toDays().toInt()
            return field
        }

    var endsAt: LocalDate
        get() {
            if (_endsAt != null)
                return _endsAt ?: throw Exception("End date is null")
            return startsAt.plusDays(days.toLong())
        }
        set(value) {
            _endsAt = value
        }

    fun startsAfter(taskName: String) {
        startsAt = milestone.tasks[taskName]?.endsAt?.plusDays(1)
            ?: throw Exception("Unable to determine the end date for $taskName")
    }

    infix fun followedBy(task: Task): Task {
        task.startsAfter(name)
        return task
    }

    infix fun dependsOn(task: Task): Task {
        startsAfter(task.name)
        return task
    }

    fun print() {
        println("$startsAt - $endsAt: \t\t$name")
    }
}