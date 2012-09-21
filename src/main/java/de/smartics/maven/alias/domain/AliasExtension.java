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

import org.codehaus.plexus.util.StringUtils;

/**
 * Provides information about an extension of a number of aliases. An extension
 * extends an existing alias and adds it to the set of aliases in addition to
 * the original alias.
 */
public final class AliasExtension
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The place hoder in the template to paste in the original command string.
   */
  public static final String PLACEHOLDER = "{@cmd}";

  // --- members --------------------------------------------------------------

  /**
   * The name of the extension. The sequence of characters will be appended to
   * the extended alias name.
   */
  private final String name;

  /**
   * The template to apply to the selected aliases' commands.
   */
  private final String template;

  /**
   * The list of groups the extension is applied too.
   */
  private final List<String> applyToGroups;

  /**
   * The list of single aliases the extension is applied too.
   */
  private final List<String> applyToAliases;

  /**
   * The optional commend to be appended to the comment of the extended alias.
   */
  private final String comment;

  /**
   * The environment the extension is applied to.
   */
  private final String env;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  private AliasExtension(final Builder builder)
  {
    this.name = builder.name;
    this.template = builder.template;
    this.applyToGroups = new ArrayList<String>(builder.applyToGroups);
    this.applyToAliases = new ArrayList<String>(builder.applyToAliases);
    this.comment = builder.comment;
    this.env = builder.env;
  }

  // ****************************** Inner Classes *****************************

  /**
   * The builder for {@link AliasExtension} instances.
   */
  public static final class Builder
  {
    // ******************************** Fields ********************************

    // --- constants ----------------------------------------------------------

    // --- members ------------------------------------------------------------

    /**
     * The name of the extension. The sequence of characters will be appended to
     * the extended alias name.
     */
    private String name;

    /**
     * The template to apply to the selected aliases' commands.
     */
    private String template;

    /**
     * The list of groups the extension is applied too.
     */
    private final List<String> applyToGroups = new ArrayList<String>();

    /**
     * The list of single aliases the extension is applied too.
     */
    private final List<String> applyToAliases = new ArrayList<String>();

    /**
     * The optional commend to be appended to the comment of the extended alias.
     */
    private String comment;

    /**
     * The environment the extension is applied to.
     */
    private String env;

    // ***************************** Initializer ******************************

    // ***************************** Constructors *****************************

    // ***************************** Inner Classes ****************************

    // ******************************** Methods *******************************

    // --- init ---------------------------------------------------------------

    // --- get&set ------------------------------------------------------------

    /**
     * Sets the name of the extension. The sequence of characters will be
     * appended to the extended alias name.
     *
     * @param name the name of the extension.
     * @return a reference to this builder instance.
     */
    public Builder withName(final String name)
    {
      this.name = name;
      return this;
    }

    /**
     * Sets the template to apply to the selected aliases' commands.
     *
     * @param template the template to apply to the selected aliases' commands.
     * @return a reference to this builder instance.
     */
    public Builder withTemplate(final String template)
    {
      this.template = template;
      return this;
    }

    /**
     * Sets the optional commend to be appended to the comment of the extended
     * alias.
     *
     * @param comment the optional commend to be appended to the comment of the
     *          extended alias.
     * @return a reference to this builder instance.
     */
    public Builder withComment(final String comment)
    {
      this.comment = comment;
      return this;
    }

    /**
     * Sets the environment the extension is applied to.
     *
     * @param env the environment the extension is applied to.
     * @return a reference to this builder instance.
     */
    public Builder withEnv(final String env)
    {
      this.env = env;
      return this;
    }

    // --- business -----------------------------------------------------------

    /**
     * Adds the given group to the list of groups the extension is applied to.
     *
     * @param group the name of the group to be added.
     * @return a reference to this builder instance.
     */
    public Builder addGroup(final String group)
    {
      applyToGroups.add(group);
      return this;
    }

    /**
     * Adds the given alias to the list of aliases the extension is applied to.
     *
     * @param alias the name of the alias to be added.
     * @return a reference to this builder instance.
     */
    public Builder addAlias(final String alias)
    {
      applyToAliases.add(alias);
      return this;
    }

    /**
     * Creates an alias extension instance.
     *
     * @return the created instance.
     * @throws IllegalArgumentException if {@link #withName(String) name} or
     *           {@link #withTemplate(String) template} is blank (or both).
     */
    public AliasExtension build() throws IllegalArgumentException
    {
      final boolean templateIsBlank = StringUtils.isBlank(template);
      if (StringUtils.isBlank(name))
      {
        final String message =
            "Name is missing for extension"
                + (templateIsBlank ? " with template '" + template + '\'' : "")
                + '.';
        throw new IllegalArgumentException(message);
      }

      if (templateIsBlank)
      {
        final String message =
            "Template is missing for extension '" + name + "'.";
        throw new IllegalArgumentException(message);
      }

      return new AliasExtension(this);
    }

    // --- object basics ------------------------------------------------------
  }

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the name of the extension. The sequence of characters will be
   * appended to the extended alias name.
   *
   * @return the name of the extension.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the template to apply to the selected aliases' commands.
   *
   * @return the template to apply to the selected aliases' commands.
   */
  public String getTemplate()
  {
    return template;
  }

  /**
   * Returns the list of groups the extension is applied too.
   *
   * @return the list of groups the extension is applied too.
   */
  public List<String> getApplyToGroups()
  {
    return applyToGroups;
  }

  /**
   * Returns the list of single aliases the extension is applied too.
   *
   * @return the list of single aliases the extension is applied too.
   */
  public List<String> getApplyToAliases()
  {
    return applyToAliases;
  }

  /**
   * Returns the optional commend to be appended to the comment of the extended
   * alias.
   *
   * @return the optional commend to be appended to the comment of the extended
   *         alias.
   */
  public String getComment()
  {
    return comment;
  }

  /**
   * Returns the environment the extension is applied to.
   *
   * @return the environment the extension is applied to.
   */
  public String getEnv()
  {
    return env;
  }

  // --- business -------------------------------------------------------------

  /**
   * Checks whether the extension is applicable to the alias or not.
   *
   * @param group the name of the group the alias belongs to (may be
   *          <code>null</code>).
   * @param alias the alias to check for applicability.
   * @return <code>true</code> if the alias is to be extended,
   *         <code>false</code> otherwise.
   * @throws NullPointerException if {@code alias} is <code>null</code>.
   */
  public boolean isApplicable(final String group, final Alias alias)
    throws NullPointerException
  {
    if (alias == null)
    {
      throw new NullPointerException("Alias must not be 'null'");
    }

    return (group != null && applyToGroups.contains(group))
           || applyToAliases.contains(alias.getName());
  }

  /**
   * Applies the extension to the alias if applicable.
   *
   * @param group the name of the group the alias belongs to (may be
   *          <code>null</code>).
   * @param alias the alias to apply the extension to.
   * @return the extended alias or <code>null</code> if not applicable.
   * @throws NullPointerException if {@code alias} is <code>null</code>.
   * @see #isApplicable(String, Alias)
   */
  public Alias apply(final String group, final Alias alias)
    throws NullPointerException
  {
    if (isApplicable(group, alias))
    {
      final Alias.Builder builder = new Alias.Builder(alias);

      final String originalName = alias.getName();
      final String extensionName = originalName + name;

      final String originalCommand = alias.getCommand();
      final String extensionCommand =
          template.replace(PLACEHOLDER,
              originalCommand + (alias.isPassArgs() ? " {@args}" : ""));

      final String originalComment = alias.getComment();
      final String extensionComment =
          (originalComment != null ? (comment != null ? originalComment + ' '
                                                        + comment
              : originalComment) : null);
      builder.withName(extensionName).withCommand(extensionCommand)
          .withComment(extensionComment).withPassArgs(false).withEnv(env);

      final Alias extensionAlias = builder.build();
      return extensionAlias;
    }

    return null;
  }

  // --- object basics --------------------------------------------------------

}
