package com.anastasiat.author.controller.payload;

import java.time.LocalDate;

public record NewAuthorPayload(String firstName, String lastName, String middleName, LocalDate birthDate) {
}
