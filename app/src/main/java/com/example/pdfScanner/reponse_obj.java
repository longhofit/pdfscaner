package com.example.pdfScanner;

public class reponse_obj {
    String name,success,message;

    public reponse_obj(String name, String success, String message) {
        this.name = name;
        this.success = success;
        this.message = message;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
