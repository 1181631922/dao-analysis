package com.rili;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.rili.bean.RelationBean;
import com.rili.dao.SlaveDAO;
import com.rili.service.ControllerDAOSv;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

    @Override
    public void run(String... strings) throws Exception {

//        boolean doAnalysis = false;
//        boolean doTables = false;
//        String tempStr;
//        String tempStr2;
//        String tables = null;
//        List<String> arguments = Lists.newArrayList(strings);
//        for (String argument : arguments) {
//            LOGGER.info("argument:{}", argument);
//        }
//        if (arguments.isEmpty() || arguments.size() > 2) {
//            // 参数报错
//            LOGGER.error("please run with correct arguments...");
//        } else if (arguments.size() == 1) {
//            tempStr = StringUtils.replaceAll(arguments.get(0).trim(), " ", "");
//            if (Constant.DO_ANALYSIS.equals(tempStr)) {
//                doAnalysis = true;
//            } else if (StringUtils.indexOf(tempStr, Constant.TABLES) == 0) {
//                doTables = true;
//                tables = StringUtils.substring(tempStr, StringUtils.indexOf(tempStr, "=") + 1);
//            }
//        } else {
//            tempStr = StringUtils.replaceAll(arguments.get(0).trim(), " ", "");
//            tempStr2 = StringUtils.replaceAll(arguments.get(1).trim(), " ", "");
//            if ((Constant.DO_ANALYSIS.equals(tempStr) && StringUtils.indexOf(tempStr2, Constant.TABLES) == 0) ||
//                    (Constant.DO_ANALYSIS.equals(tempStr2) && StringUtils.indexOf(tempStr, Constant.TABLES) == 0)) {
//                doAnalysis = true;
//                doTables = true;
//
//                if (StringUtils.indexOf(tempStr2, Constant.TABLES) == 0) {
//                    tables = StringUtils.substring(tempStr2, StringUtils.indexOf(tempStr2, "=") + 1);
//                } else {
//                    tables = StringUtils.substring(tempStr, StringUtils.indexOf(tempStr, "=") + 1);
//                }
//            }
//        }
//
//        if (doAnalysis && doTables) {
//            doAnalysis();
//            doTables(tables);
//
//        } else if (doAnalysis) {
//            doAnalysis();
//        } else if (doTables) {
//            doTables(tables);
//        }

        Gson gson = new Gson();
        List<RelationBean> result = Lists.newArrayList();
        result.add(controllerDAOSv.getOrderTest());
        slaveDAO.insertTableJson("orders", gson.toJson(result));
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

    private void doTables(String tables) {
        Gson gson = new Gson();
        for (String tb : tables.split(",")) {
            if (StringUtils.isNotEmpty(tb)) {
                List<RelationBean> result = Lists.newArrayList();
                result.add(controllerDAOSv.getRelation(tb.trim()));
                String json = gson.toJson(result);
                LOGGER.info("******* TABLE:{} RESULT:{} *******", tb, json);
                slaveDAO.insertTableJson(tb, json);
            }
        }
    }

}
