package dev.zornov.market.auth.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

sealed interface ApprovalMessage {
    data class Request(val userId: String) : ApprovalMessage

    data class Response(val result: Result) : ApprovalMessage {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
        @JsonSubTypes(
            JsonSubTypes.Type(value = Result.Success::class, name = "Success"),
            JsonSubTypes.Type(value = Result.Error::class, name = "Error")
        )
        sealed interface Result {
            @JsonTypeName("Success")
            data class Success(val userId: String) : Result

            @JsonTypeName("Error")
            data class Error(val userId: String) : Result
        }
    }
}