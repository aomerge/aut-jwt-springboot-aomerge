package com.aut_jwt.aut_jwt.config.swagger;

import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerDecorators {
    @Bean
    public OperationCustomizer docAnnotationCustomizer (){
        return (operation, handlerMethod) -> {
            Doc doc = handlerMethod.getMethodAnnotation(Doc.class);
            if (doc == null) {
                doc = handlerMethod.getBeanType().getAnnotation(Doc.class); // a nivel clase
            }
            if (doc != null) {
                if (!doc.summary().isEmpty()) operation.setSummary(doc.summary());
                if (!doc.description().isEmpty()) operation.setDescription(doc.description());
                if (doc.tags().length > 0) operation.setTags(List.of(doc.tags()));
                if (doc.secured()) {
                    operation.addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth"));
                }
            }
            return operation;
        };
    }

    @Bean
    public OpenApiCustomizer standardOpenApiCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(op -> {
                    var responses = op.getResponses();
                    responses.addApiResponse("400", apiResponse("Bad Request"));
                    responses.addApiResponse("401", apiResponse("Unauthorized"));
                    responses.addApiResponse("403", apiResponse("Forbidden"));
                    responses.addApiResponse("500", apiResponse("Internal Server Error"));
                })
        );
    }

    private ApiResponse apiResponse(String badRequest) {
        return new ApiResponse().description(badRequest);
    }
}
