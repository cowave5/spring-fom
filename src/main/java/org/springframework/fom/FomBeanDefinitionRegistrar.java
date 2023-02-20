package org.springframework.fom;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.fom.annotation.EnableFom;
import org.springframework.fom.annotation.Fom;
import org.springframework.fom.support.controller.FomController;
import org.springframework.fom.support.service.FomServiceImpl;

/**
 * 
 * @author shanhm1991@163.com
 *
 */
public class FomBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar{

	private static AtomicBoolean registed = new AtomicBoolean(false);

	@Override
	public void registerBeanDefinitions(AnnotationMetadata meta, BeanDefinitionRegistry registry) {
		if(!registed.compareAndSet(false, true)){
			return;
		}

		AnnotationAttributes attrs = AnnotationAttributes.fromMap(meta.getAnnotationAttributes(EnableFom.class.getName()));
		if((boolean)attrs.get("enableFomView")){
			// FomController
			RootBeanDefinition fomController = new RootBeanDefinition(FomController.class);
			registry.registerBeanDefinition("fomController", fomController); 

			// FomServiceImpl
			RootBeanDefinition fomServiceImpl = new RootBeanDefinition(FomServiceImpl.class);
			registry.registerBeanDefinition("fomService", fomServiceImpl); 
		}

		// SchedulePostProcessor
		RootBeanDefinition fomBeanPostProcessor = new RootBeanDefinition(FomBeanPostProcessor.class);
		registry.registerBeanDefinition("schedulePostProcessor", fomBeanPostProcessor); 

		// FomScheduleStarter
		RootBeanDefinition fomScheduleStarter = new RootBeanDefinition(FomStarter.class);
		registry.registerBeanDefinition("fomScheduleStarter", fomScheduleStarter); 

		// FomExternalRegister
		RootBeanDefinition fomExternalRegister = new RootBeanDefinition(FomExternalRegister.class);
		registry.registerBeanDefinition("fomExternalRegister", fomExternalRegister); 

		// FomBeanDefinition
		String[] beanNames = registry.getBeanDefinitionNames();
		Class<?> clazz;
		for(String beanName : beanNames){
			BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
			String className = beanDefinition.getBeanClassName();
			if(className != null){
				try {
					clazz = Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new ApplicationContextException("", e);
				}

				Fom fom = clazz.getAnnotation(Fom.class);
				if(fom != null && !fom.external()){ // 忽略external
					parseFomSchedule(beanName, clazz, beanDefinition, fom, registry);
				}
			}
		}
	}

	public void parseFomSchedule(String beanName, Class<?> clazz, BeanDefinition beanDefinition, Fom fom, BeanDefinitionRegistry registry){
		if(ScheduleContext.class.isAssignableFrom(clazz)){
			beanDefinition.getPropertyValues().add("scheduleName", beanName);
			registry.registerAlias(beanName,  "$" + beanName); 
		}else{
			RootBeanDefinition fomBeanDefinition = new RootBeanDefinition(ScheduleContext.class);
			fomBeanDefinition.getPropertyValues().add("scheduleBeanName", beanName);
			fomBeanDefinition.getPropertyValues().add("scheduleName", "$" + beanName);
			registry.registerBeanDefinition("$" + beanName, fomBeanDefinition); 
		}
	}
}
