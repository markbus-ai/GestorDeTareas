package com.trullo.interfaces.dao;

import com.trullo.model.Task;
import com.trullo.model.TaskPriority;
import com.trullo.model.TaskStatus;
import java.util.List;
import com.trullo.interfaces.BaseDAOInterface;

public interface TaskDAOInterface extends BaseDAOInterface<Task> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByPriority(TaskPriority priority);
    List<Task> findByLabelId(Long labelId);
    List<Task> findOverdue();
    List<Task> findDueToday(int days);
}
