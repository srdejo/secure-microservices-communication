package com.linktic.productservice.infrastructure.adapter.mapper;

import com.linktic.productservice.domain.model.Product;
import com.linktic.productservice.infrastructure.adapter.in.rest.dto.*;
import com.linktic.productservice.infrastructure.adapter.out.persistence.ProductEntity;
import org.mapstruct.Mapper;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toDomain(ProductDTO dto);
    
    ProductDTO toDto(Product domain);

    Product toDomain(ProductEntity entity);

    ProductEntity toEntity(Product domain);

    default JsonApiResponseProductDTO toJsonResponse(Product product) {
        JsonApiDataProductDTO data = new JsonApiDataProductDTO();
        data.setType("products");
        data.setId(product.getId().toString());
        data.setAttributes(toDto(product));
        
        JsonApiResponseProductDTO response = new JsonApiResponseProductDTO();
        response.setData(data);
        return response;
    }

    default JsonApiResponseProductListDTO toJsonResponse(List<Product> products) {
        List<JsonApiDataProductDTO> dataList = products.stream()
                .map(p -> {
                    JsonApiDataProductDTO data = new JsonApiDataProductDTO();
                    data.setType("products");
                    data.setId(p.getId().toString());
                    data.setAttributes(toDto(p));
                    return data;
                })
                .collect(Collectors.toList());

        JsonApiResponseProductListDTO response = new JsonApiResponseProductListDTO();
        response.setData(dataList);
        return response;
    }
}
