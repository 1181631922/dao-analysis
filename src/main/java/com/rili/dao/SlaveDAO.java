package com.rili.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by CYM on 2017/3/13.
 */

@Repository
public class SlaveDAO {

    private static Logger LOGGER = LoggerFactory.getLogger(SlaveDAO.class);

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public void insertControllerDAO(String className, String methodName, String refClassName, String refMethodName, String date) {
        String sql = "INSERT IGNORE INTO controller_dao_analysis (class_name, method_name, ref_class_name, ref_method_name, date) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, className, methodName, refClassName, refMethodName, date);
    }

    public void disableControllerDAO() {
        String sql = "UPDATE controller_dao_analysis SET state = 0";
        jdbcTemplate.update(sql);
    }

    // TODO: 17/3/13 insert,构造参数五个:类名,方法名,表名,操作,时间
    public void insertTable(String clazz, String method, String table, String operation) {
        String sql = "insert into dao_table_analysis(dao_name,dao_method,tb_name,operation) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, clazz, method, table, operation);
    }

    public List<Map<String, Object>> getAllTableDAOData() {
        return jdbcTemplate.queryForList("SELECT dao_name, dao_method, tb_name FROM dao_table_analysis WHERE state = 1 ORDER BY tb_name");
    }

    public List<Map<String, Object>> getTableDAOData(String tableName) {
        return jdbcTemplate.queryForList("SELECT dao_name AS class_name, dao_method AS method_name FROM dao_table_analysis WHERE state = 1 AND tb_name = ?", tableName);
    }

    public List<Map<String, Object>> getClassMethod(String refClassName, String refMethodName) {
        String sql = "SELECT class_name, method_name FROM controller_dao_analysis WHERE state = 1 AND ref_class_name = ? AND ref_method_name = ?";
        return jdbcTemplate.queryForList(sql, refClassName, refMethodName);
    }

}
