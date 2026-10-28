package com.trullo.interfaces.dao;

import com.trullo.model.Label;
import java.util.List;
import com.trullo.interfaces.BaseDAOInterface;

public interface LabelDAOInterface extends BaseDAOInterface<Label> {
    List<Label> findByTaskId(Long taskId);
}
