package org.fenixedu.applicationtracking.service;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.applicationtracking.ApplicationTrackingConfiguration;
import org.fenixedu.applicationtracking.service.ReCaptchaService.ReCaptchaCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;

@Service
@Conditional(ReCaptchaCondition.class)
public class ReCaptchaService implements CaptchaService {

    @Override
    public String generateCaptcha(HttpServletRequest request) {
        StringBuilder builder =
                new StringBuilder("<script src=\"https://www.google.com/recaptcha/api.js\" async defer></script>");
        builder.append("<div class=\"g-recaptcha\" data-sitekey=\"");
        builder.append(ApplicationTrackingConfiguration.getConfiguration().reCaptchaSiteKey());
        builder.append("\"></div>");
        return builder.toString();
    }

    @Override
    public boolean isValid(HttpServletRequest request) {
        String param = request.getParameter("g-recaptcha-response");
        if (param == null) {
            return false;
        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(2);
        params.add("secret", ApplicationTrackingConfiguration.getConfiguration().reCaptchaSecret());
        params.add("response", param);
        String json = new RestTemplate().postForEntity("https://www.google.com/recaptcha/api/siteverify", params, String.class)
                .getBody();
        return new JsonParser().parse(json).getAsJsonObject().get("success").getAsBoolean();
    }

    public static final class ReCaptchaCondition implements Condition {
        @Override
        public boolean matches(ConditionContext ctx, AnnotatedTypeMetadata metadata) {
            return !Strings.isNullOrEmpty(ApplicationTrackingConfiguration.getConfiguration().reCaptchaSiteKey());
        }
    }
}
