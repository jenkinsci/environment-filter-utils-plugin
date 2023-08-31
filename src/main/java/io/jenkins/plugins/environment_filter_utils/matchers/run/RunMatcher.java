/*
 * The MIT License
 *
 * Copyright (c) 2020, CloudBees, Inc.
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
 */
package io.jenkins.plugins.environment_filter_utils.matchers.run;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Run;
import java.io.Serializable;
import java.util.function.Predicate;
import jenkins.model.Jenkins;

/**
 * Utility class that allows matching a build.
 */
public interface RunMatcher extends Describable<RunMatcher>, Serializable, ExtensionPoint, Predicate<Run<?, ?>> {

    /**
     * All registered {@link RunMatcher}s.
     */
    static ExtensionList<RunMatcher> all() {
        return ExtensionList.lookup(RunMatcher.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    default Descriptor<RunMatcher> getDescriptor() {
        return Jenkins.get().getDescriptorOrDie(getClass());
    }
}
