package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateEventAdminRequestDto extends UpdateEventUserRequest {
}
