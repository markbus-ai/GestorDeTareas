package com.trullo.dao;

import com.trullo.model.Reminder;
import java.util.List;

public interface ReminderDAOInterface extends BaseDAOInterface<Reminder> {
    List<Reminder> findByTaskId(Long taskId);
    List<Reminder> findPending();
}
