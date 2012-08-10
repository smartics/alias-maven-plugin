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

import java.io.IOException;

import org.codehaus.plexus.util.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;

/**
 * Reads an alias XML file that follows the alias XSD and creates a shell
 * script.
 */
public final class AliasesProcessor
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The alias namespace.
   */
  private static final Namespace NS_ALIAS = Namespace
      .getNamespace("http://smartics.de/alias/1.0.0");

  // --- members --------------------------------------------------------------

  /**
   * The validated and parsed document.
   */
  private final Document doc;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param source the source to read the alias XML document from.
   * @throws NullPointerException if {@code source} is <code>null</code>.
   * @throws IOException if the XML document cannot be read.
   * @throws JDOMException if the XML document cannot be parsed.
   */
  public AliasesProcessor(final InputSource source)
    throws NullPointerException, IOException, JDOMException
  {
    if (source == null)
    {
      throw new NullPointerException("'source' must not be 'null'.");
    }

    final SAXBuilder sax = new SAXBuilder();// XMLReaders.XSDVALIDATING);
    this.doc = sax.build(source);
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Applies the alias information from the XML file to the given
   * {@code builders}.
   *
   * @param builders the builders to create alias scripts.
   */
  public void process(final ScriptBuilder... builders)
  {
    final Element root = doc.getRootElement();

    for (final Element groupElement : root.getChildren("group", NS_ALIAS))
    {
      final Attribute groupName = groupElement.getAttribute("name");

      final AliasGroup group = new AliasGroup(groupName.getValue());
      for (final Element aliasElement : groupElement.getChildren("alias",
          NS_ALIAS))
      {
        final Alias alias = createAlias(aliasElement);
        group.addAlias(alias);
      }

      for (final ScriptBuilder builder : builders)
      {
        builder.addAliases(group);
      }
    }
  }

  private Alias createAlias(final Element aliasElement)
  {
    final Alias.Builder builder = new Alias.Builder();
    final Attribute env = aliasElement.getAttribute("env");
    final Element command = aliasElement.getChild("command", NS_ALIAS);
    builder.withName(aliasElement.getChildTextNormalize("name", NS_ALIAS))
        .withCommand(command.getTextNormalize());

    final Element commentElement = aliasElement.getChild("comment", NS_ALIAS);
    if (commentElement != null)
    {
      final XMLOutputter xout = new XMLOutputter();
      final String comment = xout.outputElementContentString(commentElement);
      if (StringUtils.isNotBlank(comment))
      {
        builder.withComment(comment.trim());
      }
    }

    if (env != null)
    {
      builder.withEnv(env.getValue());
    }
    final Attribute args = command.getAttribute("passArgs");
    if (args != null && !Boolean.parseBoolean(args.getValue()))
    {
      builder.withPassArgs(false);
    }

    return builder.build();
  }

  // --- object basics --------------------------------------------------------

}
