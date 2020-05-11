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
package io.jenkins.plugins.environment_filter_utils.matchers.descriptor.Multiple

import hudson.model.Descriptor
import io.jenkins.plugins.environment_filter_utils.matchers.descriptor.Multiple
import io.jenkins.plugins.environment_filter_utils.util.BuilderUtil

f = namespace(lib.FormTagLib)

Multiple self = (Multiple) instance;
def enabledList = self?.getDescriptorClassNames()?:null;

f.entry(title: _("className")) {

    table(width: "100%") {
        for (Descriptor descriptor : BuilderUtil.allDescriptors().sort({ o1, o2 -> o1.getDisplayName() <=> o2.getDisplayName() }) ) {
            String className = descriptor.getClass().getName()
            f.block {
                // check by default
                f.checkbox(name: "descriptorClassNames", title: descriptor.getDisplayName(), checked: enabledList == null || enabledList.contains(className), json: className)
            }
        }
    }
}
