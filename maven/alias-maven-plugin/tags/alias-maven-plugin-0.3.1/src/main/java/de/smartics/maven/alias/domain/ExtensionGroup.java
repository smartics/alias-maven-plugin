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

import java.util.ArrayList;
import java.util.List;

/**
 * A group of extended aliases.
 */
public final class ExtensionGroup
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The extension that has been applied.
   */
  private final AliasExtension extension;

  /**
   * The list of extended aliases.
   */
  private final List<Alias> aliases = new ArrayList<Alias>();

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param extension the extension that has been applied.
   * @throws NullPointerException if {@code extension} is <code>null</code>.
   */
  public ExtensionGroup(final AliasExtension extension)
    throws NullPointerException
  {
    if (extension == null)
    {
      throw new NullPointerException("'extension' must not be 'null'.");
    }
    this.extension = extension;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the list of extended aliases.
   *
   * @return the list of extended aliases.
   */
  public List<Alias> getAliases()
  {
    return aliases;
  }

  /**
   * Returns the extension that has been applied.
   *
   * @return the extension that has been applied.
   */
  public AliasExtension getExtension()
  {
    return extension;
  }

  // --- business -------------------------------------------------------------

  /**
   * Checks whether or not this group has aliases.
   *
   * @return <code>true</code> if the group contains at least one alias,
   *         <code>false</code> otherwise.
   */
  public boolean isEmpty()
  {
    return aliases.isEmpty();
  }

  /**
   * Adds the given alias to hte extension group if the extension is applicable
   * to the alias.
   *
   * @param group the name of the group the alias belongs to (may be
   *          <code>null</code>).
   * @param alias the alias to check for applicability.
   */
  public void addAlias(final String group, final Alias alias)
  {
    if (extension.isApplicable(group, alias))
    {
      final Alias extendedAlias = extension.apply(group, alias);
      aliases.add(extendedAlias);
    }
  }

  // --- object basics --------------------------------------------------------

}
