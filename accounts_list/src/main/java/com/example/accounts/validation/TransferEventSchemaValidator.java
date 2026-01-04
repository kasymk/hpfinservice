package com.example.accounts.validation;

import com.example.accounts.exception.SchemaValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class TransferEventSchemaValidator {

    private final JsonSchema schema;
    private final ObjectMapper objectMapper;

    public TransferEventSchemaValidator(ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;

        JsonSchemaFactory factory =
                JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);

        try (InputStream is = getClass()
                .getResourceAsStream("/schema/transfer-completed-event.json")) {
            this.schema = factory.getSchema(is);
        }
    }

    public void validate(Object event) {
        JsonNode node = objectMapper.valueToTree(event);
        Set<ValidationMessage> errors = schema.validate(node);

        if (!errors.isEmpty()) {
            throw new SchemaValidationException(errors);
        }
    }
}

