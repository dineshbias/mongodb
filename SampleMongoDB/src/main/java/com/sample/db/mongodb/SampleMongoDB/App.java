package com.sample.db.mongodb.SampleMongoDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriter;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import java.io.StringWriter;
import java.lang.Iterable.*;

/**
 * Hello world!
 * This class contains sample method to understand API's supported by mongodb driver. 
 */
public class App {

	private static void connectToDB() {

		// Options can be set based on requirements.
		// MongoClientOptions options =
		// MongoClientOptions.builder().minConnectionsPerHost(500).build();
		// MongoClient mongo = new MongoClient(new ServerAddress(),options);

		try (MongoClient mongo = new MongoClient(Constants.MONGODB_URL_LOCAL, Constants.MONGODB_URL_PORT);) {

			// Creating Credentials
			MongoCredential credential = MongoCredential.createCredential("NotRequiredForLocalSetup",
					"NotRequiredForLocalSetup", "password".toCharArray());
			System.out.println("Credentials ::" + credential);
			System.out.println("Connected to the database successfully");

			// Accessing the database. MongoDatabase is immutable.
			MongoDatabase database = mongo.getDatabase("firstDB");

			// Dropping a collection
			MongoCollection<Document> collection = getCollection(database, "books");
			System.out.println("------Documents in Collection ----------- " + collection.count());
			collection.drop();

			System.out.println("------Collection Droped----------- " + "books");

			// Print Documents
			collection = listDocumentsInCollection(database, "books");

			insertDocumentsInCollection(collection, "Harry", 140);

			collection = listDocumentsInCollection(database, "books");

			Document d1 = getSampleDocument("Ramesh");
			Document d2 = getSampleDocument("Mukesh");
			Document d3 = getSampleDocument("Suresh");
			List<Document> docList = new ArrayList<>();
			docList.add(d1);
			docList.add(d2);
			docList.add(d3);

			insertManyDocs(collection, docList);

			// MongoCollection<BsonDocument> collection2 =
			// listBsonDocumentsIncollection(database, "books");

			find(collection);

			findUsingCriteria(collection);

			testProjection(collection);

			collection.drop();
			System.out.println("------Collection Droped----------- " + "books");

			insertMany(collection);
			testSort(collection);
			testSkip(collection);
			testLimit(collection);

			testReplaceAndUpdate(collection);
			
			testDelete(collection);
			
			
			
			
			
		}

	}

	private static MongoCollection<Document> getCollection(MongoDatabase database, String name) {
		return database.getCollection(name);
	}

	/*
	 * Listing documents present in a collection.
	 */
	private static MongoCollection<Document> listDocumentsInCollection(MongoDatabase database, String name) {
		MongoCollection<Document> collection = database.getCollection(name);
		FindIterable<Document> it = collection.find();

		Block<Document> printer = System.out::println;
		it.forEach(printer);

		System.out.println("Count of Collections: " + collection.count());
		return collection;
	}

	/*
	 * Listing documents present in a collection.
	 */
	private static MongoCollection<BsonDocument> listBsonDocumentsIncollection(MongoDatabase database,
			String collectonName) {
		MongoCollection<BsonDocument> collection = database.getCollection(collectonName, BsonDocument.class);

		FindIterable<BsonDocument> it = collection.find();
		Block<BsonDocument> printer = System.out::println;
		it.forEach(printer);
		System.out.println("Count of collections: " + collection.count());
		return collection;
	}

	public static Document getSampleDocument(String name) {
		Document document = new Document().append("str", "Hello " + name).append("int", 42).append("l", 1L)
				.append("date", new Date()).append("null", null).append("embeddedDoc", new Document("x", 0))
				.append("list", Arrays.asList(1, 2, 3));

		printJSON(document);

		return document;
	}

	private static void insertDocumentsInCollection(MongoCollection<Document> collection, String name, int likes) {
		Document document = new Document("title", "MicroServices Overview").append("Date", new Date())
				.append("list", Arrays.asList(1L, 2L)).append("embededDoc", new Document("name", name))
				.append("description", "Microservices black book").append("by", "S R Ravi").append("likes", likes);

		collection.insertOne(document);
		System.out.println("----------------Document Inserted--------------------");
	}

	public static void insertManyDocs(MongoCollection<Document> collection, List<? extends Document> documents) {

		if (null != documents && !documents.isEmpty()) {
			collection.insertMany(documents);
			System.out.println("----------------Documents Inserted--------------------");
		}

	}

	public static void find(MongoCollection<Document> collection) {

		Document firstDoc = collection.find().first();
		printJSON(firstDoc);
		System.out.println("-------First Document Printed--------");

		List<Document> documents = collection.find().into(new ArrayList<Document>());
		for (Document document : documents)
			printJSON(document);
		System.out.println("-------All Documents Printed using forEach--------" + documents.size());

		try (MongoCursor<Document> mCursor = collection
				.find(new Document().append("description", "Microservices black book")).iterator();) {
			while (mCursor.hasNext()) {
				printJSON(mCursor.next());
			}
		}

		System.out.println("-------All Documents Printed using MongoCursor--------");
	}

	public static void findUsingCriteria(MongoCollection<Document> collection) {

		System.out.println("Collection.count without filter..." + collection.count());

		Document document = new Document().append("description", "Microservices black book");
		List<Document> docs = collection.find(document).into(new ArrayList<Document>());
		System.out.println("Documents found based on criteria.." + docs.size());
		for (Document doc : docs)
			printJSON(doc);
		System.out.println("-------All Documents returned in description search criteria printed--------");

		Document filter = new Document().append("int", new Document("$lte", 100));
		docs = collection.find(filter).into(new ArrayList<Document>());
		System.out.println("Documents found based on criteria.." + docs.size());
		for (Document doc : docs)
			printJSON(doc);
		System.out.println("-------All Documents returned in integer search criteria printed--------");

		System.out.println("Collection.count with filter..." + collection.count(filter));

		// Builder for query filters.
		Bson fil = Filters.and(Filters.lt("int", 50), Filters.gt("int", 40));
		docs = collection.find(fil).into(new ArrayList<Document>());
		System.out.println("Documents found based on AND filter.." + docs.size());
		for (Document doc : docs)
			printJSON(doc);
		System.out.println("-------All Documents returned in integer search criteria printed--------");

	}

