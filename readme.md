# Mongi Mongo ORM

![alt tag](https://circleci.com/gh/stump201/mongiORM.svg?style=shield&circle-token=:circle-token)

### Version 0.1
#### VERY EARLY DEVELOPMENT
## What is Mongi
Mongi is a simple ORM solution for MongoDB to hep overcome some of the limitations relating to Monogo within a java based envioment. Java doesnt undertand json in any useful way and as such most frameworks marshall json into POJO objects and Vice versa. 

Another method is to use a JSON builder such as in vert.x, But this approcach is both tricky and ugly.

Build for vert.x 3, Using both standard asyncronous callbacks and observables. Future development will be more focused towards obvervables.

## Pre-reqs

- Vertx 3
- Google Gson
- RxJava 
- Java 8 > Lambda

## TODO

- Properly implement observables and RX-Java
- Implement proper Iquery interface


```xml
<dependency>
     <groupId>org.fandanzle.mongi</groupId>
     <artifactId>mongi-core</artifactId>
     <version>0.1-BETA</version>
 </dependency>

```

## How to use Mongi

At its core Mongi is a simple ORM that runs off annotations wrapped around a class and its enclosed parameters. Given my experience with Mongo over the last few months i have grown fond of it but at the same time there are functions of a RDMS that i miss. 

Mongi is my solution to this problem. Built in my spare time as both a research project into Java 8 and mongo.

To use Mongi once you have imported the maven repo you will need to start by annotating your entities with the provided annotations. These annotations will persist across all situations.

@CollectionDefinition 
This annotation is used to declare the name of the collections relating to this entity, logically one entity will generate one collection.

@UniqueIndex
This annotations is used to mark the variables within the entity, At the moment this will only apply a unique index to the entity in question and a index is only one level deep, E.g you cannot apply and index to an array list type. If you apply a unique index to an type of iterable type then an exception will be thrown by Mongi.

Below is a simple example, This example also used Google Gson. This is a typical pattern within the new Identity service and SCAP database.


```Java
@CollectionDefinition(
        collectionName = "COLLECTION_NAME"
)
public class ExampleEntity {

    @Since(1.0)
    @Expose
    @UniqueIndex(indexName = "entity_id_unique_index")
    private String entityId;
    
    @Since(1.0)
    @Expose
    @UniqueIndex(indexName = "entity_name_unique_index")
    private String entityName;
    
}
```

###Mongo and vertx

Currently you can use Mongi with Vert.x 3.0 and the offical Vert.x mongo client
Simply pass in a Vert.x instance along with the mongo config that you wish to use. Please note that with mongo once you have created a Mongo instance via its officall driver all new instances will use the same configuration unless you specify a new group within the config array.

```Java
import org.fandanzle.mongi.MongiVertxTest;
// You will need to use MongiVertx
// Mongo is a really simple ORM that uses annotations and relfections
Mongi mongi = new Mongi(vertx, mongoConfig);
mongi.buildOrmSolution("your.entity.package.path");
```
And your done. If you passed in a valid vert.x instance and config then Mongi will automatically create all of your collections and the indexes for those collections as per your syntax sugar.

### Mongo and Vert.rx  AKA Observables

Im currently moving over all of the Identity service to Observables as opposed to the standard call back approach, As such Mongi is in development with this. Bu the principle will be much the same.%  

## Contacts
alexbrown201@googlemail.com
