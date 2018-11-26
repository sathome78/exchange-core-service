package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.exrates.model.dto.ChatHistoryDto;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ChatHistoryDateWrapperDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private List<ChatHistoryDto> messages;
}