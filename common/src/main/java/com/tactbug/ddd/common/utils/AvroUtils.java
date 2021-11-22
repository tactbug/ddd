package com.tactbug.ddd.common.utils;

import com.tactbug.ddd.common.avro.EventTypeEnum;
import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericRecord;

import java.io.File;
import java.io.FileNotFoundException;

public class AvroUtils {

    private String IDLPath;

    private static final String ROOT_PATH = "common/src/main/resources/avro/";

    public static AvroUtils generate(EventTypeEnum eventTypeEnum) throws FileNotFoundException {
        AvroUtils avroUtils = new AvroUtils();
        avroUtils.setIDLPath(ROOT_PATH + eventTypeEnum.getAvroPath());
        if (!new File(avroUtils.getIDLPath()).exists()){
            throw new FileNotFoundException("avro模式文件[" + avroUtils.getIDLPath() + "]不存在");
        }
        return avroUtils;
    }

    public DataFileWriter<GenericRecord> write(){
        Schema schema = getSchema();

    }

    private GenericRecord genericRecord(Event<? extends BaseDomain> event){

    }

    private Schema getSchema(){
        return new Schema.Parser().parse(IDLPath);
    }

    public String getIDLPath() {
        return IDLPath;
    }

    public void setIDLPath(String IDLPath) {
        this.IDLPath = IDLPath;
    }
}
