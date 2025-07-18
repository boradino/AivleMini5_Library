package aivlelibraryminiproj.domain;

import aivlelibraryminiproj.domain.*;
import java.util.*;
import aivlelibraryminiproj.infra.AbstractEvent;
import lombok.*;

@Data
@ToString
public class BookSubscriptionApplied extends AbstractEvent {
    
    private Long id;
    private Long subscriberId;
    private Long bookId;
    private Boolean isSubscribed;
    private Date subscriptionDate;
    private Date subscriptionExpiredDate;
    private String title;
    private Integer subscriptionFee;
    private Boolean isBestSeller;
}
