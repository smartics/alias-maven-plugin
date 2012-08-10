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
package de.smartics.maven.alias;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.xml.sax.InputSource;

import de.smartics.maven.alias.domain.AliasesProcessor;
import de.smartics.maven.alias.domain.ScriptBuilder;
import de.smartics.maven.alias.script.WindowsScriptBuilder;

/**
 * Creates an alias script based on the alias configuration.
 *
 * @goal alias
 * @phase process-resources
 * @description Creates an alias script based on the alias configuration.
 *              Supported scripts are: <tt>windows</tt>.
 * @author <a href="mailto:robert.reiner@smartics.de">Robert Reiner</a>
 * @version $Revision$
 */
public class MavenAliasMojo extends AbstractMojo
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * A simple flag to skip alias generation. If set on the command line use
   * <code>-Dalias.skip</code>.
   *
   * @parameter expression="${alias.skip}" default-value="false"
   * @since 1.0
   */
  private boolean skip;

  /**
   * A simple flag to log verbosely. If set on the command line use
   * <code>-Dalias.verbose</code>.
   *
   * @parameter expression="${alias.verbose}" default-value="false"
   * @since 1.0
   */
  private boolean verbose;

  /**
   * The location of the alias definitions. The contents of the file is required
   * to use the alias XSD.
   *
   * @parameter default-value="${basedir}/src/main/resources/alias.xml"
   * @required
   * @since 1.0
   */
  private String aliasLocation;

  /**
   * The location to write the alias scripts to.
   *
   * @parameter default-value="${project.build.directory}/alias-scripts"
   * @required
   * @since 1.0
   */
  private String scriptLocation;

  /**
   * The name of the alias to print the help on all defined aliases.
   *
   * @parameter default-value="h"
   * @required
   * @since 1.0
   */
  private String helpAlias;

  /**
   * The optional text to prepend to the generated script. No comment markers
   * allowed. This text will be written to the generated file as comments.
   *
   * @parameter
   * @since 1.0
   */
  private String intro;

  /**
   * If set to <code>true</code> instructs the script generator to add a comment
   * after the intro text that informs about the default installation of the
   * script. If set to <code>false</code> no information is added.
   *
   * @parameter default-value="true"
   * @since 1.0
   */
  private boolean addInstallationComment;

  /**
   * The optional text to append to the generated script. No comment markers
   * allowed. This text will be written to the generated file as comments.
   *
   * @parameter
   * @since 1.0
   */
  private String extro;

  /**
   * A URL to further documentation on this script. This may point to a page in
   * the developer team's wiki or a generated site in the project's
   * documentation.
   * <p>
   * The URL is presented to the user of the alias script if s/he requests the
   * help listing.
   * </p>
   *
   * @parameter
   * @since 1.0
   */
  private String docUrl;

  /**
   * The list of scripts to be generated. If not specified all supported script
   * types are generated.
   *
   * @parameter
   * @since 1.0
   */
  private String[] scripts;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.plugin.AbstractMojo#execute()
   */
  public void execute() throws MojoExecutionException, MojoFailureException
  {
    if (!skip)
    {
      final File scriptFolder = createScriptFolder();

      final ScriptBuilder[] builders = createBuilders();

      final InputSource source = createSource();
      final AliasesProcessor processor = createProcessor(source);
      processor.process(builders);
      logProcessingCompleted();

      for (final ScriptBuilder builder : builders)
      {
        final String script = builder.createScript();
        writeScript(scriptFolder, builder.getId(), script);
      }
    }
    else
    {
      getLog().info("Skipping alias plugin.");
    }
  }

  private void writeScript(final File scriptFolder, final String id,
      final String script) throws MojoExecutionException
  {
    final File scriptFile = new File(scriptFolder, id);
    try
    {
      final OutputStream out =
          new BufferedOutputStream(new FileOutputStream(scriptFile));
      try
      {
        IOUtils.write(script, out);
      }
      finally
      {
        IOUtils.closeQuietly(out);
      }
    }
    catch (final Exception e)
    {
      throw new MojoExecutionException("Cannot write script to '"
                                       + scriptFile.getAbsolutePath() + "'.", e);
    }
  }

  private AliasesProcessor createProcessor(final InputSource source)
    throws MojoExecutionException
  {
    try
    {
      return new AliasesProcessor(source);
    }
    catch (final Exception e)
    {
      throw new MojoExecutionException("Cannot read alias XML from '"
                                       + source.getSystemId() + "'.", e);
    }
  }

  private ScriptBuilder[] createBuilders()
  {
    if (scripts == null || scripts.length == 0)
    {
      return new ScriptBuilder[] { createWindowsScriptBuilder() };
    }

    final ScriptBuilder[] builders = new ScriptBuilder[scripts.length];
    int i = 0;
    for (final String script : scripts)
    {
      if (WindowsScriptBuilder.ID.equals(script))
      {
        builders[i] = createWindowsScriptBuilder();
        i++;
      }
    }

    return builders;
  }

  private WindowsScriptBuilder createWindowsScriptBuilder()
  {
    final WindowsScriptBuilder builder = new WindowsScriptBuilder(helpAlias);
    builder.setCommentIntro(intro);
    builder.setCommentExtro(extro);
    builder.setDocUrl(docUrl);
    builder.setAddInstallationComment(addInstallationComment);
    return builder;
  }

  private File createScriptFolder() throws MojoExecutionException
  {
    final File file = new File(scriptLocation);
    if (!file.exists() && !file.mkdirs())
    {
      throw new MojoExecutionException("Cannot create destination folder '"
                                       + scriptLocation + "'.");
    }
    return file;
  }

  private InputSource createSource() throws MojoExecutionException
  {
    final File file = new File(aliasLocation);
    try
    {
      final InputStream in = new BufferedInputStream(new FileInputStream(file));
      final InputSource source = new InputSource();
      source.setSystemId(aliasLocation);
      source.setByteStream(in);
      return source;
    }
    catch (final FileNotFoundException e)
    {
      throw new MojoExecutionException("Cannot read alias XML file '"
                                       + aliasLocation + "'.", e);
    }
  }

  private void logProcessingCompleted()
  {
    if (verbose)
    {
      getLog().info("Alias script generated successfully.");
    }
  }

  // --- object basics --------------------------------------------------------

}
