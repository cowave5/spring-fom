package com.fom.context;

/**
 * 
 * Task执行结果
 * 
 * @see Task
 * 
 * @author shanhm
 *
 */
public class Result {

	final String taskId;

	boolean success;

	long createTime;

	long startTime;

	long costTime;

	Throwable throwable;

	Result(String sourceUri) {
		this.taskId = sourceUri;
	}

	/**
	 * 获取任务id
	 * @return id
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * 任务是否成功
	 * @return isSuccess
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * 获取任务创建时间
	 * @return createTime
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * 获取任务开始时间
	 * @return startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * 获取任务耗时
	 * @return costTime
	 */
	public long getCostTime() {
		return costTime;
	}

	/**
	 * 获取任务异常信息
	 * @return throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}

}
