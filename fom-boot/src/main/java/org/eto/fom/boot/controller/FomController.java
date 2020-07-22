package org.eto.fom.boot.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.eto.fom.context.core.ContextHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author shanhm
 *
 */
@RestController
public class FomController {
	
	private static final int OPTION_STARTUP = 1;
	
	private static final int OPTION_SHUTDOWN = 2;
	
	private static final int OPTION_EXECNOW = 3;

	@RequestMapping("/list")
	@ResponseBody
	public Map<String, Object> list() throws Exception{
		List<Map<String, String>> list = ContextHelper.list();
		Map<String, Object> map = new HashMap<>();
		map.put("data", list);
		map.put("length", list.size());
		map.put("recordsTotal", list.size());
		map.put("recordsFiltered", list.size());
		return map;
	}

	@RequestMapping("/save")
	@ResponseBody
	public Map<String,Object> save(String name, String data) throws Exception{ 
		return ContextHelper.save(name, data);
	}

	@RequestMapping("/operation")
	@ResponseBody
	public Map<String,Object> operation(String name, int opid) throws Exception{ 
		switch(opid){
		case OPTION_STARTUP: return ContextHelper.startup(name);
		case OPTION_SHUTDOWN: return ContextHelper.shutDown(name);
		case OPTION_EXECNOW: return ContextHelper.execNow(name);
		default : 
			Map<String,Object> map = new HashMap<>();
			map.put("result", false);
			map.put("msg", "unsupported operation.");
			return map;
		}
	}

	@RequestMapping("/state")
	@ResponseBody
	public Map<String,Object> state(String name) throws Exception{ 
		return ContextHelper.state(name);
	}

	@RequestMapping("/log")
	public String log(String name, String level) throws Exception{ 
		ContextHelper.changeLogLevel(name, level); 
		return "success";
	}

	@RequestMapping("/create")
	@ResponseBody
	public Map<String,Object> create(String json) throws Exception{ 
		return ContextHelper.create(json);
	}

	@RequestMapping("/listOtherLogs")
	@ResponseBody
	public Map<String, String> listOtherLogs() throws Exception {
		return ContextHelper.listOtherLogs();
	}

	@RequestMapping("/queryLevel")
	@ResponseBody
	public Map<String, String> queryLevel(String logger) {
		Map<String, String> map = new HashMap<>();
		map.put("level", ContextHelper.queryLogLevel(logger));
		return map;
	}

	@RequestMapping("/saveLevel")
	public String saveLevel(String logger, String level) {
		ContextHelper.saveLogLevel(logger, level);
		return "success";
	}

	@RequestMapping("/successdetail")
	@ResponseBody
	public Map<String,Object> successDetail(String name) throws Exception { 
		return ContextHelper.successDetail(name);
	}

	@RequestMapping("/faileddetail")
	@ResponseBody
	public Map<String,Object> failedDetail(String name) throws Exception { 
		return ContextHelper.failedDetail(name);
	}
	
	@RequestMapping("/executedetail")
	@ResponseBody
	public Map<String,String> executedetail(String name) throws Exception { 
		return  ContextHelper.getLastExceptions(name);
	}

	@RequestMapping("/activedetail")
	@ResponseBody
	public Map<String,Object> activeDetail(String name) throws Exception{ 
		return ContextHelper.activeDetail(name);
	}

	@RequestMapping("/waitingdetail")
	@ResponseBody
	public Map<String,Object> waitingdetail(String name) throws Exception { 
		return ContextHelper.waitingdetail(name);
	}

	@RequestMapping("/saveCostLevel")
	@ResponseBody
	public Map<String,Object> saveCostLevel(String name, String levelStr, String saveDay, String date) throws Exception { 
		return ContextHelper.saveCostLevel(name, levelStr, saveDay, date);
	}

	@RequestMapping("/changeDate")
	@ResponseBody
	public Map<String,Object> changeDate(String name, String date) throws Exception { 
		return ContextHelper.changeDate(name, date);
	}

	@RequestMapping("/dataDownload")
	@ResponseBody
	public Map<String,Object> dataDownload(String name, HttpServletResponse resp) throws Exception{ 
		String json = ContextHelper.dataDownload(name);
		resp.reset();
		resp.setContentType("application/octet-stream;charset=UTF-8");
		resp.addHeader("Content-Disposition", "attachment;filename=\"" + name + "." + System.currentTimeMillis() +".json\"");
		PrintWriter write = resp.getWriter();
		write.write(json);
		write.flush();
		return null;
	}

}
