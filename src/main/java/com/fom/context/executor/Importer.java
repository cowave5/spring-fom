package com.fom.context.executor;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.fom.context.exception.WarnException;
import com.fom.context.executor.helper.importer.ImporterHelper;
import com.fom.context.executor.reader.Reader;
import com.fom.log.LoggerFactory;
import com.fom.util.IoUtil;

/**
 * 
 * @author shanhm
 * @date 2018年12月23日
 *
 */
public class Importer implements Executor {

	protected final Logger log;

	protected final String name;

	protected final int batch;
	
	protected final String sourceUri;

	protected final String sourceName;

	protected final long sourceSize;

	protected final File logFile;

	@SuppressWarnings("rawtypes")
	protected final ImporterHelper helper;

	@SuppressWarnings("rawtypes")
	public Importer(String name, String sourceUri, IImporterConfig config, ImporterHelper helper){
		this.name = name;
		this.log = LoggerFactory.getLogger(name);

		this.batch = config.getBatch();
		this.helper = helper;

		this.sourceUri = sourceUri;
		this.sourceName = helper.getFileName(sourceUri);
		this.sourceSize = helper.getFileSize(sourceUri);
		this.logFile = new File(System.getProperty("import.progress") 
				+ File.separator + name + File.separator + sourceName + ".log");
	}

	@Override
	public final void exec() throws Exception {
		long sTime = System.currentTimeMillis();
		int lineIndex = 0;
		if(!logFile.exists()){ //TODO 创建父目录
			if(!logFile.createNewFile()){
				throw new WarnException("创建日志文件失败.");
			}
		}else{
			log.warn("继续处理任务遗留文件."); 
			List<String> lines = FileUtils.readLines(logFile);
			try{
				lineIndex = Integer.valueOf(lines.get(0));
				log.info("获取文件处理进度:" + lineIndex); 
			}catch(Exception e){
				log.warn("获取文件处理进度失败,将从第0行开始处理.");
			}
		}

		read(lineIndex);
		log.info("处理文件结束(" 
				+ new DecimalFormat("#.##").format(sourceSize) + "KB),耗时=" + (System.currentTimeMillis() - sTime) + "ms");
		if(!helper.delete(sourceUri)){ 
			throw new WarnException("删除文件失败."); 
		}
		if(!logFile.delete()){
			throw new WarnException("删除日志失败."); 
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void read(int StartLine) throws Exception {
		int lineIndex = 0;
		String line = "";
		Reader reader = null;
		try{
			reader = helper.getReader(sourceUri);
			List lineDatas = new LinkedList<>(); 
			long batchTime = System.currentTimeMillis();
			while ((line = reader.readLine()) != null) {
				lineIndex++;
				if(lineIndex <= StartLine){
					continue;
				}
				if(batch > 0 && lineDatas.size() >= batch){
					helper.batchProcessLineData(lineDatas, batchTime); 
					logProcess(sourceUri, lineIndex);
					lineDatas.clear();
					batchTime = System.currentTimeMillis();
				}
				helper.praseLineData(lineDatas, line, batchTime);
			}
			if(!lineDatas.isEmpty()){
				helper.batchProcessLineData(lineDatas, batchTime); 
			}
			logProcess(sourceUri, lineIndex);
		}finally{
			IoUtil.close(reader);
		}
	}

	private void logProcess(String uri, int lineIndex) throws IOException{ 
		log.info("已读取行数:" + lineIndex);
		FileUtils.writeStringToFile(logFile, String.valueOf(lineIndex), false);
	}
}
