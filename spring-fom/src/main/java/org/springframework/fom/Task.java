package org.springframework.fom;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.fom.ScheduleContext.ScheduleBatch;

/**
 * 
 * @param <E> 任务执行结果类型
 * 
 * @author shanhm1991@163.com
 *
 */
public abstract class Task<E> implements Callable<Result<E>> {

	protected volatile Logger logger = LoggerFactory.getLogger(Task.class);

	protected final String id;

	private final long createTime;

	// 任务线程自己设置，其它线程读取
	private volatile long startTime;

	// 轮询线程设置，任务线程读取
	private volatile ScheduleContext<E> scheduleContext;

	// 轮询线程设置，任务线程读取
	private volatile ScheduleBatch<E> scheduleBatch;
	
	public Task(){
		this.id = Thread.currentThread().getName() + "-Task";
		this.createTime = System.currentTimeMillis();
	}

	public Task(String id) { 
		this.id = id;
		this.createTime = System.currentTimeMillis();
	}

	@Override
	public final Result<E> call() throws InterruptedException {   
		Thread.currentThread().setName("task[" + id + "]");
		logger.debug("task started."); 

		this.startTime = System.currentTimeMillis();
		final Result<E> result = new Result<>(id, createTime, startTime); 
		doCall(result);
		result.setCostTime(System.currentTimeMillis() - startTime); 

		if(scheduleContext != null){
			if(scheduleBatch != null){
				scheduleBatch.addResult(result); 
				scheduleContext.checkScheduleComplete(scheduleBatch);
			}
			scheduleContext.record(result); 
		}

		if(result.isSuccess()){
			logger.info("task success, cost={}ms {}", result.getCostTime(), result.getContent());
		}else{
			logger.warn("task failed, cost={}ms {}", result.getCostTime(), result.getContent());
		}
		return result;
	}

	private void doCall(Result<E> result){
		try {
			if(!beforeExec()){
				result.setSuccess(false); 
				return;
			}
			result.setContent(exec());
		} catch(Throwable e) {
			logger.error("", e); 
			result.setSuccess(false); 
			result.setThrowable(e);
		} finally{
			try {
				afterExec(result.isSuccess(), result.getContent(), result.getThrowable());
			}catch(Throwable e) {
				logger.error("", e); 
				result.setSuccess(false); 
				result.setThrowable(e);; // exec的异常已经交给afterExec处理过，这里覆盖掉也能接受
			}
		}
	}

	public boolean beforeExec() throws Exception {
		return true;
	}

	public abstract E exec() throws Exception;

	public void afterExec(boolean isExecSuccess,  E content, Throwable e) throws Exception {

	}
	
	public final String getTaskId() {
		return id;
	}

	public final long getCreateTime() {
		return createTime;
	}

	public final long getStartTime() {
		return startTime;
	}

	ScheduleBatch<E> getScheduleBatch() {
		return scheduleBatch;
	}

	void setScheduleBatch(ScheduleBatch<E> scheduleBatch) {
		this.scheduleBatch = scheduleBatch;
	}

	ScheduleContext<E> getScheduleContext() {
		return scheduleContext;
	}

	void setScheduleContext(ScheduleContext<E> scheduleContext) {
		this.scheduleContext = scheduleContext;
		this.logger = scheduleContext.getLogger();
	}

	public String getScheduleName(){
		if(scheduleContext != null){
			return scheduleContext.getScheduleName();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <V> V getConfig(String key){
		if(scheduleContext != null){
			return (V)scheduleContext.getScheduleConfig().get(key);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final boolean equals(Object obj) {
		if(!(obj instanceof Task)){
			return false;
		}
		Task<E> task = (Task<E>)obj;
		return this.id.equals(task.id);
	}

	@Override
	public final int hashCode() {
		return this.id.hashCode();
	}
}