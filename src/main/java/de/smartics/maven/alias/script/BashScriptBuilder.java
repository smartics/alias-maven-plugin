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

import java.util.StringTokenizer;

import org.codehaus.plexus.util.StringUtils;

import de.smartics.maven.alias.domain.Alias;
import de.smartics.maven.alias.domain.AliasGroup;

/**
 * Creates an alias script for Linux Bash.
 */
public final class BashScriptBuilder extends AbstractScriptBuilder
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * Defines the identifier for this script builder. Only aliases not qualified
   * or defining this string as its environment are considered.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final String ID = "bash";

  /**
   * The newline sequence.
   */
  private static final String NEWLINE = "\n";

  /**
   * Command delimiter for Windows.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String COMMAND_DELIM = " && ";

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param aliasHelpName the alias name to use to print help to the console.
   */
  public BashScriptBuilder(final String aliasHelpName)
  {
    super(ID, aliasHelpName);
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  @Override
  protected Object getCommandDelim()
  {
    return COMMAND_DELIM;
  }

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public String createScript()
  {
    final int maxAliasNameLength = getMaxAliasNameLength();
    final String helpKey =
        String.format("%-" + maxAliasNameLength + 's', aliasHelpName);

    final StringBuilder helpAlias = createHelpAliasStringBuffer();
    final StringBuilder script = new StringBuilder(2048);

    script.append("#!/bin/bash").append(NEWLINE);
    appendAsComment(script, this.commentIntro);
    appendInstallationComment(script);

    for (final AliasGroup group : aliasGroups)
    {
      helpAlias.append("echo \" --- ").append(group.getName()).append('\"')
          .append(COMMAND_DELIM);
      for (final Alias alias : group.getAliases())
      {
        final String aliasEnv = alias.getEnv();
        if (aliasEnv == null || ID.equals(aliasEnv))
        {
          final String key =
              String.format("%1$-" + maxAliasNameLength + "s", alias.getName());
          appendAlias(script, alias);
          appendHelp(helpAlias, alias, key);
        }
      }
    }

    appendExtensions(helpAlias, script);

    if (!aliasGroups.isEmpty())
    {
      helpAlias.append("echo \" --- \"").append("help").append(COMMAND_DELIM);
    }

    helpAlias.append("echo \" ").append(helpKey).append(" = This help.\"");
    appendDocUrl(helpAlias);
    helpAlias.append('\''); // We have to close the remembered "'".

    script.append(helpAlias).append(NEWLINE);
    appendAsComment(script, this.commentExtro);

    return script.toString();
  }

  private void appendDocUrl(final StringBuilder helpAlias)
  {
    if (StringUtils.isNotBlank(docUrl))
    {
      helpAlias
          .append(COMMAND_DELIM)
          .append("echo -e \"For additional information please refer to: \\n  ")
          .append(docUrl).append('"');
    }
  }

  private void appendInstallationComment(final StringBuilder script)
  {
    if (isAddInstallationComment())
    {
      script
          .append("# ")
          .append(
              "Add the aliases to your ~/.bashrc or source it from ~/.bash_profile like this:")
          .append(NEWLINE)
          .append("#   source PATH_TO_THIS_FILE")
          .append(NEWLINE)
          .append("# For further information please refer to")
          .append(NEWLINE)
          .append(
              "#   http://tldp.org/LDP/Bash-Beginners-Guide/html/Bash-Beginners-Guide.html")
          .append(NEWLINE);
    }
  }

  private StringBuilder createHelpAliasStringBuffer()
  {
    final StringBuilder helpAlias = new StringBuilder(1024);
    helpAlias.append("alias ").append(aliasHelpName).append("='");
    // Unfortunately we have to remember to close the "'".
    return helpAlias;
  }

  private void appendAlias(final StringBuilder script, final Alias alias)
  {
    appendAlias(script, alias, null);
  }

  /**
   * {@inheritDoc}
   */
  protected void appendAlias(final StringBuilder script, final Alias alias,
      final String key)
  {
    // Bash always accepts appended arguments. So there is no check for pass.
    script.append("alias ").append(alias.getName()).append("='");

    // FIXME: Howto insert the command line args within the command?
    final String command = alias.getCommand().replace("{@args}", "");
    script.append(command).append('\'').append(NEWLINE);
  }

  private void appendHelp(final StringBuilder helpAlias, final Alias alias,
      final String key)
  {
    helpAlias.append("echo \" ").append(key).append(" = ")
        .append(alias.getCommand());

    if (alias.isPassArgs())
    {
      helpAlias.append(" [args]");
    }

    helpAlias.append('"').append(COMMAND_DELIM);
  }

  private static void appendAsComment(final StringBuilder script,
      final String text)
  {
    if (StringUtils.isBlank(text))
    {
      return;
    }

    final StringTokenizer tokenizer = new StringTokenizer(text, "\n");
    while (tokenizer.hasMoreTokens())
    {
      final String line = tokenizer.nextToken();
      script.append("# ").append(line).append(NEWLINE);
    }
  }

  // --- object basics --------------------------------------------------------

}
