package com.nike.backstopper.jersey2sample.error;

import com.nike.backstopper.apierror.ApiError;
import com.nike.backstopper.apierror.ApiErrorBase;
import com.nike.backstopper.apierror.ApiErrorWithMetadata;
import com.nike.backstopper.apierror.sample.SampleCoreApiError;
import com.nike.backstopper.jersey2sample.model.RgbColor;
import com.nike.internal.util.MapBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Response.Status;

/**
 * Project-specific error definitions for this sample app. Note that the error codes for errors specified here must
 * conform to the range specified in {@link SampleProjectApiErrorsImpl#getProjectSpecificErrorCodeRange()} or an
 * exception will be thrown on app startup, and unit tests should fail. The one exception to this rule is a "core
 * error wrapper" - an instance that shares the same error code, message, and HTTP status code as a
 * {@link SampleProjectApiErrorsImpl#getCoreApiErrors()} instance (in this case that means a wrapper around
 * {@link SampleCoreApiError}).
 *
 * @author Nic Munroe
 */
public enum SampleProjectApiError implements ApiError {
    FIELD_CANNOT_BE_NULL_OR_BLANK(99100, "Field cannot be null or empty", Status.BAD_REQUEST.getStatusCode()),
    // FOO_STRING_CANNOT_BE_BLANK shows how you can build off a base/generic error and add metadata.
    FOO_STRING_CANNOT_BE_BLANK(FIELD_CANNOT_BE_NULL_OR_BLANK, MapBuilder.builder("field", (Object)"foo").build()),
    INVALID_RANGE_VALUE(99110, "The range_0_to_42 field must be between 0 and 42 (inclusive)",
                        Status.BAD_REQUEST.getStatusCode()),
    // RGB_COLOR_CANNOT_BE_NULL could build off FIELD_CANNOT_BE_NULL_OR_BLANK like FOO_STRING_CANNOT_BE_BLANK does,
    //      however this shows how you can make individual field errors with unique code and custom message.
    RGB_COLOR_CANNOT_BE_NULL(99120, "The rgb_color field must be defined", Status.BAD_REQUEST.getStatusCode()),
    NOT_RGB_COLOR_ENUM(99130, "The rgb_color field value must be one of: " + Arrays.toString(RgbColor.values()),
                       Status.BAD_REQUEST.getStatusCode()),
    MANUALLY_THROWN_ERROR(99140, "You asked for an error to be thrown", Status.INTERNAL_SERVER_ERROR.getStatusCode()),
    // This is a wrapper around a core error. It will have the same error code, message, and HTTP status code,
    //      but will show up in the logs with contributing_errors="SOME_MEANINGFUL_ERROR_NAME", allowing you to
    //      distinguish the context of the error vs. the core GENERIC_SERVICE_ERROR at a glance.
    SOME_MEANINGFUL_ERROR_NAME(SampleCoreApiError.GENERIC_SERVICE_ERROR);

    private final ApiError delegate;

    SampleProjectApiError(ApiError delegate) {
        this.delegate = delegate;
    }

    SampleProjectApiError(ApiError delegate, Map<String, Object> metadata) {
        this(new ApiErrorWithMetadata(delegate, metadata));
    }

    SampleProjectApiError(int errorCode, String message, int httpStatusCode) {
        this(new ApiErrorBase(
            "delegated-to-enum-wrapper-" + UUID.randomUUID().toString(), errorCode, message, httpStatusCode
        ));
    }

    @SuppressWarnings("unused")
    SampleProjectApiError(int errorCode, String message, int httpStatusCode, Map<String, Object> metadata) {
        this(new ApiErrorBase(
            "delegated-to-enum-wrapper-" + UUID.randomUUID().toString(), errorCode, message, httpStatusCode, metadata
        ));
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getErrorCode() {
        return delegate.getErrorCode();
    }

    @Override
    public String getMessage() {
        return delegate.getMessage();
    }

    @Override
    public int getHttpStatusCode() {
        return delegate.getHttpStatusCode();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return delegate.getMetadata();
    }

}