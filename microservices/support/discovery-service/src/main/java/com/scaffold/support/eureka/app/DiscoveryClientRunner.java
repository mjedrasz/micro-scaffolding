package com.scaffold.support.eureka.app;

import java.util.Collections;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.netflix.eureka.DataCenterAwareMarshallingStrategy.InstanceIdDataCenterInfo;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.PeerAwareInstanceRegistry;


@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DiscoveryClientRunner implements CommandLineRunner, ApplicationListener<EurekaRegistryAvailableEvent> {
  
	@Override
    public void onApplicationEvent(EurekaRegistryAvailableEvent event) {
		System.out.println("registered");
			y();
			ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
			threadPoolTaskScheduler.initialize();
			threadPoolTaskScheduler.scheduleAtFixedRate(() -> {
				PeerAwareInstanceRegistry.getInstance().renew("TESTOPENAM", "openam1", false);
				InstanceInfo s1 = PeerAwareInstanceRegistry.getInstance().getInstanceByAppAndId("SAMPLE-SERVICE", "marek-pc");
				InstanceInfo s2 = PeerAwareInstanceRegistry.getInstance().getInstanceByAppAndId("TESTOPENAM", "openam1");
				String reflectionToString1 = ToStringBuilder.reflectionToString(s1);
				String reflectionToString2 = ToStringBuilder.reflectionToString(s2);
				System.out.println(reflectionToString1);
				System.out.println(reflectionToString2);
			}, 30000);
    }

    @Override
    public void run(String... strings) throws Exception {
    	System.out.println("i've run");
		// new SimpleAsyncTaskExecutor().execute(() -> {
		// OpenamDiscoveryApplication.main();
//    	});
//    	x();
//    	y();
    }
    
    public void y() {
    	InstanceInfo info = InstanceInfo.Builder.newBuilder()
    			.setStatus(InstanceStatus.UP)
    			.setAppName("testopenam")
    			.setHostName("localhost")
    			.setPort(8080)
    			.setVIPAddress("testopenam")
//    			.setMetadata(new HashMap() {{put("instanceId", "openam1");}})
    			.setDataCenterInfo(new InstanceIdDataCenterInfo("openam1"))
    			.setHomePageUrl("/OpenAM-12.0.0/isAlive.jsp", "http://localhost:8080/OpenAM-12.0.0/isAlive.jsp")
    			.setStatusPageUrl("/OpenAM-12.0.0/isAlive.jsp", "http://localhost:8080/OpenAM-12.0.0/isAlive.jsp")
    			.setHealthCheckUrls("/OpenAM-12.0.0/isAlive.jsp", "http://localhost:8080/OpenAM-12.0.0/isAlive.jsp", null)
    			.build();
    	info.getId();
    	
		PeerAwareInstanceRegistry.getInstance().register(info, false);
    }
    
    
   public  EurekaInstanceConfig eurekaInstanceConfigBean() {
        

        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean();
        config.setNonSecurePort(8080);
        config.setPreferIpAddress(false);
        config.setIpAddress("127.0.0.1");
        config.setAppname("openamtest");
        config.setHostname("localhost");
        config.getMetadataMap().put("instanceId", "openamtest1");
        
        return config;
    }
    
    private static final String DEFAULT_REGION = "default";
	private static final String DEFAULT_ZONE = "defaultZone";
	private static final int REGISTRY_FETCH_INTERVAL_SECS = 5;
	private static final String EUREKA_API_PREFIX = "/eureka/";


	protected EurekaClientConfig getEurekaClientConfig() {
		EurekaClientConfigBean eurekaClientConfigBean = new EurekaClientConfigBean();
		eurekaClientConfigBean.setServiceUrl(Collections.singletonMap(DEFAULT_ZONE, "http://localhost:8761" + EUREKA_API_PREFIX));
		eurekaClientConfigBean.setRegion(DEFAULT_REGION);
		eurekaClientConfigBean.setRegistryFetchIntervalSeconds(REGISTRY_FETCH_INTERVAL_SECS);

		return eurekaClientConfigBean;
	}
    
    public void x() {
    	EurekaInstanceConfig instanceConfig = eurekaInstanceConfigBean();
    	EurekaClientConfig eurekaClientConfig = getEurekaClientConfig();
    	
    	DiscoveryManager instance = DiscoveryManager.getInstance();
    	instance.shutdownComponent();
    	ApplicationInfoManager.getInstance().initComponent(instanceConfig);
		instance.initComponent(instanceConfig, eurekaClientConfig);
    }
}