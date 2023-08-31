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
import io.jenkins.plugins.environment_filter_utils.matchers.run.ExactJobFullNameRunMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExactJobFullNameRunMatcherTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();
    
    @Test
    public void testSingle() throws Exception {
        ExactJobFullNameRunMatcher matcher = new ExactJobFullNameRunMatcher();
        matcher.setName("job-ok");
        
        {// regular case, perfect match
            FreeStyleProject project = j.createFreeStyleProject("job-ok");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
            
            assertThat(matcher.test(build), equalTo(true));
        }
        
        {// regular case, name does not match
            FreeStyleProject project = j.createFreeStyleProject("job-not-ok");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
            
            assertThat(matcher.test(build), equalTo(false));
        }
    }
    
    @Test
    public void testWithSpaceAndComma() throws Exception {
        ExactJobFullNameRunMatcher matcher = new ExactJobFullNameRunMatcher();
        matcher.setName("job with space, and comma");
    
        FreeStyleProject project = j.createFreeStyleProject("job with space, and comma");
        FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
    
        assertThat(matcher.test(build), equalTo(true));
    }
    
    @Test
    public void testWithinFolder() throws Exception {
        ExactJobFullNameRunMatcher matcherFolderA = new ExactJobFullNameRunMatcher();
        matcherFolderA.setName("folderA/job1");
        ExactJobFullNameRunMatcher matcherDirectJob = new ExactJobFullNameRunMatcher();
        matcherDirectJob.setName("job1");
        
        MockFolder folderA = j.createFolder("folderA");
        MockFolder folderB = j.createFolder("folderB");
        
        {// job inside the folder A
            FreeStyleProject project = folderA.createProject(FreeStyleProject.class, "job1");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
            
            assertThat(matcherFolderA.test(build), equalTo(true));
            assertThat(matcherDirectJob.test(build), equalTo(false));
        }
        
        {// job inside the folder B
            FreeStyleProject project = folderB.createProject(FreeStyleProject.class, "job2");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
            
            assertThat(matcherFolderA.test(build), equalTo(false));
            assertThat(matcherDirectJob.test(build), equalTo(false));
        }
        
        {// job inside the folder B
            FreeStyleProject project = j.createFreeStyleProject("job1");
            FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
            
            assertThat(matcherFolderA.test(build), equalTo(false));
            assertThat(matcherDirectJob.test(build), equalTo(true));
        }
    }
}