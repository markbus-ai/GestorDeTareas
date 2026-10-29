package com.trullo.interfaces.dao;

import com.trullo.model.Reminder;
import java.util.List;
import com.trullo.interfaces.BaseInterface;

public interface ReminderDAOInterface extends BaseInterface<Reminder> {
    List<Reminder> findByTaskId(Long taskId);
    List<Reminder> findPending();
}
