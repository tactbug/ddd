package com.tactbug.ddd.common.avro;

public enum EventTypeEnum {
    CATEGORY_CREATED("category_created", "product/category_created.avcs"),
    ;

    private final String eventType;
    private final String avroPath;

    EventTypeEnum(String eventType, String avroPath) {
        this.eventType = eventType;
        this.avroPath = avroPath;
    }

    public static EventTypeEnum getInstance(String eventType){
        for (EventTypeEnum e :
                EventTypeEnum.values()) {
            if (e.eventType.equals(eventType)){
                return e;
            }
        }
        throw new IllegalStateException("不支持的事件类型枚举[" + eventType + "]");
    }

    public String getEventType() {
        return eventType;
    }

    public String getAvroPath() {
        return avroPath;
    }
}
