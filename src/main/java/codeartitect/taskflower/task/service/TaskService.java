package codeartitect.taskflower.task.service;

import codeartitect.taskflower.event.Event;
import codeartitect.taskflower.flow.Flow;
import codeartitect.taskflower.task.repository.TaskItemRepository;
import codeartitect.taskflower.task.exception.TaskNotFoundException;
import codeartitect.taskflower.task.repository.TaskRepository;
import codeartitect.taskflower.task.model.Task;
import codeartitect.taskflower.task.payload.SaveTaskRequest;
import codeartitect.taskflower.task.payload.TaskResponse;
import codeartitect.taskflower.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskItemRepository taskitemRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskItemRepository taskitemRepository) {
        this.taskRepository = taskRepository;
        this.taskitemRepository = taskitemRepository;
    }

    /**
     * Save task
     * @param user User information
     * @param saveTaskRequest Information to save a task
     * @return TaskResponse
     */
    public TaskResponse save(User user, SaveTaskRequest saveTaskRequest) {
        Task task = new Task(user, saveTaskRequest);
        taskRepository.save(task);

        return new TaskResponse(task);
    }

    /**
     * Get task by task id
     * @param id Id of task
     * @return TaskResponse
     */
    public TaskResponse getById(Long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        return new TaskResponse(task);
    }

    /**
     * Find all tasks by user info
     * @param user User information
     * @return TaskResponse list
     */
    public List<TaskResponse> findAll(User user) {
        List<Optional<Task>> tasks = taskRepository.findAllByUser(user);

        List<TaskResponse> taskResponseList = new ArrayList<>();
        for (Optional<Task> task : tasks) {
            task.ifPresent(value -> taskResponseList.add(new TaskResponse(value)));
        }

        return taskResponseList;
    }

    /**
     * Find all task filtered by flow info or event info
     * Both flow and event should not be null
     * Either flow or event should be null
     * @param user User information - not null
     * @param flow Flow information - nullable
     * @param event Event information - nullable
     * @return TaskResponse list
     */
    public List<TaskResponse> findAllByFlowOrEvent(User user, Flow flow, Event event) {
        List<Optional<Task>> tasks;
        if (flow == null && event == null) {
            throw new IllegalStateException("Invalid parameter - flow, event");
        } else if (flow == null) {
            tasks = taskRepository.findAllByUserAndEvent(user, event);
        } else if (event == null) {
            tasks = taskRepository.findAllByUserAndFlow(user, flow);
        } else {
            tasks = taskRepository.findAllByUserAndFlowAndEvent(user, flow, event);
        }

        List<TaskResponse> taskResponseList = new ArrayList<>();
        for (Optional<Task> task : tasks) {
            task.ifPresent(value -> taskResponseList.add(new TaskResponse(value)));
        }

        return taskResponseList;
    }

    /**
     * Update task information
     * @param taskId Task id for update task information
     * @param saveTaskRequest Task information to be updated
     * @return TaskResponse
     */
    public TaskResponse updateTask(Long taskId, SaveTaskRequest saveTaskRequest) throws TaskNotFoundException {
        Task task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        task.update(saveTaskRequest);

        Task updatedTask = taskRepository.save(task);
        return new TaskResponse(updatedTask);
    }

    /**
     * Delete task
     * @param id Task id for delete task
     */
    public void deleteById(Long id) throws TaskNotFoundException {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else throw new TaskNotFoundException();
    }

    public void deleteAllByUser(User user) {
        List<Optional<Task>> tasks = taskRepository.findAllByUser(user);
        for (Optional<Task> task : tasks) {
            task.ifPresent(taskitemRepository::deleteAllByTask);
        }
        taskRepository.deleteAllByUser(user);
    }
}
