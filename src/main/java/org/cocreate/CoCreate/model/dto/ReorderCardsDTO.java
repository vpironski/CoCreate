package org.cocreate.CoCreate.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReorderCardsDTO {
    private List<String> newOrder;
}