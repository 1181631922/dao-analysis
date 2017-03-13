package com.rili;

import com.rili.dao.SlaveDAO;
import com.rili.service.ControllerDAOSv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SlaveDAO slaveDAO;

    @Override
    public void run(String... strings) throws Exception {
        controllerDAOSv.test();
    }

}
