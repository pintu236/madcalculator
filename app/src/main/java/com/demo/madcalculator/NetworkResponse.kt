package com.demo.madcalculator

class NetworkResponse<T> {
    var status: Status
    var message: String?
    var statusMessage = ""
    var response: T

    enum class Status {
        SUCCESS, LOADING, ERROR
    }

    constructor(status: Status, message: String?, response: T) {
        this.status = status
        this.message = message
        this.response = response
    }

    constructor(status: Status, message: String?, statusMessage: String, response: T) {
        this.status = status
        this.message = message
        this.response = response
        this.statusMessage = statusMessage
    }



    companion object {
        fun <T> loading(): NetworkResponse<T?> {
            return NetworkResponse(Status.LOADING, null, null)
        }

        fun <T> success(data: T, message: String?): NetworkResponse<T> {
            return NetworkResponse(Status.SUCCESS, message, data)
        }

        fun <T> success(data: T): NetworkResponse<T> {
            return NetworkResponse(Status.SUCCESS, null, data)
        }

        fun <T> success(message: String?): NetworkResponse<T?> {
            return NetworkResponse(Status.SUCCESS, message, null)
        }

        fun <T> success(message: String?, statusMessage: String): NetworkResponse<T?> {
            return NetworkResponse(Status.SUCCESS, message, statusMessage, null)
        }

        fun <T> error(error: String?): NetworkResponse<T?> {
            return NetworkResponse(Status.ERROR, error, null)
        }
    }
}