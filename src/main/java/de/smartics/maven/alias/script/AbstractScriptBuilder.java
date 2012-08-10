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

import java.util.ArrayList;
import java.util.List;

import de.smartics.maven.alias.domain.Alias;
import de.smartics.maven.alias.domain.AliasGroup;
import de.smartics.maven.alias.domain.ScriptBuilder;

/**
 * Base implementation common to all script builders.
 */
public abstract class AbstractScriptBuilder implements ScriptBuilder
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

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
   * The help key formatted according to {@link #maxAliasNameLength}.
   */
  protected String helpKey;

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

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param id the identifier of the script builder.
   * @param aliasHelpName the alias name to use to print help to the console.
   */
  public AbstractScriptBuilder(final String id, final String aliasHelpName)
  {
    this.id = id;
    this.aliasHelpName = aliasHelpName;
    maxAliasNameLength = aliasHelpName.length();
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public final String getId()
  {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  public final void setCommentIntro(final String commentIntro)
  {
    this.commentIntro = commentIntro;
  }

  /**
   * {@inheritDoc}
   */
  public final void setCommentExtro(final String commentExtro)
  {
    this.commentExtro = commentExtro;
  }

  /**
   * {@inheritDoc}
   */
  public final void setDocUrl(final String docUrl)
  {
    this.docUrl = docUrl;
  }

  /**
   * Returns the maximum length of a registered alias name.
   *
   * @return the maximum length of a registered alias name.
   */
  protected final int getMaxAliasNameLength()
  {
    return maxAliasNameLength;
  }

  /**
   * {@inheritDoc}
   */
  public final void setAddInstallationComment(
      final boolean addInstallationComment)
  {
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
  protected final boolean isAddInstallationComment()
  {
    return addInstallationComment;
  }

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public final void addAliases(final AliasGroup group)
  {
    final AliasGroup myGroup = group.filter(id);

    if (!myGroup.isEmpty())
    {
      for (final Alias alias : myGroup.getAliases())
      {
        final int length = alias.getName().length();
        if (length > maxAliasNameLength)
        {
          maxAliasNameLength = length;
        }
      }

      this.aliasGroups.add(group);
    }
  }

  // --- object basics --------------------------------------------------------

}
