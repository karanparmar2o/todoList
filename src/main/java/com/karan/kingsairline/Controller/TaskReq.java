package com.karan.kingsairline.Controller;

import com.karan.kingsairline.modules.Status;
import lombok.Data;

@Data
public class TaskReq {
    private String tname;
    private Status status;
    private int uId;
    private int cId;
}
