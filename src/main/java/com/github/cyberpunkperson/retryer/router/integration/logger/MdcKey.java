package com.github.cyberpunkperson.retryer.router.integration.logger;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MdcKey {
    public static final String OPERATION_NAME = "operationName";
    public static final String FAILED_EVENT = "failedEvent";
}
