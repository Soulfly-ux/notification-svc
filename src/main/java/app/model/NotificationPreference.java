package app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class NotificationPreference {


    // ТОЗИ КЛАС СЪХРАНЯВА КОНКРЕТНА ПРЕФЕРЕНЦИЯ ЗА НОТИФИКАЦИИТЕ ЗА ЕДИН ПОТРЕБИТЕЛ
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false) // unique = true, защото искаме потребителя да има само една преференция(или само имейл, или само смс, или само MOBILE_PUSH)
    private UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;


    private boolean isEnabled;

    @Column(nullable = false)
    private String contactInfo;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;





}
