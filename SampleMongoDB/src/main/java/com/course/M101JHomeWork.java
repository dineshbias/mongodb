/**
 * 
 */
package com.course;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonWriter;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;

/**
 * @author dinesh.joshi
 *
 */
public class M101JHomeWork {

	private static final MongoClient client;
	private static final MongoDatabase mongod;
	private static final MongoCollection<Document> grades;

	static {
		client = new MongoClient();
		System.out.println("Client Loaded");
		mongod = client.getDatabase("students");
		System.out.println("Database loaded");
		grades = mongod.getCollection("grades");
		System.out.println("grades loaded");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			List<Document> documents = grades.find().into(new ArrayList<Document>());
			System.out.println("Total Documents in collection : " + grades.count());
			System.out.println("Total Documents retrieved : " + documents.size());
			System.out.println("-------------\n");
			documents = grades.find(Filters.eq("type", "homework")).sort(Sorts.ascending("student_id", "type", "score"))
					.into(new ArrayList<Document>());

			printJSON(documents);

			System.out.println("Total Documents in collection : " + grades.count());
			System.out.println("Total Documents retrieved : " + documents.size());

			System.out.println("Removing documents with lowest grade for homework..");

			List<String> studentIds = new ArrayList<>();
			for (Document document : documents) {
				String studentId = String.valueOf(document.get("student_id"));
				if (!studentIds.contains(studentId)) {
					studentIds.add(studentId);
					String docId = String.valueOf(document.get("_id"));

					System.out.print(" Removing grade ");
					printJSON(document);
					List<Document> gLTemp = grades.find(Filters.eq("_id", docId)).into(new ArrayList<Document>());
					DeleteResult dResult = grades.deleteOne(document);
					System.out.print("Deleted Count :  " + dResult.getDeletedCount());

				}
			}

			System.out.println("Total Documents in collection : " + grades.count());
			System.out.println("Total Documents retrieved : " + documents.size());
			printJSON(documents);
		} finally {
			client.close();
			System.out.println("Close connection...");
		}

		System.out.println("Exiting main...");
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
