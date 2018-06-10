/**
 * 
 */
package com.sample.db.mongodb.SampleMongoDB;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonWriter;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
 *
 */
public class AppMongoSparkFreemarker {
	
	private static final MongoClient client;
	private static final MongoDatabase mongoD;
	private static final MongoCollection<Document> greetingCollection;
	private static final Configuration freeMarkerConfiguration;
	
	static{
		
		client = new MongoClient();
		
		//DROP Database
		// mongoD = client.getDatabase("firstDB");
		//mongoD.drop();
		
		 mongoD = client.getDatabase("firstDB");
		 
		 greetingCollection = mongoD.getCollection("greeting");
		 
		 //Drop Collection
		 greetingCollection.drop();
		 greetingCollection.insertOne(new Document("name","Dinesh Joshi"));
		 
		 freeMarkerConfiguration = setUPFreeMarker();
		 sparkHandlerSetup(freeMarkerConfiguration);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Running Main..");
	}

	
	private static void sparkHandlerSetup(Configuration configuration) {

		Spark.get("/home", new Route() {
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				System.out.println("handle GET");
				// return "Hello World from Spark.";

				StringWriter writer = new StringWriter();
				try {
					Template helloTemplate = configuration.getTemplate("hello.ftl");

					Map<String, Object> helloMap = new HashMap<>();
					
					Document document = greetingCollection.find().first();
					
					printJSON(document);
					
					helloMap.put("name", document.get("name"));
					
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
		System.out.println("sparkHandlerSetup Done..");
	}
	
	private static Configuration setUPFreeMarker() {
		Configuration config = new Configuration();
		config.setClassForTemplateLoading(HelloWorldFreemarkerStyle.class, "/");
		System.out.println("setUPFreeMarker Done..");
		return config;
	}
	
	public static void printJSON(List<Document> documents) {
		for (Document document : documents)
			printJSON(document);
	}

	public static void printJSON(Document document) {
		JsonWriter jsonWriter = new JsonWriter(new StringWriter());

		new DocumentCodec().encode(jsonWriter, document,
				EncoderContext.builder().isEncodingCollectibleDocument(true).build());

		System.out.println(jsonWriter.getWriter());
	}
}
