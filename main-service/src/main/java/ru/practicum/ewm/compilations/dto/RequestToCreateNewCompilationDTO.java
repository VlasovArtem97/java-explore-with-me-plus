package ru.practicum.ewm.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestToCreateNewCompilationDTO {

    private List<Long> eventsId;

    private boolean pinned;

    @NotBlank(message = "Подборка событий должна иметь название")
    @Size(min = 3)
    private String title;
}
