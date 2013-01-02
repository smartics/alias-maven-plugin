/*
 * Copyright 2012-2013 smartics, Kronseder & Reiner GmbH
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

import org.codehaus.plexus.util.StringUtils;

/**
 * Provides alias information.
 */
public final class Alias
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The short name of the alias.
   */
  private final String name;

  /**
   * The command that is bound to the short name.
   */
  private final String command;

  /**
   * The optional comment to be used for reports that provides detailed
   * information about the alias and the context of its usage.
   */
  private final String comment;

  /**
   * The environment the alias is applied to.
   */
  private final String env;

  /**
   * The flag that allows arguments to be appended to the command.
   */
  private final boolean passArgs;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  private Alias(final Builder builder)
  {
    this.name = builder.name;
    this.command = builder.command;
    this.comment = builder.comment;

    this.passArgs = builder.passArgs;
    this.env = builder.env;
  }

  // ****************************** Inner Classes *****************************

  /**
   * The builder for {@link Alias} instances.
   */
  public static final class Builder
  {
    // ******************************** Fields ********************************

    // --- constants ----------------------------------------------------------

    // --- members ------------------------------------------------------------

    /**
     * The short name of the alias.
     */
    private String name;

    /**
     * The command that is bound to the short name.
     */
    private String command;

    /**
     * The optional comment to be used for reports that provides detailed
     * information about the alias and the context of its usage.
     */
    private String comment;

    /**
     * The environment the alias is applied to.
     */
    private String env;

    /**
     * The flag that allows arguments to be appended to the command.
     */
    private boolean passArgs = true;

    // ***************************** Initializer ******************************

    // ***************************** Constructors *****************************

    /**
     * Default constructor.
     *
     * @param alias
     */
    public Builder()
    {
    }

    /**
     * Copy constructor.
     *
     * @param alias the alias to initialize with.
     */
    public Builder(final Alias alias)
    {
      withName(alias.name).withCommand(alias.command)
          .withComment(alias.comment).withEnv(alias.env)
          .withPassArgs(alias.passArgs);
    }

    // ***************************** Inner Classes ****************************

    // ******************************** Methods *******************************

    // --- init ---------------------------------------------------------------

    // --- get&set ------------------------------------------------------------

    /**
     * Sets the short name of the alias.
     *
     * @param name the short name of the alias.
     * @return a reference to the builder.
     */
    public Builder withName(final String name)
    {
      this.name = name;
      return this;
    }

    /**
     * Sets the command that is bound to the short name.
     *
     * @param command the command that is bound to the short name.
     * @return a reference to the builder.
     */
    public Builder withCommand(final String command)
    {
      this.command = command;
      return this;
    }

    /**
     * Sets the optional comment to be used for reports that provides detailed
     * information about the alias and the context of its usage.
     *
     * @param comment the optional comment to be used for reports that provides
     *          detailed information about the alias and the context of its
     *          usage.
     * @return a reference to the builder.
     */
    public Builder withComment(final String comment)
    {
      this.comment = comment;
      return this;
    }

    /**
     * Sets the environment the alias is applied to.
     *
     * @param env the environment the alias is applied to.
     * @return a reference to the builder.
     */
    public Builder withEnv(final String env)
    {
      this.env = env;
      return this;
    }

    /**
     * Sets the flag that allows arguments to be appended to the command.
     *
     * @param passArgs the flag that allows arguments to be appended to the
     *          command.
     * @return a reference to the builder.
     */
    public Builder withPassArgs(final boolean passArgs)
    {
      this.passArgs = passArgs;
      return this;
    }

    // --- business -----------------------------------------------------------

    /**
     * Creates an alias instance.
     *
     * @return the requested instance build by this builders properties.
     * @throws IllegalArgumentException if {@link #withName(String) name} or
     *           {@link #withCommand(String) command} is blank (or both).
     */
    public Alias build() throws IllegalArgumentException
    {
      final boolean nameIsBlank = StringUtils.isBlank(name);
      final boolean commandIsBlank = StringUtils.isBlank(command);
      if (nameIsBlank && commandIsBlank)
      {
        final String message =
            "Alias name and command is required, but missing.";
        throw new IllegalArgumentException(message);
      }
      else if (nameIsBlank)
      {
        final String message =
            "Alias name is required for command '" + command
                + "', but missing.";
        throw new IllegalArgumentException(message);
      }
      else if (commandIsBlank)
      {
        final String message =
            "Alias command is required for name '" + name + "', but missing.";
        throw new IllegalArgumentException(message);
      }

      return new Alias(this);
    }

    // --- object basics ------------------------------------------------------
  }

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the short name of the alias.
   *
   * @return the short name of the alias.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the command that is bound to the short name.
   *
   * @return the command that is bound to the short name.
   */
  public String getCommand()
  {
    return command;
  }

  /**
   * Returns the optional comment to be used for reports that provides detailed
   * information about the alias and the context of its usage.
   *
   * @return the optional comment to be used for reports that provides detailed
   *         information about the alias and the context of its usage.
   */
  public String getComment()
  {
    return comment;
  }

  /**
   * Returns the environment the alias is applied to.
   *
   * @return the environment the alias is applied to.
   */
  public String getEnv()
  {
    return env;
  }

  /**
   * Returns the flag that allows arguments to be appended to the command.
   *
   * @return the flag that allows arguments to be appended to the command.
   */
  public boolean isPassArgs()
  {
    return passArgs;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

}
