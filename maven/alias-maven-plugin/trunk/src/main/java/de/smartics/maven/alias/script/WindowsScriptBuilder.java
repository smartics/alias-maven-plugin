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
 * Creates an alias script for Windows.
 */
public final class WindowsScriptBuilder extends AbstractScriptBuilder
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
  public static final String ID = "windows";

  /**
   * The newline sequence.
   */
  private static final String NEWLINE = "\r\n";

  /**
   * Command delimiter for Windows.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  private static final String COMMAND_DELIM = " ^& ";

  // --- members --------------------------------------------------------------

  /**
   * The help key formatted according to {@link #maxAliasNameLength}.
   */
  private String helpKey;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param aliasHelpName the alias name to use to print help to the console.
   */
  public WindowsScriptBuilder(final String aliasHelpName)
  {
    super(ID, aliasHelpName);
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public String createScript()
  {
    final int maxAliasNameLength = getMaxAliasNameLength();
    this.helpKey =
        String.format("%-" + maxAliasNameLength + 's', aliasHelpName);

    final StringBuilder helpAlias = createHelpAliasStringBuffer();
    final StringBuilder script = new StringBuilder(2048);

    script.append("@echo off").append(NEWLINE);
    appendAsComment(script, this.commentIntro);

    for (final AliasGroup group : aliasGroups)
    {
      helpAlias.append("echo  --- ").append(group.getName())
          .append(COMMAND_DELIM);
      for (final Alias alias : group.getAliases())
      {
        final String aliasEnv = alias.getEnv();
        if (aliasEnv == null || ID.equals(aliasEnv))
        {
          final String key =
              String.format("%1$-" + maxAliasNameLength + "s", alias.getName());
          appendAlias(script, alias, key);
          appendHelp(helpAlias, alias, key);
        }
      }
    }

    if (!aliasGroups.isEmpty())
    {
      helpAlias.append("echo  --- ").append("help").append(COMMAND_DELIM);
    }

    helpAlias.append("echo  ").append(helpKey).append(" = This help.");

    if (StringUtils.isNotBlank(docUrl))
    {
      helpAlias.append(COMMAND_DELIM)
          .append("echo For additional information please refer to: ")
          .append(docUrl);
    }

    script.append(helpAlias).append(NEWLINE);
    appendAsComment(script, this.commentExtro);

    script.append("@echo on").append(NEWLINE);
    return script.toString();
  }

  private StringBuilder createHelpAliasStringBuffer()
  {
    final StringBuilder helpAlias = new StringBuilder(1024);
    helpAlias.append("doskey ").append(helpKey).append(" = "); // NOPMD
    return helpAlias;
  }

  private void appendAlias(final StringBuilder script, final Alias alias,
      final String key)
  {
    script.append("doskey ").append(key).append(" = ")
        .append(alias.getCommand());
    if (alias.isPassArgs())
    {
      script.append(" $*");
    }
    script.append(NEWLINE);
  }

  private void appendHelp(final StringBuilder helpAlias, final Alias alias,
      final String key)
  {
    helpAlias.append("echo  ").append(key).append(" = ")
        .append(alias.getCommand());

    if (alias.isPassArgs())
    {
      helpAlias.append(" [args]");
    }

    helpAlias.append(COMMAND_DELIM);
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
      script.append("REM ").append(line).append(NEWLINE);
    }
  }

  // --- object basics --------------------------------------------------------

}
