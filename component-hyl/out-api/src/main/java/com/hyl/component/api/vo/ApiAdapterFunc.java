package com.hyl.component.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.function.Function;

/**
 * @author hyl
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiAdapterFunc {

    private String name;

    private Map<String, Function> apiFunc;

}
