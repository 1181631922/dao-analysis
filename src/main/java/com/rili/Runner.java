package com.rili;

import com.rili.service.ControllerDAOSv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by CYM on 2017/3/6.
 */

@Component
public class Runner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    @Autowired
    private ControllerDAOSv controllerDAOSv;

    @Value("${filePath.controllerDAO.path}")
    private String[] cdPaths;

    @Override
    public void run(String... strings) throws Exception {

        LOGGER.info("controller-dao analysis start...");
        for (String filePath : cdPaths) {
            LOGGER.info("controller-dao analysis file name: {}", filePath);
            controllerDAOSv.analyzeControllerDAOFile(filePath);
        }
        LOGGER.info("controller-dao analysis end...");

    }

}
