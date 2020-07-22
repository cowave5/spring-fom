package org.eto.fom.boot;

import java.io.File;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.eto.fom.boot.listener.FomContextListener;
import org.eto.fom.boot.listener.PoolListener;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 启动参数:<br>
 * -Dwebapp.root="/"<br>
 * -Dcache.root="/WEB-INF/cache"<br>
 * -Dlog.root="/log"<br>
 * -Dlog4jConfigLocation="/log4j.properties"<br>
 * -DfomConfigLocation="/WEB-INF/fom.xml"<br>
 * -DpoolConfigLocation="/WEB-INF/pool.xml"<br>
 * 
 * @author shanhm
 *
 */
@Configuration
public class FomConfiguration implements ServletContextInitializer {
	
	private static final int ORDER4 = 4;
	
	private static final int ORDER5 = 5;
	
	private static final long AGE = 3600l;
	
	@Bean
	public ServletListenerRegistrationBean<PoolListener> listenPool(){
		ServletListenerRegistrationBean<PoolListener> listener = new ServletListenerRegistrationBean<>();
		listener.setListener(new PoolListener());
		listener.setOrder(ORDER4);
		return listener;
	}

	@Bean
	public ServletListenerRegistrationBean<FomContextListener> listenConfig(){
		ServletListenerRegistrationBean<FomContextListener> listener = new ServletListenerRegistrationBean<>();
		listener.setListener(new FomContextListener());
		listener.setOrder(ORDER5);
		return listener;
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildConfig()); 
		return new CorsFilter(source);
	}
	
	//跨域问题
	@Bean
	public CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.setAllowCredentials(true);//这两句不加不能跨域上传文件，
		corsConfiguration.setMaxAge(AGE);//加上去就可以了
		return corsConfiguration;
	}
	
	//文件上传问题
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String location = System.getProperty("user.dir") + "/data/tmp";
        File tmpFile = new File(location);
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }
        factory.setLocation(location);
        return factory.createMultipartConfig();
    }

	@Bean
	public ServletWebServerFactory servletContainer() {
		return new TomcatServletWebServerFactory() {

			@Override
			protected void postProcessContext(Context context) {
				super.postProcessContext(context);

				String root = System.getProperty("webapp.root");
				if(StringUtils.isBlank(root)){
					root = ClassLoader.getSystemResource("").getPath();
					String os = System.getProperty("os.name");
			    	if(os.toLowerCase().indexOf("windows") != -1){
			    		root = root.substring(1);
			    	}
					System.setProperty("webapp.root", root);
				}
				context.setDocBase(root); 

				String logRoot = System.getProperty("log.root");
				if(StringUtils.isBlank(logRoot)){ 
					System.setProperty("log.root", root + File.separator + "log");
				}

				String logPath = System.getProperty("log4jConfigLocation");
				if(StringUtils.isBlank(logPath)){
					PropertyConfigurator.configure(root + File.separator + "/log4j.properties");
				}
			}
		};
	}

	@Override
	public void onStartup(ServletContext context) throws ServletException {
		String fomPath = System.getProperty("fomConfigLocation");
		if(StringUtils.isBlank(fomPath)){
			fomPath = "/WEB-INF/fom.xml";
		}
		String poolpath = System.getProperty("poolConfigLocation");
		if(StringUtils.isBlank(poolpath)){
			poolpath = "/WEB-INF/pool.xml";
		}
		context.setInitParameter("fomConfigLocation", fomPath);
		context.setInitParameter("poolConfigLocation", poolpath);	
		ServletUtil.set(context); 
	}
}
