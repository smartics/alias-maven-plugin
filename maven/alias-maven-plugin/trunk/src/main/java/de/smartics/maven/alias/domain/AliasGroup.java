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
 * A group of aliases.
 */
public final class AliasGroup
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The name of this group.
   */
  private final String name;

  /**
   * The optional description of the group.
   */
  private final String comment;

  /**
   * The list of aliases belonging to this group.
   */
  private final List<Alias> aliases = new ArrayList<Alias>();

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param name the name of this group.
   * @param comment the optional description of the group.
   */
  public AliasGroup(final String name, final String comment)
  {
    this.name = name;
    this.comment = comment;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the name of this group.
   *
   * @return the name of this group.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the optional description of the group.
   *
   * @return the optional description of the group.
   */
  public String getComment()
  {
    return comment;
  }

  /**
   * Returns the list of aliases belonging to this group.
   *
   * @return the list of aliases belonging to this group.
   */
  public List<Alias> getAliases()
  {
    return aliases;
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
   * Creates a clone containing only aliases belonging to the given {@code env}.
   *
   * @param env the identifier of the environment to use for filtering.
   * @return a clone with aliases with no environment of an environment that
   *         matches {@code env}.
   */
  public AliasGroup filter(final String env)
  {
    final AliasGroup clone = new AliasGroup(name, comment);

    for (final Alias alias : aliases)
    {
      final String aliasEnv = alias.getEnv();
      if (aliasEnv == null || env.equals(aliasEnv))
      {
        clone.aliases.add(alias);
      }
    }

    return clone;
  }

  /**
   * Adds the given alias to this group.
   *
   * @param alias the alias to add. Must not be <code>null</code>.
   * @throws NullPointerException if {@code alias} is <code>null</code>.
   */
  public void addAlias(final Alias alias) throws NullPointerException
  {
    if (alias == null)
    {
      throw new NullPointerException("'alias' must not be 'null'.");
    }
    aliases.add(alias);
  }

  // --- object basics --------------------------------------------------------

}
