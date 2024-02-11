package org.qiyu.live.user.provider.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaObject {
    
    private String code;
    private String userId;
}
