package com.in.jrfc.entities;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
public class  Prize {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long brandId;
    private Date startDate;
    private Date endDate;
    private Integer prizeList;
    private Integer productId;
    private Integer priority;
    private BigDecimal prize;
    private String curr;

    public Prize(Long brandId, Date startDate, Date endDate, Integer prizeList, Integer productId, Integer priority, BigDecimal prize, String curr) {
        this.brandId = brandId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.prizeList = prizeList;
        this.productId = productId;
        this.priority = priority;
        this.prize = prize;
        this.curr = curr;
    }

    public boolean validPrizeRange(Date applicationTime) {
        return applicationDates(applicationTime);
    }

    private boolean applicationDates(Date applicationTime) {
        return this.startDate.compareTo(applicationTime) <= 0 && this.endDate.compareTo(applicationTime) >= 0;
    }

}
