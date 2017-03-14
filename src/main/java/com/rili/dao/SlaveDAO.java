package com.rili.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by CYM on 2017/3/13.
 */

@Repository
public class SlaveDAO {

    private static Logger LOGGER = LoggerFactory.getLogger(SlaveDAO.class);

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public void insertControllerDAO(String className, String methodName, String refClassName, String refMethodName) {
        String sql = "INSERT INTO controller_dao_analysis (class_name, method_name, ref_class_name, ref_method_name) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, className, methodName, refClassName, refMethodName);
    }

    // TODO: 17/3/13 insert,构造参数五个:类名,方法名,表名,操作,时间
    public void insertTable(String clazz, String method, String tableName, String operate, long time) {
        jdbcTemplate.update("");
    }

}
