package com.project.book_store.payload.response;

import com.project.book_store.payload.request.abstracts.BaseUserRequest;
import com.project.book_store.payload.response.abstracts.BaseUserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CustomerResponse extends BaseUserResponse {

}
