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
import java.util.ArrayList;
import java.util.List;

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
   * The prefix of supported namespaces.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String SUPPORTED_NAMESPACE_PREFIX =
      "http://smartics.de/alias/1.";

  // --- members ---------------------------------------------------------------

  /**
   * The validated and parsed document.
   */
  private final Document doc;

  /**
   * The alias namespace.
   */
  private final Namespace nsAlias;

  /**
   * The collection of extension and their extended aliases.
   */
  private final List<ExtensionGroup> extensionGroups =
      new ArrayList<ExtensionGroup>();

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
    this.nsAlias = doc.getRootElement().getNamespace();
    final String uri = nsAlias.getURI();
    if (!uri.startsWith(SUPPORTED_NAMESPACE_PREFIX))
    {
      throw new JDOMException(
          "The namespace '" + nsAlias
              + "' is not supported. Namespace is required to start with '"
              + SUPPORTED_NAMESPACE_PREFIX + "'.");
    }
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
  public void process(final AliasCollector... builders)
  {
    final Element root = doc.getRootElement();

    for (final Element extensionElement : root
        .getChildren("extension", nsAlias))
    {
      final AliasExtension extension = createExtension(extensionElement);
      final ExtensionGroup extensionGroup = new ExtensionGroup(extension);
      extensionGroups.add(extensionGroup);
    }

    for (final Element groupElement : root.getChildren("group", nsAlias)) // NOPMD
    {
      final Attribute groupName = groupElement.getAttribute("name"); // NOPMD
      final String comment = readComment(groupElement);
      final AliasGroup group = new AliasGroup(groupName.getValue(), comment);
      for (final Element aliasElement : groupElement.getChildren("alias", // NOPMD
          nsAlias))
      {
        final Alias alias = createAlias(aliasElement);
        for (final ExtensionGroup extensionGroup : extensionGroups)
        {
          extensionGroup.addAlias(group.getName(), alias);
        }
        group.addAlias(alias);
      }

      for (final AliasCollector builder : builders)
      {
        builder.addAliases(group);
      }
    }

    for (final AliasCollector builder : builders)
    {
      builder.setExtensionGroups(extensionGroups);
    }
  }

  private AliasExtension createExtension(final Element extensionElement)
  {
    final AliasExtension.Builder builder = new AliasExtension.Builder();

    final Attribute env = extensionElement.getAttribute("env");
    if (env != null)
    {
      builder.withEnv(env.getValue());
    }

    final String name = extensionElement.getChildTextNormalize("name", nsAlias);
    final String template =
        extensionElement.getChildTextNormalize("template", nsAlias);
    final String comment = readComment(extensionElement);

    final Element commentElement =
        extensionElement.getChild("comment", nsAlias);
    if (commentElement != null)
    {
      final String mnemonic = commentElement.getAttributeValue("mnemonic");
      builder.withMnemonic(mnemonic);
    }

    builder.withName(name).withTemplate(template).withComment(comment);

    appendApplyTos(extensionElement, builder);

    return builder.build();
  }

  private void appendApplyTos(final Element extensionElement,
      final AliasExtension.Builder builder)
  {
    final Element applyToElement =
        extensionElement.getChild("apply-to", nsAlias);
    if (applyToElement != null)
    {
      for (final Element groupElement : applyToElement.getChildren("group",
          nsAlias))
      {
        final String group = groupElement.getTextNormalize();
        builder.addGroup(group);
      }

      for (final Element aliasElement : applyToElement.getChildren("alias",
          nsAlias))
      {
        final String alias = aliasElement.getTextNormalize();
        builder.addAlias(alias);
      }
    }
  }

  private Alias createAlias(final Element aliasElement)
  {
    final Alias.Builder builder = new Alias.Builder();
    final Attribute env = aliasElement.getAttribute("env");
    final Element command = aliasElement.getChild("command", nsAlias);
    final String normalizedCommandText = command.getTextNormalize();
    builder.withName(aliasElement.getChildTextNormalize("name", nsAlias))
        .withCommand(normalizedCommandText);

    final String comment = readComment(aliasElement);
    builder.withComment(comment);

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

  private String readComment(final Element root)
  {
    final Element commentElement = root.getChild("comment", nsAlias);
    if (commentElement != null)
    {
      final XMLOutputter xout = new XMLOutputter();
      final String comment = xout.outputElementContentString(commentElement);
      if (StringUtils.isNotBlank(comment))
      {
        return comment;
      }
    }
    return null;
  }

  // --- object basics --------------------------------------------------------

}
