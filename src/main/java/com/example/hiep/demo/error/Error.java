package com.example.hiep.demo.error;

import java.io.Serializable;

public class Error implements Serializable {

    private int errStatus;
    private String errMsg;

    public Error() {
    }

    public Error(int errStatus, String errMsg) {
        this.errStatus = errStatus;
        this.errMsg = errMsg;
    }

    public int getErrStatus() {
        return errStatus;
    }

    public void setErrStatus(int errStatus) {
        this.errStatus = errStatus;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "Error{" + "errStatus=" + errStatus + ", errMsg=" + errMsg + '}';
    }

}
