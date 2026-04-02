package app.web.Dto;

import app.model.NotificationStatus;
import app.model.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {


    // Тук не слагаме валидации, защото информацията идва от базата данни, която вече е валидирана преди да бъде записана там
    // имаме нужда от валидации само, когато информацията идва от вън
    private String subject;

    private LocalDateTime createdOn;

    private NotificationStatus status;

    private NotificationType type;
}
