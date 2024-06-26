package taskflower.taskflower.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskflower.taskflower.annotation.CurrentUser;
import taskflower.taskflower.security.model.UserPrincipal;
import taskflower.taskflower.service.TaskService;
import taskflower.taskflower.exception.TaskNotFoundExeption;
import taskflower.taskflower.exception.TaskTitleExistException;
import taskflower.taskflower.model.dto.TaskDto;
import taskflower.taskflower.model.entity.User;
import taskflower.taskflower.service.UserService;

import java.util.List;

@RestController
@RequestMapping("${app.api-base-url}/task")
public class TaskConroller {

    private final UserService userService;
    private final TaskService taskService;

    @Autowired
    public TaskConroller(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Object> save(@CurrentUser UserPrincipal userDetails, @RequestBody TaskDto taskDto) {
        try {
            User user = userService.getUserById(userDetails.getId());
            TaskDto task = taskService.save(taskDto, user);
            return ResponseEntity.ok().body(task);
        } catch (TaskTitleExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> findAllByUser(@CurrentUser UserPrincipal userDetails) {
        User user = userService.getUserById(userDetails.getId());
        List<TaskDto> tasks = taskService.findAllByUserEmail(user.getEmail());
        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findTaskById(@PathVariable String id) {
        TaskDto taskResponse;
        try {
            taskResponse = taskService.getTaskById(Long.parseLong(id));
            return ResponseEntity.ok().body(taskResponse);
        } catch (TaskNotFoundExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTaskById(@PathVariable String id, @RequestBody TaskDto taskDto) {
        TaskDto taskResponse;
        try {
            taskResponse = taskService.updateTask(Long.parseLong(id), taskDto);
            return ResponseEntity.ok().body(taskResponse);
        } catch (TaskNotFoundExeption e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}