	public static void testProjection(MongoCollection<Document> collection) {

		Document projection = new Document().append("_id", 0).append("int", 0).append("null", 0);
		List<Document> docs = collection.find().projection(projection).into(new ArrayList<>());
		for (Document doc : docs)
			printJSON(doc);
		System.out.println("-------All Documents printed with projection using raw documents--------");

		Bson projection1 = Projections.fields(Projections.excludeId(), Projections.include("title"),
				Projections.include("date"));
		List<Document> documents = collection.find().projection(projection1).into(new ArrayList<>());
		for (Document doc : documents)
			printJSON(doc);
		System.out.println("-------All Documents printed with projection using static Projections method--------");
	}

	private static void insertMany(MongoCollection<Document> collection) {

		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 5; j++) {

				Document document = new Document("i", i).append("j", j);
				collection.insertOne(document);

			}
		}

		Document document = new Document("i", 0).append("j", 1);
		collection.insertOne(document);

		document = new Document("i", 1).append("j", 0);
		collection.insertOne(document);

		document = new Document("i", 0).append("j", 0);
		collection.insertOne(document);

		System.out.println("-------All Documents inserted-------- " + collection.count());
	}

	private static void testSort(MongoCollection<Document> collection) {

		List<Document> documents = collection.find().projection(Projections.excludeId())
				.sort(new Document("i", 1).append("j", 1)).into(new ArrayList<>());
		printJSON(documents);
		System.out.println("-------All Documents are sorted i asc j asc--------");

		documents = collection.find().projection(Projections.excludeId()).sort(new Document("i", -1).append("j", -1))
				.into(new ArrayList<>());
		printJSON(documents);
		System.out.println("-------All Documents are sorted i desc j desc --------");

		documents = collection.find().projection(Projections.excludeId()).sort(new Document("i", 1).append("j", -1))
				.into(new ArrayList<>());
		printJSON(documents);
		System.out.println("-------All Documents are sorted i asc j desc --------");

		documents = collection.find().projection(Projections.excludeId()).sort(new Document("i", -1).append("j", 1))
				.into(new ArrayList<>());
		printJSON(documents);
		System.out.println("-------All Documents are sorted i desc j asc--------");

		// Sorting using static method in Sorts builder class.
		Bson sortingOrder = Sorts.orderBy(Sorts.ascending("i"), Sorts.ascending("j"));
		documents = collection.find().projection(Projections.excludeId()).sort(sortingOrder).into(new ArrayList<>());
		printJSON(documents);
		System.out.println("-------All Documents are sorted by using Sorts static method--------");
	}

	private static void testSkip(MongoCollection<Document> collection) {

		List<Document> documents = collection.find().projection(Projections.excludeId())
				.sort(new Document("i", 1).append("j", 1)).skip(3).into(new ArrayList<>());
		printJSON(documents);
		System.out.println("-------All Documents are sorted i asc j asc. 3 Documents are skipped--------");
	}

	private static void testLimit(MongoCollection<Document> collection) {
		List<Document> documents = collection.find().projection(Projections.excludeId())
				.sort(new Document("i", 1).append("j", 1)).skip(3).limit(10).into(new ArrayList<>());
		printJSON(documents);
		System.out.println(
				"-------All Documents are sorted i asc j asc. 3 Documents are skipped and documents are limited to 10--------");
	}

	private static void testReplaceAndUpdate(MongoCollection<Document> collection) {

		// replace existing document.
		collection.replaceOne(Filters.and(Filters.eq("i", 0), Filters.eq("j", 0)),
				new Document("name", "DJ").append("age", 32));

		// updating document using $set operator
		collection.updateOne(Filters.and(Filters.eq("i", 1), Filters.eq("j", 1)),
				new Document("$set", new Document("name", "DJ").append("age", 32)));

		// updating exiting document using builder class
		collection.updateOne(Filters.and(Filters.eq("i", 2), Filters.eq("j", 2)),
				Updates.combine(Updates.set("name", "DJ"), Updates.set("age", 32)));

		// Upsert
		collection.updateOne( Filters.and(Filters.eq("i", 100), Filters.eq("j", 100)),
				Updates.combine(Updates.set("name", "DJ"), Updates.set("age", 32)), new UpdateOptions().upsert(true) ) ;

		List<Document> documents = collection.find().projection(Projections.excludeId())
				.sort(new Document("i", 1).append("j", 1)).into(new ArrayList<>());
		printJSON(documents);
		System.out.println("-------Printed replaced, updated and upsert documents, sorted i asc j asc--------");

	}
	
	
	private static void testDelete(MongoCollection<Document> collection){
		System.out.println("Before Deleting" + collection.count());
		collection.deleteMany(Filters.eq("i",0));
		collection.deleteMany(Filters.eq("i",1));
		collection.deleteMany(Filters.eq("i",2));
		collection.deleteMany(Filters.eq("i",3));
		System.out.println("After Deleting"+collection.count());
		printJSON(collection.find().into(new ArrayList<Document>()));
		System.out.println("-------Deleted documents--------");
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

	public static void main(String[] args) {
		System.out.println("Hello World!");
		connectToDB();
	}
}
