package app.web.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;


//DTO = contract
@Data
public class UpsertNotificationPreference {


    // За да получим това дто, ни трябва контролер
    // ДТО- то  е информация , която потребителя ни праща за да мога да си създам моето ентити

    @NotNull
    private UUID userId;

    private boolean notificationEnabled;

    @NotNull
    private NotificationTypeRequest type; // правим този енъм към DTO- то, който в себе си има само емайл, защото приложението за момента поддържа само емайл
                                          // тук не можем да сложим NotificationType, защото в него има емайл, SMS, MOBILE_PUSH и по този начин ще излъжем потребителя
    @NotNull
    @NotBlank
    private String contactInfo;
}
