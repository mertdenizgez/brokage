package com.brokage.application.mapper;

import com.brokage.application.dto.response.AssetResponse;
import com.brokage.domain.entity.Asset;
import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Quantity;
import com.brokage.domain.valueobject.UsableSize;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AssetMapper {

    @Mapping(source = "assetSymbol", target = "assetName", qualifiedByName = "assetSymbolToString")
    @Mapping(source = "size", target = "size", qualifiedByName = "quantityToDecimal")
    @Mapping(source = "usableSize", target = "usableSize", qualifiedByName = "usableSizeToDecimal")
    AssetResponse toResponse(Asset asset);

    List<AssetResponse> toResponseList(List<Asset> assets);

    @Named("assetSymbolToString")
    default String assetSymbolToString(AssetSymbol assetSymbol) {
        return assetSymbol != null ? assetSymbol.getSymbol() : null;
    }

    @Named("quantityToDecimal")
    default BigDecimal quantityToDecimal(Quantity quantity) {
        return quantity != null ? quantity.getValue() : null;
    }

    @Named("usableSizeToDecimal")
    default BigDecimal usableSizeToDecimal(UsableSize usableSize) {
        return usableSize != null ? usableSize.getValue() : null;
    }
}
