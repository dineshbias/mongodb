/**
 * 
 */
package com.sample.db.mongodb.SampleMongoDB;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.sample.template.freemarker.HelloWorldFreemarkerStyle;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * @author dinesh.joshi
 * Class contains samples for understanding Spark and freemarker.
 */
public class HelloWorldSparkStyle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("Default Port : 4567");
		Configuration configuration = setUPFreeMarker();
		defaultHandlerSetup(configuration);
		testHandlerSetup(configuration);
		pathParamHandlerSetup(configuration);
		fruitHandlerSetup(configuration);
	}

	public static Configuration setUPFreeMarker() {
		Configuration config = new Configuration();
		config.setClassForTemplateLoading(HelloWorldFreemarkerStyle.class, "/");
		return config;
	}

	public static void defaultHandlerSetup(Configuration configuration) {
		Spark.get("/", new Route() {

			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				System.out.println("handle GET");
				// return "Hello World from Spark.";

				StringWriter writer = new StringWriter();
				try {
					Template helloTemplate = configuration.getTemplate("hello.ftl");

					Map<String, Object> helloMap = new HashMap<>();
					helloMap.put("name", "Freemarker");
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
				return writer;
			}
		});
	}

	public static void testHandlerSetup(Configuration configuration) {

		Spark.get("/test", new Route() {
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				System.out.println("handle GET");
				return "Hello World TEST Spark.";
			}
		});
	}

	public static void pathParamHandlerSetup(Configuration configuration) {
		Spark.get("/echo/:thing", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				System.out.println("handle GET");
				return request.params(":thing");
			}
		});
	}

	public static void fruitHandlerSetup(Configuration configuration) {
		Spark.get("/fruit", new Route() {

			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				System.out.println("handle GET");

				StringWriter writer = new StringWriter();
				try {
					Template fruitPickerTemplate = configuration.getTemplate("fruitPicker.ftl");

					Map<String, Object> fruits = new HashMap<>();
					fruits.put("fruits", Arrays.asList("Banana", "Apple", "Papaya", "Orange"));
					fruitPickerTemplate.process(fruits, writer);
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
				return writer;
			}
		});

		Spark.post("/fruit/favorite", new Route() {

			@Override
			public Object handle(Request request, Response response) throws Exception {
				final String selectedFruit = request.queryParams("fruit");

				if (selectedFruit == null || selectedFruit.isEmpty()) {
					return "Why don't you pick one..";
				} else {
					return "You selected " + selectedFruit;
				}
			}
		});
	}

}
