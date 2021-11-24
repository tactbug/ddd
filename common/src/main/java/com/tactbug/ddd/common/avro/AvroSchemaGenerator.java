package com.tactbug.ddd.common.avro;

import com.tactbug.ddd.common.avro.schema.CategoryCreatedSchema;

import java.io.IOException;


public class AvroSchemaGenerator {
    public static void main(String[] args) throws IOException {
        CategoryCreatedSchema.generateSchema();
    }
}
