package net.canarymod.tasks;

import net.canarymod.plugin.Plugin;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static net.canarymod.Canary.log;

/**
 * Server Task Manager
 * <p/>
 * The Manager for {@link ServerTask}<br>
 * If an exception occurs while running a task, it gets removed from the queue, regardless of continuous status
 *
 * @author Jason (darkdiplomat)
 */
public final class ServerTaskManager {
    private final static ServerTaskManager $;
    private final ConcurrentHashMap<ServerTask, TaskOwner> tasks;

    static {
        $ = new ServerTaskManager();
    }

    private ServerTaskManager() {
        tasks = new ConcurrentHashMap<ServerTask, TaskOwner>();
    }

    /**
     * Adds a {@link ServerTask} to the queue
     *
     * @param task
     *         the {@link ServerTask} to be added
     *
     * @return {@code true} if successfully added; {@code false} if not
     */
    public static boolean addTask(ServerTask task) {
        synchronized ($.tasks) {
            $.tasks.put(task, task.getOwner());
            return true;
        }
    }

    /**
     * Removes a {@link ServerTask} from the queue<br>
     * When a {@link Plugin} is disabled, it should remove it's tasks from the queue
     *
     * @param task
     *         the {@link ServerTask} to be removed
     *
     * @return {@code true} if removed; {@code false} if not found or unable to be removed
     */
    public static boolean removeTask(ServerTask task) {
        synchronized ($.tasks) {
            return $.tasks.remove(task) != null;
        }
    }

    /**
     * Removes all the tasks for a specified {@link TaskOwner}
     *
     * @param owner
     *         the {@link TaskOwner} to remove tasks for
     */
    public static void removeTasks(TaskOwner owner) {
        synchronized ($.tasks) {
            Iterator<Entry<ServerTask, TaskOwner>> taskIter = $.tasks.entrySet().iterator();
            while (taskIter.hasNext()) {
                if (taskIter.next().getValue().equals(owner)) {
                    taskIter.remove();
                }
            }
        }
    }

    /**
     * Internal method called to run the tasks or decrease timers.
     */
    public static void runTasks() {
        if ($.tasks.isEmpty()) {
            // No tasks? no execution needed
            return;
        }
        synchronized ($.tasks) {
            Iterator<Entry<ServerTask, TaskOwner>> taskIter = $.tasks.entrySet().iterator();
            while (taskIter.hasNext()) {
                ServerTask task = taskIter.next().getKey();
                task.decrementDelay();
                if (task.shouldExecute()) {
                    try {
                        task.run();
                    }
                    catch (Throwable thrown) {
                        log.error("An Exception occurred while executing ServerTask: " + task.getClass().getSimpleName(), thrown);
                        taskIter.remove();
                        continue;
                    }
                    if (!task.isContinuous()) {
                        taskIter.remove();
                    }
                    else {
                        task.reset();
                    }
                }
            }
        }
    }
}
