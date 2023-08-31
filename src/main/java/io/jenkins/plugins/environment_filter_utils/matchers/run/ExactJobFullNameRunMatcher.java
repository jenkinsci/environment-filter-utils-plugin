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

import hudson.Extension;
import hudson.model.AutoCompletionCandidates;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Run;
import hudson.util.FormValidation;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

@Restricted(NoExternalUse.class)
public class ExactJobFullNameRunMatcher implements RunMatcher {
    private String name;

    @DataBoundConstructor
    public ExactJobFullNameRunMatcher() {}

    @DataBoundSetter
    public void setName(String name) {
        this.name = name;
    }

    // used by Jelly view
    public String getName() {
        return this.name;
    }

    @Override
    public boolean test(@Nonnull Run<?, ?> run) {
        String fullName = run.getParent().getFullName();
        return name.equals(fullName);
    }

    @Extension
    @Symbol("jobName")
    public static final class DescriptorImpl extends Descriptor<RunMatcher> {
        @Override
        public @Nonnull String getDisplayName() {
            return Messages.ExactJobFullNameBuildMatcher_DisplayName();
        }

        @POST
        public FormValidation doCheckName(@QueryParameter String value) {
            if (Jenkins.get().getItemByFullName(value) == null) {
                return FormValidation.warning(Messages.ExactJobFullNameBuildMatcher_Validation_NotFound(value));
            }
            return FormValidation.ok();
        }

        public AutoCompletionCandidates doAutoCompleteName(@QueryParameter String value) {
            return AutoCompletionCandidates.ofJobNames(Item.class, value, Jenkins.get());
        }
    }
}
