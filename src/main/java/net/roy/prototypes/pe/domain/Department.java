package net.roy.prototypes.pe.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Roy on 2015/12/21.
 */
public class Department {
    private long id;
    private String name;
    private long parent_id;
    private boolean is_root;

    public Department(long id, String name, long parent_id, boolean is_root) {
        this.id = id;
        this.name = name;
        this.parent_id = parent_id;
        this.is_root = is_root;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getParentId() {
        return parent_id;
    }

    public boolean isRoot() {
        return is_root;
    }

    public void setName(String name) {
        this.name=name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
