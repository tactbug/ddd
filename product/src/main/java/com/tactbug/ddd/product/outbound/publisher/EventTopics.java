package com.tactbug.ddd.product.outbound.publisher;

import com.tactbug.ddd.product.assist.exception.TactProductException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventTopics {
    CATEGORY,
    ;

    public static EventTopics get(String topic){
        for (EventTopics e :
                EventTopics.values()) {
            if (e.name().equals(topic)){
                return e;
            }
        }
        throw TactProductException.unknowEnumError(topic);
    }
}
