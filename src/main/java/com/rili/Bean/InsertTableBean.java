package com.rili.Bean;

/**
 * Author： fanyafeng
 * Data： 17/3/15 17:31
 * Email: fanyafeng@live.cn
 */
public class InsertTableBean implements Comparable<InsertTableBean> {
    private String clazz;
    private String method;
    private String table;
    private String operation;

    public InsertTableBean(String clazz, String method, String table, String operation) {
        this.clazz = clazz;
        this.method = method;
        this.table = table;
        this.operation = operation;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "InsertTable{" +
                "clazz='" + clazz + '\'' +
                ", method='" + method + '\'' +
                ", table='" + table + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }

    @Override
    public int compareTo(InsertTableBean o) {
        int result = 1;
        result = this.clazz.compareTo(o.clazz);
        if (result != 0) {
            return result;
        }

        result = this.method.compareTo(o.method);
        if (result != 0) {
            return result;
        }

        result = this.table.compareTo(o.table);
        if (result != 0) {
            return result;
        }

        result = this.operation.compareTo(o.operation);
        if (result != 0) {
            return result;
        }

        return result;
    }

}
