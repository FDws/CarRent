package org.teamwe.carrent.controller.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.teamwe.carrent.controller.utils.Format;
import org.teamwe.carrent.utils.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author FDws
 * Created on 2018/7/8 16:01
 */
@RestController
public class MErrorController extends AbstractErrorController {
    @Value("${server.error.path:/error}")
    private String error;

    public MErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @RequestMapping("${server.error.path:/error}")
    public Format error(HttpServletRequest request) {
        Format f = new Format().code(1).message(StringUtil.ERROR_OCCUR);
        Map<String, Object> prop = getErrorAttributes(request, false);
        for (String key : prop.keySet()) {
            f.addData(key, prop.get(key));
        }
        return f;
    }

    @Override
    public String getErrorPath() {
        return error;
    }
}
