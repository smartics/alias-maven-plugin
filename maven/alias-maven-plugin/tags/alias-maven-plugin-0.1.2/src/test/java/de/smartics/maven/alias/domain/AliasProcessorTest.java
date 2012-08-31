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
package de.smartics.maven.alias.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Tests {@link AliasProcessor}.
 */
public class AliasProcessorTest
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Inner Classes *****************************

  /**
   *
   */
  private static final String ALIAS_NAME = "i";

  /**
   *
   */
  private static final String ALIAS_COMMAND = "mvn -T 4 clean install";

  private static final class FakeBuilder implements ScriptBuilder
  {
    // ******************************** Fields ********************************

    // --- constants ----------------------------------------------------------

    // --- members ------------------------------------------------------------

    private final List<AliasGroup> aliasGroups = new ArrayList<AliasGroup>();

    // ***************************** Initializer ******************************

    // ***************************** Constructors *****************************

    // ***************************** Inner Classes ****************************

    // ******************************** Methods *******************************

    // --- init ---------------------------------------------------------------

    // --- get&set ------------------------------------------------------------

    public String getId()
    {
      return "test";
    }

    public void setCommentIntro(final String intro)
    {
      throw new UnsupportedOperationException();
    }

    public void setCommentExtro(final String extro)
    {
      throw new UnsupportedOperationException();
    }

    public void setDocUrl(final String docUrl)
    {
      throw new UnsupportedOperationException();
    }

    public void addAliases(final AliasGroup group)
    {
      aliasGroups.add(group);
    }

    public String createScript()
    {
      throw new UnsupportedOperationException();
    }

    public void setAddInstallationComment(final boolean addInstallationComment)
    {
      throw new UnsupportedOperationException();
    }

    // --- business -----------------------------------------------------------

    // --- object basics ------------------------------------------------------
  }

  // ********************************* Methods ********************************

  // --- prepare --------------------------------------------------------------

  // --- helper ---------------------------------------------------------------

  private static AliasesProcessor createUut(final String resourceId)
  {
    try
    {
      final URL url = AliasProcessorTest.class.getResource(resourceId);
      if (url == null)
      {
        throw new IllegalArgumentException("Resource '" + resourceId
                                           + "' not found.");
      }
      final InputStream in = new BufferedInputStream(url.openStream());
      final InputSource source = new InputSource(in);
      source.setSystemId(url.toExternalForm());
      final AliasesProcessor uut = new AliasesProcessor(source);
      return uut;
    }
    catch (final Exception e)
    {
      throw new IllegalArgumentException("Cannot open resource '" + resourceId
                                         + "'.", e);
    }
  }

  private static ScriptBuilder[] createBuilders()
  {
    return new ScriptBuilder[] { new FakeBuilder() };
  }

  private static List<Alias> getAliases(final ScriptBuilder[] builders)
  {
    return ((FakeBuilder) builders[0]).aliasGroups.get(0).getAliases();
  }

  // --- tests ----------------------------------------------------------------

  @Test
  public void readsCommentlessAliases()
  {
    final AliasesProcessor uut = createUut("alias-without-comment.xml");
    final ScriptBuilder[] builders = createBuilders();
    uut.process(builders);

    final List<Alias> aliases = getAliases(builders);
    assertEquals(1, aliases.size());
    final Alias alias = aliases.get(0);
    assertEquals(ALIAS_NAME, alias.getName());
    assertEquals(ALIAS_COMMAND, alias.getCommand());
    assertEquals(true, alias.isPassArgs());
    assertNull(alias.getComment());
  }

  @Test
  public void readsAliasesWithComments()
  {
    final AliasesProcessor uut = createUut("alias-with-comment.xml");
    final ScriptBuilder[] builders = createBuilders();
    uut.process(builders);

    final List<Alias> aliases = getAliases(builders);
    assertEquals(1, aliases.size());
    final Alias alias = aliases.get(0);
    assertEquals(ALIAS_NAME, alias.getName());
    assertEquals(ALIAS_COMMAND, alias.getCommand());
    assertEquals("Installs a project with Maven. Requires a valid Maven-POM.",
        alias.getComment());
  }

  @Test
  public void readsAliasesWithStructuredComments()
  {
    final AliasesProcessor uut = createUut("alias-with-structured-comment.xml");
    final ScriptBuilder[] builders = createBuilders();
    uut.process(builders);

    final List<Alias> aliases = getAliases(builders);
    assertEquals(1, aliases.size());
    final Alias alias = aliases.get(0);
    assertEquals(ALIAS_NAME, alias.getName());
    assertEquals(ALIAS_COMMAND, alias.getCommand());
    final String comment = alias.getComment();
    assertTrue(comment.contains("Installs a project with Maven."));
    assertTrue(comment.contains("<b>Requires</b>"));
  }

  @Test
  public void readsAliasWithEnv()
  {
    final AliasesProcessor uut = createUut("alias-with-env.xml");
    final ScriptBuilder[] builders = createBuilders();
    uut.process(builders);

    final List<Alias> aliases = getAliases(builders);
    assertEquals(1, aliases.size());
    final Alias alias = aliases.get(0);
    assertEquals("test", alias.getEnv());
  }

  @Test
  public void readsAliasWithPassArgs()
  {
    final AliasesProcessor uut = createUut("alias-with-pass.xml");
    final ScriptBuilder[] builders = createBuilders();
    uut.process(builders);

    final List<Alias> aliases = getAliases(builders);
    assertEquals(1, aliases.size());
    final Alias alias = aliases.get(0);
    assertEquals(false, alias.isPassArgs());
  }
}
