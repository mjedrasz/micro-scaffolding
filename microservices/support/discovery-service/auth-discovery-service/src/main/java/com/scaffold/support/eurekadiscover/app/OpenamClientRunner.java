package com.scaffold.support.eurekadiscover.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import com.scaffold.support.eurekadiscover.app.OpenamDiscoveryApplication;


@Component
public class OpenamClientRunner implements CommandLineRunner {

    @Override
    public void run(String... strings) throws Exception {
    	System.out.println("i've run tii");
    }
}