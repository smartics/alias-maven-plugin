/*
 * Copyright 2012-2015 smartics, Kronseder & Reiner GmbH
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
package de.smartics.maven.alias.report;

import de.smartics.maven.alias.domain.AliasCollector;
import de.smartics.maven.alias.domain.AliasGroup;
import de.smartics.maven.alias.domain.ExtensionGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Collects alias information.
 */
public final class ReportAliasCollector
    implements AliasCollector, Iterable<AliasGroup> {
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The list of aliases to add to the script.
   */
  private final List<AliasGroup> aliasGroups = new ArrayList<AliasGroup>();

  /**
   * The collection of extension and their extended aliases.
   */
  private final List<ExtensionGroup> extensionGroups =
      new ArrayList<ExtensionGroup>();

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  public ReportAliasCollector() {}

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  @Override
  public void setExtensionGroups(final List<ExtensionGroup> extensionGroups) {
    this.extensionGroups.addAll(extensionGroups);
  }

  /**
   * Returns the list of extension groups.
   *
   * @return the list of extension groups.
   */
  public List<ExtensionGroup> getExtensionGroups() {
    return extensionGroups;
  }

  // --- business -------------------------------------------------------------

  @Override
  public void addAliases(final AliasGroup group) {
    if (!group.isEmpty()) {
      this.aliasGroups.add(group);
    }
  }

  /**
   * Returns an iterator over the collected alias groups.
   *
   * @return an iterator over the collected alias groups.
   */
  public Iterator<AliasGroup> iterator() {
    return aliasGroups.iterator();
  }

  // --- object basics --------------------------------------------------------

}
