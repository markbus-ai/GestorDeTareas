package com.trullo.interfaces.dao;

import com.trullo.model.Reminder;
import java.util.List;
import com.trullo.interfaces.BaseDAOInterface;


public interface ReminderDAOInterface extends BaseDAOInterface<Reminder> {
    List<Reminder> findByTaskId(Long taskId);
    List<Reminder> findPending();
}
