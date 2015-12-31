package net.roy.prototypes.pe.domain;

/**
 * Created by Roy on 2015/12/23.
 */
public class Privilege {
    private long id;
    private String name;
    private String processName;
    private String taskName;
    private String note;

    public Privilege(long id, String name, String processName, String taskName, String note) {
        this.id = id;
        this.name = name;
        this.processName = processName;
        this.taskName = taskName;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return name;
    }
}
