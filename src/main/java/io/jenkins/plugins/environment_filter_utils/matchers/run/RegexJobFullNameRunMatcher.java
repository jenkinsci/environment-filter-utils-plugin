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
import hudson.Util;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Run;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;
import org.kohsuke.stapler.verb.POST;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Restricted(NoExternalUse.class)
public class RegexJobFullNameRunMatcher implements RunMatcher {
    private String regex;

    @DataBoundConstructor
    public RegexJobFullNameRunMatcher() {
    }

    @DataBoundSetter
    public void setRegex(String regex) {
        this.regex = regex;
    }

    // used by Jelly view
    public String getRegex() {
        return regex;
    }

    @Override
    public boolean test(@Nonnull Run<?, ?> run) {
        if(regex == null){
            // should not happen but better safe than sorry
            return false;
        }
        String fullName = run.getParent().getFullName();
        return fullName.matches(regex);
    }

    @Extension
    @Symbol("jobNameRegex")
    public static final class DescriptorImpl extends Descriptor<RunMatcher> {
        @Override
        public @Nonnull String getDisplayName() {
            return Messages.RegexJobFullNameBuildMatcher_DisplayName();
        }

        /**
         * Checks if the regular expression is valid.
         */
        // used by Jelly view
        @POST
        public FormValidation doCheckRegex(@QueryParameter String value) {
            String v = Util.fixEmpty(value);
            if (v != null) {
                try {
                    Pattern.compile(v);
                } catch (PatternSyntaxException pse) {
                    // TODO would be nice if the formatting for a form validation response would be a core feature
                    return FormValidation.errorWithMarkup("<pre>" + Util.escape(pse.getMessage()) + "</pre>");
                }
            }
            return FormValidation.ok();
        }

        @RequirePOST
        public FormValidation doTestRegex(@QueryParameter String regex, @AncestorInPath Item context) {
            if (context == null) {
                if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                    return FormValidation.ok();
                }
            } else {
                if (!context.hasPermission(Item.CONFIGURE)) {
                    return FormValidation.ok();
                }
            }

            FormValidation check = doCheckRegex(regex);
            if (check.kind != FormValidation.Kind.OK) {
                return FormValidation.error(Messages.RegexJobFullNameBuildMatcher_Validation_Invalid());
            }

            final Pattern pattern = Pattern.compile(regex);

            List<String> matchingJobNames = new ArrayList<>();
            boolean excessMatches = false;

            for (Item item : Jenkins.get().allItems()) {
                final String fullName = item.getFullName();
                if (pattern.matcher(fullName).matches()) {
                    if (matchingJobNames.size() == 10) {
                        excessMatches = true;
                        break;
                    }
                    matchingJobNames.add(fullName);
                }
            }

            if (matchingJobNames.isEmpty()) {
                return FormValidation.warning(Messages.RegexJobFullNameBuildMatcher_Validation_NoJob());
            }

            if (excessMatches) {
                return FormValidation.okWithMarkup(Messages.RegexJobFullNameBuildMatcher_Validation_FoundMore(Util.xmlEscape(regex),
                                matchingJobNames.stream().map( it -> Messages.RegexJobFullNameBuildMatcher_Validation_FoundEntry(Util.xmlEscape(it))).collect(Collectors.joining())));
            }
            return FormValidation.okWithMarkup(Messages.RegexJobFullNameBuildMatcher_Validation_Found(Util.xmlEscape(regex),
                    matchingJobNames.stream().map( it -> Messages.RegexJobFullNameBuildMatcher_Validation_FoundEntry(Util.xmlEscape(it))).collect(Collectors.joining())));
        }
    }
}
