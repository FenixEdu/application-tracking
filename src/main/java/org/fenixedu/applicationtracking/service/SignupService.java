package org.fenixedu.applicationtracking.service;

import com.google.common.primitives.Longs;
import org.fenixedu.applicationtracking.domain.Actor;
import org.fenixedu.applicationtracking.domain.Application;
import org.fenixedu.applicationtracking.domain.ApplicationTrackingDomainException;
import org.fenixedu.applicationtracking.domain.Period;
import org.fenixedu.bennu.core.domain.User;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

@Service
public class SignupService {

    @Atomic
    public Application createApplication(Period period, String givenNames, String familyNames, String email) {
        if (period.isOpen()) {
            return new Application(period, givenNames, familyNames, email);
        } else {
            throw ApplicationTrackingDomainException.periodNotOpen(period.getSlug());
        }
    }

    @Atomic
    public Application createApplication(Period period, User user) {
        if (period.isOpen()) {
            Optional<Application> application = findApplicationForUser(period, user);
            if (application.isPresent()) {
                return application.get();
            } else {
                return new Application(period, user);
            }
        } else {
            throw ApplicationTrackingDomainException.periodNotOpen(period.getSlug());
        }
    }

    public Optional<Application> findApplicationForUser(Period period, User user) {
        return user.getApplicationSet().stream().filter(a -> a.getPeriod().equals(period)).findAny();
    }

    @Atomic(mode = TxMode.WRITE)
    public void regenerateSecret(Actor actor) {
        actor.generateSecret();
    }

    public String generateHashForApplicant(long timestamp, Period period, String nonce, String email) {
        return hmac(timestamp, period, nonce, email);
    }

    public boolean validateHash(long timestamp, Period period, String nonce, String email, String hash) {
        return validateTimestamp(period, timestamp) && hmac(timestamp, period, email, nonce).equals(hash);
    }

    // Internal implementation

    private boolean validateTimestamp(Period period, long timestamp) {
        Instant then = Instant.ofEpochMilli(timestamp), now = Instant.now();
        return period.isOpen() && then.isBefore(now) && then.plus(1, ChronoUnit.HOURS).isAfter(now);
    }

    private static final String ALGORITHM = "HmacSHA512";

    private String hmac(long timestamp, Period period, String nonce, String email) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(period.getSignatureKey(), ALGORITHM));
            mac.update(Longs.toByteArray(timestamp));
            mac.update(period.getSlug().getBytes(StandardCharsets.UTF_8));
            mac.update(nonce.getBytes(StandardCharsets.UTF_8));
            mac.update(email.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(mac.doFinal());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Could not compute hash", e);
        }
    }

}
