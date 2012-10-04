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

import java.util.List;

/**
 * Provides an interface to create scripts for aliases.
 */
public interface AliasCollector
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Adds the alias to the script.
   *
   * @param alias the alias to be added.
   */
  void addAliases(AliasGroup alias);

  /**
   * Sets the extension groups to the collector.
   *
   * @param extensionGroups the groups that map an alias extension to a group of
   *          extended aliases.
   */
  void setExtensionGroups(List<ExtensionGroup> extensionGroups);

  // --- object basics --------------------------------------------------------

}
