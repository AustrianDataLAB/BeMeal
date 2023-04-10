package at.ac.tuwien.ase.groupphase.backend.event.listener;

import at.ac.tuwien.ase.groupphase.backend.event.RequestPasswordResetEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventListener {

    private final MessageSource messageSource;
    private final JavaMailSender mailSender;

    @Value("bemeal.frontend.passwordResetConfirmUrl")
    private final String passwordResetConfirmUrl;

    /**
     * Handle the password reset request. This just sends the confirmation message to the specified email.
     *
     * @param event
     *            the event which contains all necessary information for the reset process.
     */
    @Async
    @EventListener(RequestPasswordResetEvent.class)
    public void handleRequestPasswordReset(final RequestPasswordResetEvent event) {
        log.trace("handleRequestPasswordReset({})", event);
        final var confirmationUrl = this.passwordResetConfirmUrl.replace("{resetToken}",
                event.passwordResetToken().toString());

        final var email = new SimpleMailMessage();
        email.setTo(event.email());
        email.setSubject(messageSource.getMessage("requestPasswordResetSubject", null, Locale.getDefault()));
        email.setText(messageSource.getMessage("requestPasswordResetText", new Object[] { confirmationUrl },
                Locale.getDefault()));
        this.mailSender.send(email);
    }
}
