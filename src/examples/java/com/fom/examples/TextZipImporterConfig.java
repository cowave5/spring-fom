package com.fom.examples;

import com.fom.context.Config;

/**
 * 
 * @author shanhm
 *
 */
public class TextZipImporterConfig extends Config {
	
	private int batch;
	
	private String pattern;

	protected TextZipImporterConfig(String name) {
		super(name);
	}
	
	@Override
	protected void loadExtends() throws Exception {
		batch = loadExtends("importer.batch", 5000, 1, 50000);
		pattern = loadExtends("zip.entryPattern", "");
	}

	@Override
	public String getType() {
		return TYPE_IMPORTER;
	}
	
	public int getBatch() {
		return batch;
	}
	
	public String getEntryPattern(){
		return pattern;
	}
	
}
