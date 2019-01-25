package com.fom.context.executor.helper;

import java.util.List;

import org.apache.log4j.Logger;

import com.fom.log.LoggerFactory;

/**
 * 
 * @author shanhm
 *
 * @param <V>
 */
public abstract class AbstractImporterHelper<V> implements ImporterHelper<V> {
	
	protected final Logger log;
	
	protected final String name;
	
	public AbstractImporterHelper(String name){
		this.name = name;
		this.log = LoggerFactory.getLogger(name);
	}

	@Override
	public final void batchProcessLineData(List<V> lineDatas, long batchTime) throws Exception {
		if(Thread.interrupted()){
			throw new InterruptedException("interrupted when batchProcessLineData");
		}
		int size = lineDatas.size();
		batchProcessIfNotInterrupted(lineDatas, batchTime);
		log.info("批处理结束[" + size + "],耗时=" + (System.currentTimeMillis() - batchTime) + "ms");
	}
	
	public abstract void batchProcessIfNotInterrupted(List<V> lineDatas, long batchTime) throws Exception;
	
}
