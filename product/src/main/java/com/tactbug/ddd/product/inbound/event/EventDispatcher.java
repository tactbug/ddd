package com.tactbug.ddd.product.inbound.event;

import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventDispatcher {

    public void handler(String data){

    }

    private void accept(Event<? extends BaseDomain> event){

    }
}
