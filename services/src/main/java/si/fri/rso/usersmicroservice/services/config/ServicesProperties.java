package si.fri.rso.usersmicroservice.services.config;

import javax.enterprise.context.ApplicationScoped;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

@ApplicationScoped
@ConfigBundle("microservices")
public class ServicesProperties {

    @ConfigValue(watch = true)
    private String mailingServiceHost;

    public String getMailingServiceHost() {
        return mailingServiceHost;
    }

    public void setMailingServiceHost(String mailingServiceHost) {
        this.mailingServiceHost = mailingServiceHost;
    }
}
