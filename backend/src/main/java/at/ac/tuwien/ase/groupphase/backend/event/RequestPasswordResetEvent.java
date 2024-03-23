package at.ac.tuwien.ase.groupphase.backend.event;

import java.util.UUID;

/**
 * Event used to indicate a request for a password reset.
 *
 * @param email
 *            the email to send the reset message to
 * @param passwordResetToken
 *            the token which is used to identify the {@link at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser}
 *            subject
 */
public record RequestPasswordResetEvent(String email, UUID passwordResetToken) {
}
