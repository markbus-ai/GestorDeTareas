package com.trullo.dao;

import com.trullo.model.Label;
import java.util.List;

public interface TaskLabelDAOInterface {
    void addLabel(Long taskId, Long labelId);
    void removeLabel(Long taskId, Long labelId);
    List<Label> findLabelsByTaskId(Long taskId);
}
