package com.tactbug.ddd.product.domain.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.avro.product.CategoryCreatedAvro;
import com.tactbug.ddd.common.avro.product.CategoryCreatedDataAvro;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.common.exceptions.TactException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.CategoryEvent;
import com.tactbug.ddd.product.query.vo.CategoryVo;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import javax.persistence.Entity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/3 21:39
 */
@Entity
public class CategoryCreated extends CategoryEvent implements Serializable {

    public CategoryCreated(Long eventId, Category category, Long operator) {
        super(eventId, category, operator);
        assembleData(category);
        checkData();
    }

    public CategoryCreated() {
        super();
    }

    public void replay(Category category){
        super.replay(category);
        try {
            HashMap<String, Object> dataMap = SerializeUtil.jsonToObject(data, new TypeReference<>() {
            });
            category.setName(dataMap.get("name").toString());
            category.setRemark(dataMap.get("remark").toString());
            category.setParentId(Long.valueOf(dataMap.get("parentId").toString()));
            category.setDeleted(false);
        } catch (Exception e) {
            throw TactException.replayError("[" + category.getId() + "]新增数据异常", e);
        }
    }

    public void doAccept(CategoryVo categoryVo){
        super.doAccept(categoryVo);
        try {
            HashMap<String, Object> dataMap = SerializeUtil.jsonToObject(data, new TypeReference<>() {
            });
            categoryVo.setName(dataMap.get("name").toString());
            categoryVo.setRemark(dataMap.get("remark").toString());
            categoryVo.setDeleted(false);
        } catch (Exception e) {
            throw TactException.replayError("[" + categoryVo.getId() + "]视图基础信息构建异常", e);
        }
    }

    public byte[] serialize(){
        CategoryCreatedAvro avro = avro();
        DatumWriter<CategoryCreatedAvro> writer = new SpecificDatumWriter<>(
                CategoryCreatedAvro.class);
        byte[] data;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Encoder jsonEncoder;
        try {
            jsonEncoder = EncoderFactory.get().jsonEncoder(
                    CategoryCreatedAvro.getClassSchema(), stream);
            writer.write(avro, jsonEncoder);
            jsonEncoder.flush();
            data = stream.toByteArray();
        } catch (IOException e) {
            throw TactException.serializeOperateError("[" + avro.toString() + "]序列化失败", e);
        }
        return data;
    }

    private CategoryCreatedAvro avro(){
        Map<String, Object> map = dataMap();
        CategoryCreatedDataAvro data = CategoryCreatedDataAvro.newBuilder()
                .setName(map.get("name").toString())
                .setRemark(map.get("remark").toString())
                .setParentId(Long.parseLong(map.get("parentId").toString()))
                .build();
        return CategoryCreatedAvro.newBuilder()
                .setId(id)
                .setVersion(version)
                .setCreateTime(createTime.format(DateTimeFormatter.ISO_DATE_TIME))
                .setUpdateTime(updateTime.format(DateTimeFormatter.ISO_DATE_TIME))
                .setDomainId(domainId)
                .setDomainVersion(domainVersion)
                .setCategoryCreatedDataAvro(data)
                .setEventType(eventType.toString())
                .setOperator(operator)
                .build();
    }

    private void assembleData(Category category) {
        try {
            data = SerializeUtil.mapToString(data(category));
        } catch (JsonProcessingException e) {
            throw TactException.serializeOperateError(data(category).toString(), e);
        }
    }

    private Map<String, Object> data(Category category){
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("name", category.getName());
        map.put("remark", category.getRemark());
        map.put("parentId", category.getParentId());
        return map;
    }

    private Map<String, Object> dataMap(){
        try {
            return SerializeUtil.<HashMap<String, Object>>jsonToObject(data, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw TactException.serializeOperateError("[" + data + "]反序列化异常", e);
        }
    }

    private void checkData(){
        super.check();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(this.data, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw TactException.serializeOperateError(this.data, e);
        }
        if (Objects.isNull(data.get("id")) || !SerializeUtil.isNumber(data.get("id").toString())){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合ID状态异常");
        }
        if (Objects.isNull(data.get("name")) || data.get("name").toString().isBlank()){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合名称不能为空");
        }
        if (Objects.isNull(data.get("remark"))){
            data.put("remark", "");
        }
        if (Objects.isNull(data.get("parentId")) || !SerializeUtil.isNumber(data.get("parentId").toString())){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合父分类ID状态异常");
        }
    }

}
