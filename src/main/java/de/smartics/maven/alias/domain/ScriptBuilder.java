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

/**
 * Provides an interface to create scripts for aliases.
 */
public interface ScriptBuilder extends AliasCollector
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- get&set --------------------------------------------------------------

  /**
   * The unique identifier of the script type the builder generated.
   *
   * @return the unique identifier of the script type the builder generated.
   */
  String getId();

  /**
   * Sets the optional introduction text to be rendered in the generated script
   * as a comment.
   *
   * @param intro the optional introduction text. May be <code>null</code> if no
   *          introduction is required.
   */
  void setCommentIntro(String intro);

  /**
   * Sets the optional text to be rendered at the end of the generated script as
   * a comment.
   *
   * @param extro the optional text rendered at the end. May be
   *          <code>null</code> if no text at the end is required.
   */
  void setCommentExtro(String extro);

  /**
   * Sets the URL to additional documention that is displayed in the help
   * listing of aliases.
   *
   * @param docUrl a URL to a documentation page for the user.
   */
  void setDocUrl(String docUrl);

  /**
   * Sets the installation comment flag. If set to <code>true</code> instructs
   * the script generator to add a comment after the intro text that informs
   * about the default installation of the script. If set to <code>false</code>
   * no information is added.
   *
   * @param addInstallationComment the installation comment flag.
   */
  void setAddInstallationComment(boolean addInstallationComment);

  // --- business -------------------------------------------------------------

  /**
   * Returns the script.
   *
   * @return the script.
   */
  String createScript();

  // --- object basics --------------------------------------------------------

}
