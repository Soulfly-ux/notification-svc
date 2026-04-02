package app.web;

import app.model.Notification;
import app.model.NotificationPreference;
import app.service.NotificationService;
import app.web.Dto.NotificationPreferenceResponse;
import app.web.Dto.NotificationRequest;
import app.web.Dto.NotificationResponse;
import app.web.Dto.UpsertNotificationPreference;
import app.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/preferences")

    public ResponseEntity<NotificationPreferenceResponse> upsertNotificationPreference(@RequestBody UpsertNotificationPreference upsertNotificationPreference) {

        System.out.println();
      // @RequestBody е DTO, т.е. търсим го в тялото на заявката - в POSTMAN например - ТОВА Е ТЯЛО НА ЗАЯВКАТА
        // ResponseEntity<.....> ТОВА Е ТЯЛО НА ВРЪЩАНЕ НА ОТГОВОРА, СЪЩО dto

        NotificationPreference notificationPreference1 = notificationService.upsertPreference(upsertNotificationPreference);
        // не трябва да изпращаме ентити към потребителя а трябва да изпращаме DTO,за това правом нов метод в DtoMapper

        NotificationPreferenceResponse responseDto = DtoMapper.fromNotificationPreference(notificationPreference1);

        System.out.println();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping("/preferences/{userId}") // Правим този метод за да се визуализира страницата с предпочитания на потребителя
    public ResponseEntity<NotificationPreferenceResponse> getUserNotificationPreferences(@RequestParam(name = "userId") UUID userId) {

       NotificationPreference notificationPreference = notificationService.getPreferencesByUserId(userId);// какви са конкретните предпочитания на даден потребител

        NotificationPreferenceResponse responseDto = DtoMapper.fromNotificationPreference(notificationPreference);// превръщаме notificationPreference(ентити)
                                                                                                                  // в NotificationPreferenceResponse (DTO) за да се визуализира на потребителя
                                                                                                                  // само  userId, type, enabled, contactInfo а не цялото ентити


        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDto);
    }


    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest notificationRequest) {

        // Entity
        Notification notification = notificationService.sendNotification(notificationRequest);

        // От entity трябва да направим DTO(чрез DtoMapper), за да се визуализира на потребителя
        NotificationResponse notificationResponse = DtoMapper.fromNotification(notification);

        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationResponse);
    }

}
