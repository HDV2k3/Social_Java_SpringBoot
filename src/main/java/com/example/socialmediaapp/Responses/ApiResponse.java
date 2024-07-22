//package com.example.socialmediaapp.Responses;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//
//@Data
//@Builder
//@NoArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class ApiResponse<T> {
//    @Builder.Default
//    private int code = 1000;
//    private String message;
//    private T result;
//
//
//    public ApiResponse(int code, String message, T result) {
//        this.code = code;
//        this.message = message;
//        this.result = result;
//    }
//}
package com.example.socialmediaapp.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    int code = 1000;
    String message;
    T result;

    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.code = success ? 1000 : 9999; // Thay đổi mã lỗi tùy theo logic của bạn
    }
}
