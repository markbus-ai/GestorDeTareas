package com.trullo.dao;

import com.trullo.model.Label;
import java.util.List;

public interface LabelDAOInterface extends BaseDAOInterface<Label> {
    List<Label> findByTaskId(Long taskId);
}
