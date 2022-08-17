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
public class  Price {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long brandId;
    private Date startDate;
    private Date endDate;
    private Integer priceList;
    private Integer productId;
    private Integer priority;
    private BigDecimal price;
    private String curr;

    @JsonIgnore
    @Transient
    private List<LocalDate> priceMandatoryDays;

    public Price(Long brandId, Date startDate, Date endDate, Integer priceList, Integer productId, Integer priority, BigDecimal price, String curr) {
        this.brandId = brandId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priceList = priceList;
        this.productId = productId;
        this.priority = priority;
        this.price = price;
        this.curr = curr;
    }

    public boolean validPriceRange(Date applicationTime) {
        return applicationDates(applicationTime);
    }

    private boolean applicationDates(Date applicationTime) {
        return this.startDate.compareTo(applicationTime) <= 0 && this.endDate.compareTo(applicationTime) >= 0;
    }

    public List<LocalDate> lookForApplicationDates(Date filterDate) {

        this.priceMandatoryDays = Collections
                .unmodifiableList(listPriceMandatoryDays(filterDate));
        return this.priceMandatoryDays;
    }

    private List<LocalDate> listPriceMandatoryDays(Date filterDate) {

        final List<LocalDate> localDateList = LocalDate.ofInstant(filterDate.toInstant(), ZoneId.of("UTC"))
                .datesUntil(LocalDate.ofInstant(this.endDate.toInstant(), ZoneId.of("UTC")))
                .collect(Collectors .toList());
        if (localDateList.isEmpty()) localDateList.add(LocalDate.ofInstant(filterDate.toInstant(), ZoneId.of("UTC")));

        return localDateList;
    }
}
