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
package io.jenkins.plugins.environment_filter_utils.matchers;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import io.jenkins.plugins.environment_filter_utils.matchers.run.RegexJobFullNameRunMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class RegexJobFullNameRunMatcherTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();
    
    @Test
    public void testSingle() throws Exception {
        RegexJobFullNameRunMatcher matcher = new RegexJobFullNameRunMatcher();
        matcher.setRegex("job-[A-Z]");
        
        {// regular case
            FreeStyleProject project = j.createFreeStyleProject("job-A");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));

            assertThat(matcher.test(build), equalTo(true));
        }
        
        {// not matching directly
            FreeStyleProject project = j.createFreeStyleProject("job-2");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
            
            assertThat(matcher.test(build), equalTo(false));
        }
        
        {// contains information before
            FreeStyleProject project = j.createFreeStyleProject("somewords_before_job-A");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
            
            assertThat(matcher.test(build), equalTo(false));
        }
        
        {// contains information after
            FreeStyleProject project = j.createFreeStyleProject("job-AB");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
            
            assertThat(matcher.test(build), equalTo(false));
        }
    }
    
    @Test
    public void testWithinFolder() throws Exception {
        RegexJobFullNameRunMatcher matcherOnlyA = new RegexJobFullNameRunMatcher();
        matcherOnlyA.setRegex("folderA/.*");
        RegexJobFullNameRunMatcher matcherAnyFolder = new RegexJobFullNameRunMatcher();
        matcherAnyFolder.setRegex(".*/.*");
        RegexJobFullNameRunMatcher matcherDirectJob = new RegexJobFullNameRunMatcher();
        matcherDirectJob.setRegex("[^/]+");
    
        MockFolder folderA = j.createFolder("folderA");
        MockFolder folderB = j.createFolder("folderB");
    
        {// job inside the folder A
            FreeStyleProject project = folderA.createProject(FreeStyleProject.class, "job inside first folder");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
        
            assertThat(matcherOnlyA.test(build), equalTo(true));
            assertThat(matcherAnyFolder.test(build), equalTo(true));
            assertThat(matcherDirectJob.test(build), equalTo(false));
        }
    
        {// job inside the folder B
            FreeStyleProject project = folderB.createProject(FreeStyleProject.class, "job inside second folder");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
        
            assertThat(matcherOnlyA.test(build), equalTo(false));
            assertThat(matcherAnyFolder.test(build), equalTo(true));
            assertThat(matcherDirectJob.test(build), equalTo(false));
        }
    
        {// job at root level
            FreeStyleProject project = j.createFreeStyleProject("job root level");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
        
            assertThat(matcherOnlyA.test(build), equalTo(false));
            assertThat(matcherAnyFolder.test(build), equalTo(false));
            assertThat(matcherDirectJob.test(build), equalTo(true));
        }
    }
}