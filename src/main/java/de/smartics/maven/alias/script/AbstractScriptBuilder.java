/*
 * Copyright 2012-2024 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.smartics.maven.alias.script;

import de.smartics.maven.alias.domain.Alias;
import de.smartics.maven.alias.domain.AliasGroup;
import de.smartics.maven.alias.domain.ExtensionGroup;
import de.smartics.maven.alias.domain.ScriptBuilder;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation common to all script builders.
 */
public abstract class AbstractScriptBuilder implements ScriptBuilder {
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The variable that can be used in commands for a bell (^G or unicode 0007).
   * As XML 1.0 does not allow that character we use this marker.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  protected static final String BELL_VARIABLE = "{@bell}";

  /**
   * The bell value to use.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  protected static final String BELL_VALUE =
      /* CHECKSTYLE:OFF */"\u0007";/* CHECKSTYLE:ON */

  // --- members --------------------------------------------------------------

  /**
   * The identifier of the script builder.
   */
  protected final String id;

  /**
   * The list of aliases to add to the script.
   */
  protected final List<AliasGroup> aliasGroups = new ArrayList<AliasGroup>();

  /**
   * The alias name to use to print help to the console.
   */
  protected final String aliasHelpName;

  /**
   * The maximum length of a registered alias name.
   */
  private int maxAliasNameLength;

  /**
   * The optional introduction text to be rendered in the generated script as a
   * comment.
   */
  protected String commentIntro;

  /**
   * The installation comment flag. If set to <code>true</code> instructs the
   * script generator to add a comment after the intro text that informs about
   * the default installation of the script. If set to <code>false</code> no
   * information is added.
   */
  private boolean addInstallationComment;

  /**
   * The optional extroductional text to be rendered in the generated script as
   * a comment.
   */
  protected String commentExtro;

  /**
   * The URL to additional documention that is displayed in the help listing of
   * aliases.
   */
  protected String docUrl;

  /**
   * The collection of extension and their extended aliases.
   */
  protected final List<ExtensionGroup> extensionGroups =
      new ArrayList<ExtensionGroup>();

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param id the identifier of the script builder.
   * @param aliasHelpName the alias name to use to print help to the console.
   */
  public AbstractScriptBuilder(final String id, final String aliasHelpName) {
    this.id = id;
    this.aliasHelpName = aliasHelpName;
    maxAliasNameLength = aliasHelpName.length();
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  @Override
  public final String getId() {
    return id;
  }

  @Override
  public final void setCommentIntro(final String commentIntro) {
    this.commentIntro = commentIntro;
  }

  @Override
  public final void setCommentExtro(final String commentExtro) {
    this.commentExtro = commentExtro;
  }

  @Override
  public final void setDocUrl(final String docUrl) {
    this.docUrl = docUrl;
  }

  /**
   * Returns the maximum length of a registered alias name.
   *
   * @return the maximum length of a registered alias name.
   */
  protected final int getMaxAliasNameLength() {
    return maxAliasNameLength;
  }

  @Override
  public final void setAddInstallationComment(
      final boolean addInstallationComment) {
    this.addInstallationComment = addInstallationComment;
  }

  /**
   * Returns the installation comment flag. If set to <code>true</code>
   * instructs the script generator to add a comment after the intro text that
   * informs about the default installation of the script. If set to
   * <code>false</code> no information is added.
   *
   * @return the installation comment flag.
   */
  protected final boolean isAddInstallationComment() {
    return addInstallationComment;
  }

  @Override
  public final void setExtensionGroups(
      final List<ExtensionGroup> extensionGroups) {
    this.extensionGroups.addAll(extensionGroups);
  }

  // --- business -------------------------------------------------------------

  @Override
  public final void addAliases(final AliasGroup group) {
    final AliasGroup myGroup = group.filter(id);

    if (!myGroup.isEmpty()) {
      for (final Alias alias : myGroup.getAliases()) {
        final int length = alias.getName().length();
        if (length > maxAliasNameLength) {
          maxAliasNameLength = length;
        }
      }

      this.aliasGroups.add(group);
    }
  }

  /**
   * Appends extensions to the script.
   *
   * @param helpAlias the buffer for help messages.
   * @param script the script to append to.
   */
  protected final void appendExtensions(final StringBuilder helpAlias,
      final StringBuilder script) {
    // FIXME echo may not be correct for every script...
    final List<ExtensionGroup> filteredGroups = filter(extensionGroups);
    if (!filteredGroups.isEmpty()) {
      helpAlias.append("echo  --- ALIAS EXTENSIONS").append(getCommandDelim());

      for (final ExtensionGroup extension : filteredGroups) {
        helpAlias.append("echo  ...")
            .append(extension.getExtension().getName());

        final String mnemonic = extension.getExtension().getMnemonic();
        if (StringUtils.isNotBlank(mnemonic)) {
          helpAlias.append(" (").append(mnemonic).append("):");
        }

        for (final Alias alias : extension.getAliases()) {
          final String key =
              String.format("%1$-" + maxAliasNameLength + "s", alias.getName());
          appendAlias(script, alias, key);
          helpAlias.append(' ').append(alias.getName());
        }

        helpAlias.append(getCommandDelim()).append(' ');
      }
    }
  }

  // private static String normalizeComment(final String comment)
  // {
  // final String normComment = comment.trim().replaceAll("\\s+", " ");
  // return normComment;
  // }

  private List<ExtensionGroup> filter(
      final List<ExtensionGroup> extensionGroups) {
    final List<ExtensionGroup> filteredGroups =
        new ArrayList<ExtensionGroup>(extensionGroups.size());

    final String scriptEnv = getId();
    for (final ExtensionGroup group : extensionGroups) {
      final String env = group.getExtension().getEnv();
      if (env == null || ObjectUtils.equals(scriptEnv, env)) {
        filteredGroups.add(group);
      }
    }
    return filteredGroups;
  }

  /**
   * Returns the command delimiter.
   *
   * @return the command delimiter.
   */
  protected abstract Object getCommandDelim();

  /**
   * Appends the given alias to the script.
   *
   * @param script the script to append to.
   * @param alias the alias to append.
   * @param key the formatted key to the alias.
   */
  protected abstract void appendAlias(StringBuilder script, Alias alias,
      String key);

  // --- object basics --------------------------------------------------------

}
