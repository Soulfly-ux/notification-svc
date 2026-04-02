package app.web.mapper;

import app.model.NotificationPreference;
import app.model.NotificationType;
import app.web.Dto.NotificationTypeRequest;
import app.web.Dto.NotificationPreferenceResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    // Mapping logic: прехвърляме един тип енумерация към друг тип енумерация
    // Връща NotificationType
    public static NotificationType fromNotificationTypeRequest(NotificationTypeRequest dto) {

        return switch (dto) {

            case EMAIL -> NotificationType.EMAIL;

        };
    }

    // Mapping logic: build dto from entity
    // Връща NotificationPreferenceResponse(конкретно DTO)
    public static NotificationPreferenceResponse fromNotificationPreference(NotificationPreference entity) {
        // NotificationPreferenceResponse - dto
        // NotificationPreference - entity

        return NotificationPreferenceResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .contactInfo(entity.getContactInfo())
                .enabled(entity.isEnabled())
                .userId(entity.getUserId())
                .build();
    }
}
