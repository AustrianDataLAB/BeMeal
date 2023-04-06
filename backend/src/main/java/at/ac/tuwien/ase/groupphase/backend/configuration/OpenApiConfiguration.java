package at.ac.tuwien.ase.groupphase.backend.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "BeMeal API", version = "1.0", description = "Rest API for the communication with BeMeal"), security = {
        @SecurityRequirement(name = "bearerToken"), @SecurityRequirement(name = "credentials") })
@SecuritySchemes({
        @SecurityScheme(name = "bearerToken", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "For operation which require authentication. Either participant or game-master"),
        @SecurityScheme(name = "credentials", type = SecuritySchemeType.HTTP, scheme = "basic", description = "For operation which require authentication. Either participant or game-master") })
public class OpenApiConfiguration {
}
