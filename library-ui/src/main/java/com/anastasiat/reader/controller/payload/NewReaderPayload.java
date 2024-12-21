package com.anastasiat.reader.controller.payload;

import java.time.LocalDate;

public record NewReaderPayload(String firstName, String lastName, String middleName, String email) {
}
