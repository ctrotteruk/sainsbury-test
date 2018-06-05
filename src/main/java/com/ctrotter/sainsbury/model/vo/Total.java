package com.ctrotter.sainsbury.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gross",
    "vat"
})
/**
 * Json Class representing the vat and total cost of fruit.
 * @author CTrotter
 *
 */
public class Total implements Serializable
{

    @JsonProperty("gross")
    private BigDecimal gross = new BigDecimal(0);
    
    @JsonProperty("vat")
    private BigDecimal vat = new BigDecimal(0);
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -2720258943783951181L;

    @JsonProperty("gross")
    public BigDecimal getGross() {
        return gross;
    }

    @JsonProperty("gross")
    public void setGross(BigDecimal gross) {
        this.gross = gross;
        this.vat = gross.multiply(new BigDecimal(0.2)).setScale(2, RoundingMode.DOWN);
    }

    public Total withGross(BigDecimal gross) {
        this.gross = gross;
        
        return this;
    }

    @JsonProperty("vat")
    public BigDecimal getVat() {
    	
        return this.vat;
    }

    @JsonProperty("vat")
    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }



    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("gross", gross).append("additionalProperties", additionalProperties).toString();
    }

}
