package com.clinic.management.dto;

import com.clinic.management.entity.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {
    private AppointmentStatus status;
}