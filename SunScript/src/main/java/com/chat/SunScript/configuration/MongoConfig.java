package com.chat.SunScript.configuration;

import com.chat.SunScript.entity.Admin;
import com.chat.SunScript.entity.User;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Collation;

import java.util.List;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry()//,
                //CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD))
                //CodecRegistries.fromCodecs(new ZoneDateTimeCodec())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .applyConnectionString(new ConnectionString(mongoUri))
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "chatDB");
    }

    @Bean
    public IndexOperations configureAdminIndexes(MongoTemplate mongoTemplate) {
        return createAdminIndexes(mongoTemplate);
    }
    private IndexOperations createAdminIndexes(MongoTemplate mongoTemplate) {
        IndexOperations indexOperations = mongoTemplate.indexOps(Admin.class);

        boolean indexExists = indexOperations.getIndexInfo().stream()
                        .anyMatch(index -> index.getName().equals("username_1"));

        if (indexExists) {
            indexOperations.dropIndex("username_1");
            System.out.println("Delete index 'username_1'");
        } else {
            System.out.println("Index 'username_1' not exists.");
        }

        indexOperations.ensureIndex(new Index()
                .on("username", Sort.Direction.ASC)
                .unique()
                .collation(Collation.of("en")
                       // .caseLevel(true)
                        //.strength(3)
                        //.numericOrdering(true)
                )
        );

        return indexOperations;
    }

    @Bean
    public IndexOperations configureUserIndexes(MongoTemplate mongoTemplate) {
        return createUserIndexes(mongoTemplate);
    }
    private IndexOperations createUserIndexes(MongoTemplate mongoTemplate) {
        IndexOperations indexOperations = mongoTemplate.indexOps(User.class);

        List<String> fieldsToIndex = List.of("email", "username", "phone", "discriminator");

        fieldsToIndex.forEach(field -> {
            boolean indexExists = indexOperations.getIndexInfo().stream()
                    .anyMatch(index -> index.getName().equals(field+"_1"));

            if (indexExists) {
                indexOperations.dropIndex(field+"_1");
                System.out.println("Deleted index '"+field+"_1"+"' for User");
            } else {
                System.out.println("Index '" + field + "_1' for User does not exist.");
            }
        });

        fieldsToIndex.forEach(field -> {
            Collation collation = Collation.of("en")
                            .strength(Collation.ComparisonLevel.primary());
           indexOperations.ensureIndex(new Index()
                   .on(field, Sort.Direction.ASC)
                   .unique()
                   .collation(collation)
           );
        });

        return indexOperations;
    }

}
