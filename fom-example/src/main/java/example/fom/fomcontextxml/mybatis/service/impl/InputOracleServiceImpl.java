package example.fom.fomcontextxml.mybatis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.fom.fomcontextxml.mybatis.bean.ExampleBean;
import example.fom.fomcontextxml.mybatis.mapper.InputOracleMapper;
import example.fom.fomcontextxml.mybatis.service.InputOracleService;

/**
 * 
 * @author shanhm
 *
 */
@Service
public class InputOracleServiceImpl implements InputOracleService {
	
	@Autowired
	private InputOracleMapper mapper;

	@Override 
	public void input(List<ExampleBean> list) {
		mapper.input(list);
	}

}