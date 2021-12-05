package info.fareez.planner

import java.time.LocalDate

class Milestone(
    val name: String,
    private val project: Project
) {
    var tasks: MutableMap<String, Task> = mutableMapOf()

    var startsAt: LocalDate = project.startsAt
    var description = ""

    val endsAt: LocalDate
        get() = try {
            tasks.toList().maxOf { it.second.endsAt }
        } catch (e: Exception) {
            startsAt
        }

    fun task(name: String, fn: Task.() -> Unit): Task {
        Task(name, this).apply(fn).also {
            tasks[name] = it
            return it
        }
    }

    fun startsWithProject() {
        startsAt = project.startsAt
    }

    fun startsAfter(milestoneName: String) {
        startsAt = project.milestones[milestoneName]?.endsAt?.plusDays(1)
            ?: throw Exception("Unable to determine the end date for $milestoneName")
    }

    fun startsAfter(milestone: Milestone) = startsAfter(milestone.name)

    fun print() {
        printDetails(startsAt, endsAt, name, description, 1)
        tasks
            .map { it.value }
            .sortedBy { it.startsAt }
            .forEach { it.print() }
    }
}