package com.project.book_store.payload.mapper;

import com.project.book_store.entity.concretes.Customer;
import com.project.book_store.payload.request.CustomerRequest;
import com.project.book_store.payload.response.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer mapToCustomer(CustomerRequest request);

    @Mapping(target = "userId", source = "id")
    CustomerResponse toCustomerResponse(Customer customer);

    List<CustomerResponse> toCustomerResponseList(List<Customer> customers);

    void updateCustomerFromRequest(CustomerRequest request,@MappingTarget Customer customer);

}
