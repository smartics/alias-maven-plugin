/*
 * Copyright 2012 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.smartics.maven.alias.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.smartics.maven.alias.domain.Alias;
import de.smartics.maven.alias.domain.AliasExtension;
import de.smartics.maven.alias.domain.AliasGroup;
import de.smartics.maven.alias.domain.ExtensionGroup;

/**
 * Tests {@link WindowsScriptBuilder}.
 */
public class WindowsScriptBuilderTest
{

  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   *
   */
  private static final String ALIAS_GROUP_NAME = "test";

  private static final String ALIAS_COMMAND = "command";

  private static final String ALIAS_NAME = "any";

  private static final String ECHO_ON = "@echo on\r\n";

  private static final String ECHO_OFF = "@echo off\r\n";

  private static final String NO_ALIAS_PRESENT =
      ECHO_OFF + "REM Some intro\r\n" + "REM Second intro Line\r\n"
          + "doskey h = echo  h = This help.\r\n" + "REM Some extro\r\n" // NOPMD
          + "REM Second extro Line\r\n" + ECHO_ON;

  // --- members --------------------------------------------------------------

  private WindowsScriptBuilder uut;

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- prepare --------------------------------------------------------------

  @Before
  public void setUp()
  {
    this.uut = new WindowsScriptBuilder("h");
    this.uut.setCommentIntro("Some intro\nSecond intro Line");
    this.uut.setCommentExtro("Some extro\nSecond extro Line");
  }

  // --- helper ---------------------------------------------------------------

  private static AliasGroup createAliasGroup(final Alias alias)
  {
    final AliasGroup group = new AliasGroup(ALIAS_GROUP_NAME, null);
    group.addAlias(alias);
    return group;
  }

  // --- tests ----------------------------------------------------------------

  @Test
  public void createsScriptWithoutAliases()
  {
    final String script = uut.createScript();
    assertEquals(NO_ALIAS_PRESENT, script);
  }

  @Test
  public void createsScriptWithoutIntro()
  {
    this.uut.setCommentIntro(null);
    final String script = uut.createScript();
    assertEquals(
        ECHO_OFF + "doskey h = echo  h = This help.\r\n" + "REM Some extro\r\n"
            + "REM Second extro Line\r\n" + ECHO_ON, script);
  }

  @Test
  public void createsScriptWithoutExtro()
  {
    this.uut.setCommentExtro(null);
    final String script = uut.createScript();
    assertEquals(ECHO_OFF + "REM Some intro\r\n" + "REM Second intro Line\r\n"
                 + "doskey h = echo  h = This help.\r\n" + ECHO_ON, script);
  }

  @Test
  public void createsScriptWithoutIntroAndExtro()
  {
    this.uut.setCommentIntro(null);
    this.uut.setCommentExtro(null);
    final String script = uut.createScript();
    assertEquals(ECHO_OFF + "doskey h = echo  h = This help.\r\n" + ECHO_ON,
        script);
  }

  @Test
  public void checksEnvironmentsAndRejectsAliasIfNoMatch()
  {
    final Alias alias =
        new Alias.Builder().withName(ALIAS_NAME).withCommand(ALIAS_COMMAND)
            .withEnv("linux").build();
    final AliasGroup group = createAliasGroup(alias);
    uut.addAliases(group);
    final String script = uut.createScript();
    assertEquals(NO_ALIAS_PRESENT, script);
  }

  @Test
  public void checksEnvironmentsAndAcceptsAliasIfMatch()
  {
    final Alias alias =
        new Alias.Builder().withName(ALIAS_NAME).withCommand(ALIAS_COMMAND)
            .withEnv("windows").build();
    final AliasGroup group = createAliasGroup(alias);
    uut.addAliases(group);
    final String script = uut.createScript();
    assertTrue(script.contains("doskey any = command $*"));
    assertTrue(script
        .contains("doskey h   = echo  --- test ^& echo  any = command [args]"
                  + " ^& echo  --- help ^& echo  h   = This help."));
  }

  @Test
  public void checksForNotPassingArgs()
  {
    final Alias alias =
        new Alias.Builder().withName(ALIAS_NAME).withCommand(ALIAS_COMMAND)
            .withPassArgs(false).build();
    final AliasGroup group = createAliasGroup(alias);
    uut.addAliases(group);
    final String script = uut.createScript();
    assertTrue(script.contains("doskey any = command"));
    assertTrue(script
        .contains("doskey h   = echo  --- test ^& echo  any = command ^& echo  --- help ^& echo  h   = This help."));
  }

  @Test
  public void appliesExtensions()
  {
    final Alias alias =
        new Alias.Builder().withName(ALIAS_NAME).withCommand(ALIAS_COMMAND)
            .withPassArgs(false).build();
    final AliasGroup group = createAliasGroup(alias);
    uut.addAliases(group);

    final List<ExtensionGroup> extensionGroups =
        new ArrayList<ExtensionGroup>();
    final AliasExtension extension =
        (new AliasExtension.Builder()).withName("xx")
            .withTemplate("xxx {@cmd} yyy").addGroup(ALIAS_GROUP_NAME).withComment("Does xxx and yyy.").build();
    final ExtensionGroup extensionGroup = new ExtensionGroup(extension);
    extensionGroup.addAlias(ALIAS_GROUP_NAME, alias);
    extensionGroups.add(extensionGroup);
    uut.setExtensionGroups(extensionGroups);

    final String expectedCommand = "doskey anyxx = xxx " + ALIAS_COMMAND + " yyy";
    final String script = uut.createScript();
    assertTrue(script.contains(expectedCommand));
  }
}
