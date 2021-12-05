import java.time.*


class Task(
    private val name: String,
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

    fun print() {
        println("$startsAt: Task $name starts and ends at $endsAt")
    }
}

class Milestone(
    private val name: String,
    private val project: Project
) {
    var tasks: MutableMap<String, Task> = mutableMapOf()

    var startsAt: LocalDate = project.startsAt

    val endsAt: LocalDate
        get() = try {
            tasks.toList().maxOf { it.second.endsAt }
        } catch (e: Exception) {
            startsAt
        }

    fun task(name: String, fn: Task.() -> Unit) {
        tasks[name] = Task(name, this).apply(fn)
    }

    fun startsWithProject() {
        startsAt = project.startsAt
    }

    fun print() {
        println("$startsAt: Milestone $name starts and ends at $endsAt")
        tasks.forEach {
            it.value.print()
        }
    }
}

class Project(
    val name: String,
    val startsAt: LocalDate
) {

    private val milestones: MutableMap<String, Milestone> = mutableMapOf()

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
        println("$startsAt: Project $name starts and ends at $endsAt")
        milestones.forEach {
            it.value.print()
        }
    }
}

fun project(name: String, startDate: LocalDate, fn: Project.() -> Unit): Project {
    return Project(name, startDate).apply {
        fn(this)
    }
}

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
    }
