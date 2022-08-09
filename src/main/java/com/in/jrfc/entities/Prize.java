package com.in.jrfc.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
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

    @JsonIgnore
    @Transient
    private List<LocalDate> prizeMandatoryDays;

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

    public List<LocalDate> lookForApplicationDates(Date filterDate) {

        for (LocalDate localDate : this.prizeMandatoryDays = Collections.unmodifiableList(listPrizeMandatiryDays(filterDate))) {
        }
        return this.prizeMandatoryDays;
    }

    private List<LocalDate> listPrizeMandatiryDays(Date filterDate) {

        final List<LocalDate> localDateList = LocalDate.ofInstant(filterDate.toInstant(), ZoneId.of("UTC"))
                .datesUntil(LocalDate.ofInstant(this.endDate.toInstant(), ZoneId.of("UTC")))
                .collect(Collectors .toList());
        if (localDateList.isEmpty()) localDateList.add(LocalDate.ofInstant(filterDate.toInstant(), ZoneId.of("UTC")));

        return localDateList;
    }
}
