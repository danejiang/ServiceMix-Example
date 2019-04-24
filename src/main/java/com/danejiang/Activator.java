package com.danejiang;

import com.danejiang.service.mainService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext arg0) throws Exception {
        mainService.start();
        System.out.println("start bundle!");
    }

    @Override
    public void stop(BundleContext arg0) throws Exception {
        mainService.stop();
        System.out.println("stop bundle!");
    }
}

