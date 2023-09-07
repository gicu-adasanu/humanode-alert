package io.humanode.humanode.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BioAuthStatusDTO {
    private String jsonrpc;

    private Object result;

    private int id;
}
