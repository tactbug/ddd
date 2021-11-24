package com.tactbug.ddd.common.avro.schema;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;


public class SchemaGenerator {
    public static Schema generateEvent(
            SchemaBuilder.FieldAssembler<org.apache.avro.Schema> builder
    ){
        return builder
                .requiredLong("id")
                .requiredInt("version")
                .requiredString("createTime")
                .requiredString("updateTime")
                .requiredLong("domainId")
                .requiredInt("domainVersion")
                .requiredString("eventType")
                .requiredLong("operator")
                .endRecord();
    }
}
