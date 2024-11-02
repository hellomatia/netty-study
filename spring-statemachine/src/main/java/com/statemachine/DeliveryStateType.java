package com.statemachine;

public enum DeliveryStateType {
    IDLE,
    GOING_TO_LOADING_POINT,
    WAITING_FOR_LOADING,
    LOADING,
    GOING_TO_UNLOADING_POINT,
    WAITING_FOR_UNLOADING,
    UNLOADING
}
