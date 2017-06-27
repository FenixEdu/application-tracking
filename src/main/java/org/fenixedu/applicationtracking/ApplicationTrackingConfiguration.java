package org.fenixedu.applicationtracking;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class ApplicationTrackingConfiguration {

    @ConfigurationManager()
    public static interface ConfigurationProperties {

        @ConfigurationProperty(key = "recaptcha.site.key")
        public String reCaptchaSiteKey();

        @ConfigurationProperty(key = "recaptcha.secret")
        public String reCaptchaSecret();

        @ConfigurationProperty(key="currency", defaultValue = "EUR")
        public String currency();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}
