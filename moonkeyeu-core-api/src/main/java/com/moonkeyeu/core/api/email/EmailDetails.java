package com.moonkeyeu.core.api.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {
    private String subject;
    private String url;
    private EmailTemplateName emailTemplateName;
}
