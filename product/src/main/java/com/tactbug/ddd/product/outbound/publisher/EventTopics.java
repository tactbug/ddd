package com.tactbug.ddd.product.outbound.publisher;

import com.tactbug.ddd.product.assist.exception.TactProductException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventTopics {
    CATEGORY("category"),
    BRAND("brand")
    ;

    private final String name;

    public static EventTopics get(String topic){
        for (EventTopics e :
                EventTopics.values()) {
            if (e.getName().equals(topic)){
                return e;
            }
        }
        throw TactProductException.unKnowEnumError(topic, null);
    }
}
