/*
 * Copyright 2012-2013 smartics, Kronseder & Reiner GmbH
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
public class AliasExtensionProcessorTest
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Inner Classes *****************************

  private static final class FakeBuilder implements ScriptBuilder
  {
    // ******************************** Fields ********************************

    // --- constants ----------------------------------------------------------

    // --- members ------------------------------------------------------------

    private final List<AliasGroup> aliasGroups = new ArrayList<AliasGroup>();

    /**
     * The collection of extension and their extended aliases.
     */
    private final List<ExtensionGroup> extensionGroups =
        new ArrayList<ExtensionGroup>();

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

    public void setExtensionGroups(final List<ExtensionGroup> extensionGroups)
    {
      this.extensionGroups.addAll(extensionGroups);
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
      final URL url = AliasExtensionProcessorTest.class.getResource(resourceId);
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
    final List<Alias> aliases = new ArrayList<Alias>();
    for (final AliasGroup group : ((FakeBuilder) builders[0]).aliasGroups)
    {
      aliases.addAll(group.getAliases());
    }

    return aliases;
  }

  private static List<ExtensionGroup> getExtensionGroups(final ScriptBuilder[] builders)
  {
    return ((FakeBuilder) builders[0]).extensionGroups;
  }

  // --- tests ----------------------------------------------------------------

  @Test
  public void appliesExtensions()
  {
    final AliasesProcessor uut = createUut("extension-example.xml");
    final ScriptBuilder[] builders = createBuilders();
    uut.process(builders);

    final List<Alias> aliases = getAliases(builders);
    assertEquals(3, aliases.size());
    final List<ExtensionGroup> extensionGroups = getExtensionGroups(builders);
    assertEquals(1, extensionGroups.size());
    final ExtensionGroup extensionGroup = extensionGroups.get(0);
    assertEquals(3, extensionGroup.getAliases().size());
  }
}
