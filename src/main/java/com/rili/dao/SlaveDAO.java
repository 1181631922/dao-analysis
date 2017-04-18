package com.rili.dao;

import com.rili.bean.InsertTableBean;
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
    public void insertTable(InsertTableBean insertTableBean) {
        String sql = "insert ignore into dao_table_analysis(dao_name,dao_method,tb_name,operation) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, insertTableBean.getClazz(), insertTableBean.getMethod(), insertTableBean.getTable(), insertTableBean.getOperation());
    }

    public List<Map<String, Object>> getAllTableDAOData() {
        return jdbcTemplate.queryForList("SELECT dao_name, dao_method, tb_name FROM dao_table_analysis WHERE state = 1 ORDER BY tb_name");
    }

    public List<Map<String, Object>> getTableDAOData(String tableName) {
        return jdbcTemplate.queryForList("SELECT dao_name, dao_method FROM dao_table_analysis WHERE state = 1 AND tb_name = ?", tableName);
    }

    public List<Map<String, Object>> getClassMethod(String refClassName, String refMethodName) {
        String sql = "SELECT class_name, method_name FROM controller_dao_analysis WHERE state = 1 AND ref_class_name = ? AND ref_method_name = ?";
        return jdbcTemplate.queryForList(sql, refClassName, refMethodName);
    }

    public void insertTableJson(String tbName, String json) {
        jdbcTemplate.update("INSERT INTO table_json (tb_name,json) VALUES (?,?) ON DUPLICATE KEY UPDATE json = ?", tbName, json, json);
    }

    public List<Map<String, Object>> getTableData(String tbName) {
        return jdbcTemplate.queryForList("SELECT c.class_name, c.method_name, c.ref_class_name, c.ref_method_name " +
                "FROM dao_table_analysis d LEFT JOIN controller_dao_analysis c on d.dao_name = c.ref_class_name AND d.dao_method = c.ref_method_name " +
                "WHERE c.state = 1 AND d.state = 1 AND d.tb_name = ? ORDER BY d.tb_name", tbName);
    }

}
