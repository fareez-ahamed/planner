package info.fareez.planner

import java.time.LocalDate

class Project(
    val name: String,
    val startsAt: LocalDate
) {

    val milestones: MutableMap<String, Milestone> = mutableMapOf()

    val endsAt: LocalDate
        get() = try {
            milestones.toList().maxOf { it.second.endsAt }
        } catch (e: Exception) {
            startsAt
        }

    fun milestone(name: String, fn: Milestone.() -> Unit) {
        milestones[name] = Milestone(name, this).apply(fn)
    }

    override fun toString(): String {
        return "$name on $startsAt"
    }

    fun print() {
        println("$startsAt - $endsAt: $name")
        milestones
            .map { it.value }
            .sortedBy { it.startsAt }
            .forEach { it.print() }
    }
}

fun project(name: String, startDate: LocalDate, fn: Project.() -> Unit): Project {
    return Project(name, startDate).apply {
        fn(this)
    }
}