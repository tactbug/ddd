package com.tactbug.ddd.product.domain.brand;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.brand.command.*;
import com.tactbug.ddd.product.domain.brand.event.*;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.command.UpdateRemark;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Brand extends BaseDomain {

    private String name;
    private String remark;

    private List<Long> categoryIds;

    public Brand(){
        super();
    }

    private Brand(Long id){
        super(id);
    }

    public static Brand generate(Long id, CreateBrand createBrand){
        Brand brand = new Brand(id);
        brand.name = createBrand.name();
        brand.remark = createBrand.remark();
        brand.check();
        return brand;
    }

    public BrandCreated createBrand(Long eventId, Long operator){
        check();
        return new BrandCreated(eventId, this, EventType.CREATED, operator);
    }

    public static Brand replay(Collection<BrandEvent> events, Brand snapshot) {
        if (Objects.isNull(snapshot)){
            snapshot = new Brand();
        }
        if (events.isEmpty()){
            return snapshot;
        }
        List<Event<Brand>> sortedEvents = events.stream().sorted().collect(Collectors.toList());
        if (sortedEvents.get(0).getDomainVersion() - snapshot.getVersion() != 1){
            throw new IllegalStateException("快照版本[" + snapshot.getVersion() + "]跟溯源版本[" + sortedEvents.get(0).getDomainVersion() + "]不匹配");
        }
        return doReplay(snapshot, sortedEvents);
    }

    public NameUpdated updateName(Long eventId, UpdateBrandName updateBrandName){
        name = updateBrandName.name();
        update();
        check();
        return new NameUpdated(eventId, this, EventType.UPDATED, updateBrandName.operator());
    }

    public RemarkUpdated updateRemark(Long eventId, UpdateBrandRemark updateBrandRemark){
        remark = updateBrandRemark.remark();
        update();
        check();
        return new RemarkUpdated(eventId, this, EventType.UPDATED, updateBrandRemark.operator());
    }

    public BrandDeleted deleteBrand(Long eventId, DeleteBrand deleteBrand){
        update();
        check();
        return new BrandDeleted(eventId, this, EventType.DELETED, deleteBrand.operator());
    }

    public List<BrandEvent> update(IdUtil idUtil, BrandCommand brandCommand){
        List<BrandEvent> events = new ArrayList<>();
        if (Objects.nonNull(brandCommand.getName())){
            events.add(updateName(idUtil.getId(), brandCommand.updateName()));
        }
        if (Objects.nonNull(brandCommand.getRemark())){
            events.add(updateRemark(idUtil.getId(), brandCommand.updateRemark()));
        }
        return events;
    }

    private static Brand doReplay(Brand snapshot, List<Event<Brand>> events) {
        for (int i = 0; i < events.size(); i++) {
            Event<Brand> current = events.get(i);
            if (i < events.size() - 1){
                Event<Brand> next = events.get(i + 1);
                if (next.getDomainVersion() - current.getDomainVersion() != 1){
                    throw new IllegalStateException("溯源版本顺序错误, " +
                            "current[" + current.getId() + "]版本[" + current.getDomainVersion() + "], " +
                            "next[" + next.getId() + "]版本[" + next.getDomainVersion() +"]");
                }
                if (current.getEventType().equals(EventType.DELETED)){
                    throw new IllegalStateException("溯源删除事件[" + current.getId() + "]后不能有后续事件");
                }
            }
            snapshot.eventsReplay(events.get(i));
        }
        snapshot.check();
        return snapshot;
    }

    private void eventsReplay(Event<Brand> event) {
        super.replay(event);
        String json = event.getData();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new TactProductException("json解析异常", e.getMessage());
        }
        replayAttr(data);
    }

    private void replayAttr(Map<String, Object> data){
        replayName(data);
        replayRemark(data);
    }

    private void replayName(Map<String, Object> data){
        if (data.containsKey("name")){
            this.name = data.get("name").toString();
        }
    }

    private void replayRemark(Map<String, Object> data){
        if (data.containsKey("remark")){
            this.remark = data.get("remark").toString();
        }
    }


    public void check(){
        super.check();
        if (Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("品牌名称不能为空");
        }
    }
}
