package si.fri.rso.usersmicroservice.api.v1.health;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import si.fri.rso.usersmicroservice.services.config.ServicesProperties;

@Readiness
@ApplicationScoped
public class MailingServiceHealthCheck implements HealthCheck {
    @Inject
    private ServicesProperties servicesProperties;

    @Override
    public HealthCheckResponse call() {
        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(
                    servicesProperties.getMailingServiceHost() + "/v1/mailing/status")
                    .openConnection();
            connection.setRequestMethod("HEAD");

            if (connection.getResponseCode() == 200) {
                return HealthCheckResponse.named(MailingServiceHealthCheck.class.getSimpleName()).up().build();
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return HealthCheckResponse.named(MailingServiceHealthCheck.class.getSimpleName()).down().build();
    }
}
