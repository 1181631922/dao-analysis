package com.rili;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.rili.Bean.InsertTableBean;
import com.rili.dao.SlaveDAO;
import com.rili.service.ControllerDAOSv;
import com.rili.service.DAOTableSv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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
    private DAOTableSv daoTableSv;

    @Autowired
    private SlaveDAO slaveDAO;

//    @Value("${filePath.controllerDAO.path}")
//    private String[] cdPaths;

    @Override
    public void run(String... strings) throws Exception {

        LOGGER.info("controller-dao analysis start...");
//        for (String filePath : cdPaths) {
//            LOGGER.info("controller-dao analysis file name: {}", filePath);
//            controllerDAOSv.analyzeControllerDAOFile(filePath);
//        }
//        daoTableSv.test("/Users/fanyafeng/Downloads/wine_backend_master/common/src/main/java/com/wine/dao/AccessLogDAO.java");

//        slaveDAO.insertTable(new InsertTableBean("clazz", "method", "table", "operation"));
        try {

            File file = new File("/Users/fanyafeng/Downloads/wine_backend_master/common/src/main/java/com/wine/dao");
            File[] files = file.listFiles();
            List<File> fileList = new ArrayList<>();
            for (File file1 : files) {
                fileList.add(file1);
            }
            for (int i = 0; i < fileList.size(); i++) {
                daoTableSv.test(fileList.get(i).getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        LOGGER.info("controller-dao analysis end...");

    }

}
