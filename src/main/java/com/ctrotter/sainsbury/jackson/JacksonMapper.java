package com.ctrotter.sainsbury.jackson;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
/**
 * Jackson Object Mapper Bean 
 * @author CTrotter
 */
public class JacksonMapper extends ObjectMapper {

	private static final long serialVersionUID = -258725290261003461L;

	public JacksonMapper() {
		super();
		enable(SerializationFeature.INDENT_OUTPUT);
	}
}