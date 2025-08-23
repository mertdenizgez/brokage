package com.brokage.application.mapper;

import com.brokage.application.dto.request.CreateOrderRequest;
import com.brokage.application.dto.response.OrderResponse;
import com.brokage.domain.entity.Order;

import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Money;
import com.brokage.domain.valueobject.Quantity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(source = "assetName", target = "assetSymbol", qualifiedByName = "stringToAssetSymbol")
    @Mapping(source = "size", target = "size", qualifiedByName = "bigDecimalToQuantity")
    @Mapping(source = "price", target = "price", qualifiedByName = "bigDecimalToMoney")
    Order toEntity(CreateOrderRequest request);
    
    @Mapping(source = "assetSymbol", target = "assetName", qualifiedByName = "assetSymbolToString")
    @Mapping(source = "size", target = "size", qualifiedByName = "quantityToBigDecimal")
    @Mapping(source = "price", target = "price", qualifiedByName = "moneyToBigDecimal")
    OrderResponse toResponse(Order order);
    
    List<OrderResponse> toResponseList(List<Order> orders);
    
    @org.mapstruct.Named("stringToAssetSymbol")
    default AssetSymbol stringToAssetSymbol(String assetName) {
        return assetName != null ? AssetSymbol.of(assetName) : null;
    }
    
    @org.mapstruct.Named("assetSymbolToString")
    default String assetSymbolToString(AssetSymbol assetSymbol) {
        return assetSymbol != null ? assetSymbol.getSymbol() : null;
    }
    
    @org.mapstruct.Named("bigDecimalToQuantity")
    default Quantity bigDecimalToQuantity(BigDecimal value) {
        return value != null ? Quantity.of(value) : null;
    }
    
    @org.mapstruct.Named("quantityToBigDecimal")
    default BigDecimal quantityToBigDecimal(Quantity quantity) {
        return quantity != null ? quantity.getValue() : null;
    }
    
    @org.mapstruct.Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal value) {
        return value != null ? Money.of(value) : null;
    }
    
    @org.mapstruct.Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.getAmount() : null;
    }
}
