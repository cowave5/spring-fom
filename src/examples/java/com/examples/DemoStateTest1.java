package com.examples;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.fom.context.Context;
import com.fom.context.FomContext;
import com.fom.context.Task;

/**
 * 
 * @author shanhm1991
 *
 */
@FomContext(remark="状态测试")
public class DemoStateTest1 extends Context {

	private static final long serialVersionUID = -838223512003059760L;
	
	private Random random = new Random(10000);

	@SuppressWarnings("unchecked")
	@Override
	protected Set<Task<Boolean>> scheduleBatchTasks() throws Exception {
		Set<Task<Boolean>> set = new HashSet<>();
		for(int i = 1; i < 50;i++){
			set.add(new Task<Boolean>("task-" + i){
				@Override
				protected Boolean exec() throws Exception {
					
					Thread.sleep(random.nextInt(10000)); 
					
					return true;
				}
				
			});
		}
		return set;
	}

}
