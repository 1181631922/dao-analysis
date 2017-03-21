package com.rili.bean;

import java.util.List;

/**
 * Created by CYM on 2017/3/21.
 */
public class RelationBean {

    private String name;
    private List<RelationBean> children;


    public RelationBean(String name) {
        this.name = name;
    }

    public RelationBean(String name, List<RelationBean> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RelationBean> getChildren() {
        return children;
    }

    public void setChildren(List<RelationBean> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "RelationBean{" +
                "name='" + name + '\'' +
                ", children=" + children +
                '}';
    }
}
