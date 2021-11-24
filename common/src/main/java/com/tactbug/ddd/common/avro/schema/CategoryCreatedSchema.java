package com.tactbug.ddd.common.avro.schema;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CategoryCreatedSchema {

    private static final String schemaPath = "common/src/main/resources/avro/categoryCreatedSchema.avsc";
    private static final String dataSchemaPath = "common/src/main/resources/avro/categoryCreatedDataSchema.avsc";

    public static void generateSchema() throws IOException {
        generateDataSchemaFile();
        generateSchemaFile();
    }

    private static SchemaBuilder.FieldAssembler<Schema> schemaBuilder(){
        return SchemaBuilder
                .record("CategoryCreatedAvro")
                .namespace("com.tactbug.ddd.common.avro.product")
                .fields()
                .name("CategoryCreatedDataAvro")
                .type(categoryCreatedDate())
                .noDefault();
    }

    private static Schema categoryCreatedDate(){
        return SchemaBuilder
                .record("CategoryCreatedDataAvro")
                .namespace("com.tactbug.ddd.common.avro.product")
                .fields()
                    .requiredString("name")
                    .requiredString("remark")
                    .requiredLong("parentId")
                .endRecord();
    }

    private static void generateDataSchemaFile() throws IOException {
        String json = categoryCreatedDate().toString(true);
        File dataFile = new File(dataSchemaPath);
        write(dataFile, json);
    }

    private static void generateSchemaFile() throws IOException {
        String json = SchemaGenerator.generateEvent(schemaBuilder()).toString(true);
        File file = new File(schemaPath);
        write(file, json);
    }

    private static void write(File file, String content) throws IOException {
        if (file.exists()){
            file.delete();
        }
        if (file.createNewFile()){
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        }else {
            System.out.println("文件[" + file.getAbsolutePath() + "]创建失败");
        }
    }

}
