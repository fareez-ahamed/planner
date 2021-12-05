import java.time.*


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

    fun print() {
        println("$startsAt - $endsAt: \t$name")
        tasks
            .map { it.value }
            .sortedBy { it.startsAt }
            .forEach { it.print() }
    }
}

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
