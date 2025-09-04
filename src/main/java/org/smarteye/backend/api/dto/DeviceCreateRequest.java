package org.smarteye.backend.api.dto;

public record DeviceCreateRequest(String type, String serial, String status) {}
