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

    public List<Map<String, Object>> test() {
        String sql = "SELECT * FROM wx_statistics LIMIT 10";
        return jdbcTemplate.queryForList(sql);
    }

    // TODO: 17/3/13 insert,构造参数五个:类名,方法名,表名,操作,时间
    public void insertTable(String clazz, String method, String table, String operation) {
        String sql = "insert into dao_table_analysis(dao_name,dao_method,tb_name,operation) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, clazz, method, table, operation);
    }

}
