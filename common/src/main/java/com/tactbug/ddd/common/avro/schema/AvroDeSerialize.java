package com.tactbug.ddd.common.avro.schema;

import com.tactbug.ddd.common.avro.product.CategoryCreatedAvro;
import com.tactbug.ddd.common.exceptions.TactException;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.IOException;
import java.util.Arrays;

public class AvroDeSerialize {
    public static CategoryCreatedAvro categoryCreatedAvro(String data){
        DatumReader<CategoryCreatedAvro> reader
                = new SpecificDatumReader<>(CategoryCreatedAvro.class);
        Decoder decoder;
        try {
            decoder = DecoderFactory.get().jsonDecoder(
                    CategoryCreatedAvro.getClassSchema(), data);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw TactException.serializeOperateError("[" + data + "]反序列化失败", e);
        }
    }
}
