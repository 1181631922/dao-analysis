package com.rili;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.rili.bean.RelationBean;
import com.rili.dao.SlaveDAO;
import com.rili.service.ControllerDAOSv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by CYM on 2017/3/6.
 */

@Component
public class Runner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    @Autowired
    private ControllerDAOSv controllerDAOSv;

    @Autowired
    private SlaveDAO slaveDAO;

    @Value("${filePath.controllerDAO.path}")
    private String[] cdPaths;

    @Value("${analysis.table.name}")
    private String[] tableNames;

    @Override
    public void run(String... strings) throws Exception {
        LOGGER.info("controller-dao、dao-tables分析...");
        doAnalysis();
        LOGGER.info("controller-dao、dao-tables分析结束...");
//        LOGGER.info("表数据分析...");
//        tablesAnalysis();
//        LOGGER.info("表数据分析结束...");
    }


    private void doAnalysis() {
        LOGGER.info("controller-dao analysis start...");
        slaveDAO.disableControllerDAO();
        for (String filePath : cdPaths) {
            LOGGER.info("controller-dao analysis file name: {}", filePath);
            controllerDAOSv.analyzeControllerDAOFile(filePath);
        }
        LOGGER.info("controller-dao analysis end...");
        // TODO: 2017/3/16 添加DAO-TABLE分析

    }

    private void tablesAnalysis() {
        LOGGER.info("tables analysis start...");
        LOGGER.info("tables:{}", Arrays.toString(tableNames));
        for (String tbName : tableNames) {
            LOGGER.info("table:{}", tbName);
            initTableData(tbName);
        }
        LOGGER.info("tables analysis end...");
    }

    private void initTableData(String tableName) {
        Gson gson = new Gson();
        List<RelationBean> data = Lists.newArrayList();
        data.add(controllerDAOSv.getTableRelationBean(tableName));
        slaveDAO.insertTableJson(tableName, gson.toJson(data));
        LOGGER.info("table:{}, data:{}", tableName, gson.toJson(data));
    }

}
