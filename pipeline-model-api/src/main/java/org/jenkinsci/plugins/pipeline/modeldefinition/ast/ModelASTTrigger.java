package org.jenkinsci.plugins.pipeline.modeldefinition.ast;

import org.jenkinsci.plugins.pipeline.modeldefinition.validator.ModelValidator;

/**
 * A single trigger, corresponding eventually to a {@code Trigger}
 *
 * @author Andrew Bayer
 */
public class ModelASTTrigger extends ModelASTMethodCall {
    public ModelASTTrigger(Object sourceLocation) {
        super(sourceLocation);
    }

    @Override
    public void validate(final ModelValidator validator) {
        validator.validateElement(this);
        super.validate(validator);
    }

    @Override
    public String toString() {
        return "ModelASTTrigger{" +
                "name='" + getName() + '\'' +
                ", args=" + getArgs() +
                "}";
    }
}
