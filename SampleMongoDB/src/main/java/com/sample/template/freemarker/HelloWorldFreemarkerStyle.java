/**
 * 
 */
package com.sample.template.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * @author dinesh.joshi
 *
 */
public class HelloWorldFreemarkerStyle {

	public static void main(String[] args) {

		Configuration config = new Configuration();
		config.setClassForTemplateLoading(HelloWorldFreemarkerStyle.class, "/");

		try {
			Template helloTemplate = config.getTemplate("hello.ftl");

			Map<String, Object> helloMap = new HashMap<>();
			helloMap.put("name", "Freemarker");

			StringWriter writer = new StringWriter();
			helloTemplate.process(helloMap, writer);

			System.out.println(writer);
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}

	}

}
