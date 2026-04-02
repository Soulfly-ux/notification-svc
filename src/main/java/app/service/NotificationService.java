package app.service;

import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationStatus;
import app.model.NotificationType;
import app.repository.NotificationPreferenceRepository;
import app.repository.NotificationRepository;
import app.web.Dto.NotificationRequest;
import app.web.Dto.UpsertNotificationPreference;
import app.web.mapper.DtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class NotificationService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationRepository notificationRepository;
    private final MailSender mailSender;
    @Autowired
    public NotificationService(NotificationPreferenceRepository preferenceRepository, NotificationRepository notificationRepository, MailSender mailSender) {
        this.preferenceRepository = preferenceRepository;
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }




    public NotificationPreference upsertPreference(UpsertNotificationPreference dto) {
       //NotificationPreference - entity
        // UpsertNotificationPreference - dto


        // upsert:




        //1.try to find such exist in the database:
        Optional<NotificationPreference> userNotificationPrefererenceOptional = preferenceRepository.findByUserId(dto.getUserId());


        //2. if exists update it:
        if (userNotificationPrefererenceOptional.isPresent()){

            NotificationPreference notificationPreference = userNotificationPrefererenceOptional.get();// ако съществува, дай ми тази преференция
            // не можем да променим id, updatedOn, createdOn, но можем да променим enabled, type, contactInfo на тази преференция
            notificationPreference.setEnabled(dto.isNotificationEnabled());
            notificationPreference.setType(DtoMapper.fromNotificationTypeRequest(dto.getType())); // реално връща енумерацията NotificationType, ако не направим мапър,
            // ще връща UpsertNotificationPreference dto с неговия енъм и ще хвърли грешка, защото енъма на notificationPreference не е  същия тип - той поддържа три вида нотификации
            // , а този на дто- то само имейл
            notificationPreference.setContactInfo(dto.getContactInfo());
            notificationPreference.setUpdatedOn(LocalDateTime.now());

            return preferenceRepository.save(notificationPreference);
        }
         // Hier I build a new entity object and save it
        //3. if not exists create new one:
        NotificationPreference newPreference = NotificationPreference.builder()
                .userId(dto.getUserId())
                .type(DtoMapper.fromNotificationTypeRequest(dto.getType()))
                .isEnabled(dto.isNotificationEnabled())
                .contactInfo(dto.getContactInfo())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        System.out.println();

        return preferenceRepository.save(newPreference);
    }

    public NotificationPreference getPreferencesByUserId(UUID userId) {
        // хвърля грешка, ако няма такава преференция.Грешно е ако направим да връща null
        return preferenceRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Notification preference not found"));
    }

    public Notification sendNotification(NotificationRequest notificationRequest) {


        //1. Взимаме потребителя до който искаме да изпратим нотификацията:
        UUID userId = notificationRequest.getUserId();

        //2. Взимаме преференцията за този потребител:
        NotificationPreference userPreferences = getPreferencesByUserId(userId);

        //3. Проверяваме дали нотификацията е включена:
        if (!userPreferences.isEnabled()) {
            throw new IllegalArgumentException("Notification is not enabled");
        }




        SimpleMailMessage message = new SimpleMailMessage();


        message.setTo(userPreferences.getContactInfo());
        message.setSubject(notificationRequest.getSubject());
        message.setText(notificationRequest.getBody()); // Ще изпрати нотификацията от email-a, който сме конфигурирали в application.properties(паролата там си я взимаме от настройките в профила ни в gmail)


        //Създаваме нотификацията за да може да се запази в базата данни и да има проследимост
        Notification notification = Notification.builder()
                .subject(notificationRequest.getSubject())
                .body(notificationRequest.getBody())
                .createdOn(LocalDateTime.now())
                .userId(userId)
                .isDeleted(false)
                .type(NotificationType.EMAIL)
                .build();

        //  mailSender- идва от библиотека spring-boot-starter-mail в pom.xml
        try {
            mailSender.send(message);
            notification.setStatus(NotificationStatus.SUCCEEDED);
        }catch (Exception e){
            notification.setStatus(NotificationStatus.FAILED);
            log.error("There was an issue sending an email to %s due to %s.".formatted(userPreferences.getContactInfo(), e.getMessage()));
        }

        return notificationRepository.save(notification);
    }
}
