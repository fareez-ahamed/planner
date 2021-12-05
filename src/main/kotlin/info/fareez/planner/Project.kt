package info.fareez.planner

import jdk.jfr.Description
import java.time.LocalDate

class Project(
    val name: String,
    val startsAt: LocalDate
) {
    var description = ""

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
        printDetails(startsAt, endsAt, name, description, 0)
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

fun printDetails(startDate: LocalDate, endDate: LocalDate, name: String, description: String, level: Int) {
    print("$startDate - $endDate : ")
    for (i in 0..level) {
        print("\t")
    }
    println("$name - $description")
}