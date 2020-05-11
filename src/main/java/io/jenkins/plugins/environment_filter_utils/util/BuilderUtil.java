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
package io.jenkins.plugins.environment_filter_utils.util;

import hudson.ExtensionList;
import hudson.model.Descriptor;
import jenkins.tasks.filters.EnvVarsFilterableBuilder;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import java.util.List;
import java.util.stream.Collectors;

@Restricted(NoExternalUse.class)
public class BuilderUtil {
    /**
     * Return all classes implementing {@link EnvVarsFilterableBuilder} that can be found using the Jenkins extension
     * mechanism. There might be others, so the result should be treated as incomplete.
     *
     * @return a list of classes implementing {@link EnvVarsFilterableBuilder}.
     */
    @SuppressWarnings("unchecked")
    public static List<Class<EnvVarsFilterableBuilder>> allBuilders() {
        return allDescriptors().stream().map(descriptor -> (Class<EnvVarsFilterableBuilder>) descriptor.clazz).collect(Collectors.toList());
    }

    /**
     * Return all descriptors for classes implemeneting {@link EnvVarsFilterableBuilder} that can be found using the
     * Jenkins extension mechanism. There might be others, so the result should be treated as incomplete.
     *
     * @return a list of descriptors
     */
    public static List<Descriptor> allDescriptors() {
        return ExtensionList.lookup(Descriptor.class).stream().filter(d -> d.isSubTypeOf(EnvVarsFilterableBuilder.class)).collect(Collectors.toList());
    }
}
