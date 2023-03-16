package com.hyl.component.gray.business.rule;

import com.hyl.component.gray.config.GrayConfigProperties;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 2022-12-04 02:22
 * create by hyl
 * desc: 顺序
 * @author hyl
 */
@Component("orderly")
public class OrderlyGrayRule extends AbstractGrayRule {

    private static final String REGEX_PERSEN = "/^(100|[1-9]?\\d(\\.\\d\\d?\\d?)?)%$|0$/";

    private static Integer count = 0;

    @Override
    public boolean matchingRule(String grayTag, String grayValue) {
        if (!super.matchingRule(grayTag, grayValue)) {
            return false;
        }
        GrayConfigProperties.FirsTagConfig grayTagConfig = grayConfigProperties.getFirstTag().get(grayTag);
        boolean matches = Pattern.matches(REGEX_PERSEN, grayTagConfig.getProportion());
        if (!matches) {
            return false;
        }
        count++;
        int orderly = Double.valueOf(grayTagConfig.getProportion().replace("%", "")).intValue();
        if (count == orderly) {
            count = 0;
            return true;
        }
        return false;
    }
}
