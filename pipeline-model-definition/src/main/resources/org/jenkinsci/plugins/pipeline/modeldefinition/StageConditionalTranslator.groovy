/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package org.jenkinsci.plugins.pipeline.modeldefinition

import org.jenkinsci.plugins.pipeline.modeldefinition.model.MethodMissingWrapper
import org.jenkinsci.plugins.pipeline.modeldefinition.model.StageConditionals
import org.jenkinsci.plugins.pipeline.modeldefinition.when.DeclarativeStageConditional
import org.jenkinsci.plugins.pipeline.modeldefinition.when.DeclarativeStageConditionalDescriptor
import org.jenkinsci.plugins.structs.describable.UninstantiatedDescribable
import org.jenkinsci.plugins.workflow.cps.CpsScript

import static org.jenkinsci.plugins.pipeline.modeldefinition.Utils.createStepsBlock
import static org.jenkinsci.plugins.pipeline.modeldefinition.Utils.isOfType

/**
 * Translates a closure containing a sequence of method calls into a {@link org.jenkinsci.plugins.pipeline.modeldefinition.model.StageConditionals} implementation
 */
public class StageConditionalTranslator implements MethodMissingWrapper, Serializable {

    List<UninstantiatedDescribable> actualList = []
    CpsScript script

    StageConditionalTranslator(CpsScript script) {
        this.script = script
    }

    def methodMissing(String s, args) {
        def argVal
        def retVal

        if (args instanceof List || args instanceof Object[]) {
            if (args.size() > 0) {
                argVal = args[0]
            } else {
                argVal = null
            }
        } else {
            argVal = args
        }

        if (Utils.instanceOfWrapper(Closure.class, argVal)) {
            argVal = createStepsBlock(argVal)
        }

        DeclarativeStageConditionalDescriptor descriptor = DeclarativeStageConditionalDescriptor.byName(s)
        if (descriptor == null) {
            throw new NoSuchMethodError(Messages.ModelValidator_ModelASTWhen_unknown(s, DeclarativeStageConditionalDescriptor.allNames().join(", ")))
        }

        if (argVal != null) {
            retVal = script."${s}"(argVal)
        } else {
            retVal = script."${s}"()
        }

        if (isOfType((UninstantiatedDescribable) retVal, DeclarativeStageConditional.class)) {
            actualList << (UninstantiatedDescribable) retVal
        }

        return retVal

    }

    public StageConditionals toWhen() {
        return new StageConditionals(actualList)
    }
}
